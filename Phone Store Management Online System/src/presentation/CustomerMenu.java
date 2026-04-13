package presentation;

import model.*;
import service.*;
import util.InputValidator;
import util.TablePrinter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomerMenu {
    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();
    private final CouponService couponService = new CouponService();
    private final FlashSaleService flashSaleService = new FlashSaleService();

    private final List<CartItem> cart = new ArrayList<>();

    public void show(Scanner scanner, User customer) {
        cart.clear();

        while (true) {
            System.out.println();
            System.out.println("MENU KHACH HANG");
            System.out.println("Xin chao, " + customer.getFullName());
            System.out.println("1. Xem san pham con hang");
            System.out.println("2. Them san pham vao gio");
            System.out.println("3. Xem gio hang");
            System.out.println("4. Dat hang");
            System.out.println("5. Lich su don hang");
            System.out.println("6. Tim kiem san pham");
            System.out.println("0. Dang xuat");

            int choice = InputValidator.readInt(scanner, "Chon: ");

            try {
                switch (choice) {
                    case 1: browseProducts(); break;
                    case 2: addToCart(scanner); break;
                    case 3: viewCart(); break;
                    case 4: checkout(scanner, customer); break;
                    case 5: viewOrderHistory(scanner, customer); break;
                    case 6: searchProducts(scanner); break;
                    case 0:
                        cart.clear();
                        System.out.println("Da dang xuat.");
                        return;
                    default:
                        System.out.println("Lua chon khong hop le!");
                }
            } catch (Exception e) {
                System.out.println("Da xay ra loi: " + e.getMessage());
            }
        }
    }

    private void browseProducts() {
        TablePrinter.printHeader("SẢN PHẨM CÒN HÀNG");
        List<Product> products = productService.getInStockProducts();

        if (products.isEmpty()) {
            System.out.println("Hien tai khong co san pham nao con hang.");
            return;
        }

        String format = "| %-4s | %-25s | %-15s | %-15s | %-5s | %-10s | %-8s |%n";
        String line = "+" + "-".repeat(6) + "+" + "-".repeat(27) + "+" + "-".repeat(17) + "+"
                + "-".repeat(17) + "+" + "-".repeat(7) + "+" + "-".repeat(12) + "+" + "-".repeat(10) + "+";

        System.out.println(line);
        System.out.printf(format, "ID", "Tên sản phẩm", "Giá gốc", "Giá bán", "Kho", "Màu", "Bộ nhớ");
        System.out.println(line);

        for (Product p : products) {
            FlashSale sale = flashSaleService.getActiveFlashSaleForProduct(p.getProductId());
            String originalPrice = TablePrinter.formatCurrency(p.getPrice());
            String salePrice;

            if (sale != null) {
                BigDecimal discounted = p.getPrice()
                        .multiply(BigDecimal.valueOf(100 - sale.getDiscountPercent()))
                        .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                salePrice = "-" + sale.getDiscountPercent() + "% " + TablePrinter.formatCurrency(discounted);
            } else {
                salePrice = originalPrice;
            }

            String name = p.getProductName();
            if (name.length() > 25) name = name.substring(0, 22) + "...";

            System.out.printf(format,
                    p.getProductId(), name, originalPrice,
                    salePrice.length() > 15 ? salePrice.substring(0, 15) : salePrice,
                    p.getStock(),
                    p.getColor() != null ? (p.getColor().length() > 10 ? p.getColor().substring(0, 7) + "..." : p.getColor()) : "",
                    p.getStorageCapacity() != null ? p.getStorageCapacity() : ""
            );
        }
        System.out.println(line);
        System.out.println("Tong: " + products.size() + " san pham con hang");
    }

    private void addToCart(Scanner scanner) {
        browseProducts();
        System.out.println();
        int productId = InputValidator.readInt(scanner, "Nhap ID san pham muon mua: ");

        Product product = productService.getProductById(productId);
        if (product == null || !product.isStatus()) {
            System.out.println("San pham khong ton tai hoac da ngung kinh doanh!");
            return;
        }
        if (product.getStock() <= 0) {
            System.out.println("San pham da het hang!");
            return;
        }

        int quantity = InputValidator.readInt(scanner, "So luong muon mua (ton kho: " + product.getStock() + "): ");
        if (quantity <= 0) {
            System.out.println("So luong phai lon hon 0!");
            return;
        }

        int alreadyInCart = 0;
        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                alreadyInCart = item.getQuantity();
                break;
            }
        }

        if (quantity + alreadyInCart > product.getStock()) {
            System.out.println("Khong du hang! Ton kho: " + product.getStock()
                    + ", da trong gio: " + alreadyInCart
                    + ", ban muon them: " + quantity);
            return;
        }

        BigDecimal unitPrice = product.getPrice();
        FlashSale sale = flashSaleService.getActiveFlashSaleForProduct(productId);
        if (sale != null) {
            unitPrice = product.getPrice()
                    .multiply(BigDecimal.valueOf(100 - sale.getDiscountPercent()))
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            System.out.println("Flash Sale -" + sale.getDiscountPercent() + "% ap dung!");
        }

        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                item.setQuantity(item.getQuantity() + quantity);
                item.setUnitPrice(unitPrice);
                found = true;
                break;
            }
        }
        if (!found) {
            cart.add(new CartItem(productId, product.getProductName(), quantity, unitPrice));
        }

        System.out.println("Da them " + quantity + "x " + product.getProductName() + " vao gio hang!");
        System.out.println("Gio hang hien co " + cart.size() + " mat hang.");
    }

    private void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("Gio hang trong!");
            return;
        }

        System.out.println("\nGIỎ HÀNG CỦA BẠN:");
        String format = "| %-4s | %-25s | %-6s | %-15s | %-17s |%n";
        String line = "+" + "-".repeat(6) + "+" + "-".repeat(27) + "+" + "-".repeat(8) + "+"
                + "-".repeat(17) + "+" + "-".repeat(19) + "+";

        System.out.println(line);
        System.out.printf(format, "STT", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền");
        System.out.println(line);

        BigDecimal total = BigDecimal.ZERO;
        int stt = 1;
        for (CartItem item : cart) {
            BigDecimal subTotal = item.getSubTotal();
            total = total.add(subTotal);

            String name = item.getProductName();
            if (name.length() > 25) name = name.substring(0, 22) + "...";

            System.out.printf(format,
                    stt++, name, item.getQuantity(),
                    TablePrinter.formatCurrency(item.getUnitPrice()),
                    TablePrinter.formatCurrency(subTotal)
            );
        }
        System.out.println(line);
        System.out.println("TỔNG CỘNG: " + TablePrinter.formatCurrency(total));
    }

    private void checkout(Scanner scanner, User customer) {
        if (cart.isEmpty()) {
            System.out.println("Gio hang trong! Hay them san pham truoc khi dat hang.");
            return;
        }

        viewCart();
        System.out.println();

        String address = InputValidator.readNonEmptyString(scanner, "Nhap dia chi giao hang: ");

        Coupon coupon = null;
        System.out.print("Nhap ma giam gia (Enter de bo qua): ");
        String couponCode = scanner.nextLine().trim();
        if (!couponCode.isEmpty()) {
            coupon = couponService.validateAndGet(couponCode);
            if (coupon != null) {
                System.out.println("Ma giam gia hop le: -" + coupon.getDiscountPercent() + "%");
            }
        }

        if (!InputValidator.confirmYesNo(scanner, "Ban co chac chan muon dat hang?")) {
            System.out.println("Da huy dat hang.");
            return;
        }

        if (orderService.checkout(customer.getUserId(), cart, address, coupon)) {
            cart.clear();
        }
    }

    private void viewOrderHistory(Scanner scanner, User customer) {
        List<Order> orders = orderService.getOrdersByUserId(customer.getUserId());
        TablePrinter.printHeader("LỊCH SỬ ĐƠN HÀNG CỦA BẠN");
        TablePrinter.printOrderTable(orders);

        if (orders.isEmpty()) return;

        System.out.print("\nNhap ma don hang de xem chi tiet (0 de quay lai): ");
        String input = scanner.nextLine().trim();
        if ("0".equals(input)) return;

        try {
            int orderId = Integer.parseInt(input);
            Order order = orderService.getOrderById(orderId);
            if (order == null || order.getUserId() != customer.getUserId()) {
                System.out.println("Khong tim thay don hang!");
                return;
            }

            System.out.println("\nCHI TIẾT ĐƠN HÀNG #" + orderId);
            System.out.println("   Ngay dat: " + order.getOrderDate());
            System.out.println("   Dia chi giao: " + order.getDeliveryAddress());
            System.out.println("   Trang thai: " + order.getOrderStatus());
            System.out.println("   Tong tien: " + TablePrinter.formatCurrency(order.getTotalAmount()));

            List<OrderDetail> details = orderService.getOrderDetails(orderId);
            System.out.println("\n   San pham:");
            for (OrderDetail d : details) {
                System.out.println("   - " + (d.getProductName() != null ? d.getProductName() : "SP #" + d.getProductId())
                        + " x" + d.getQuantity()
                        + " @ " + TablePrinter.formatCurrency(d.getUnitPrice()));
            }
        } catch (NumberFormatException e) {
            System.out.println("Ma don hang khong hop le!");
        }
    }

    private void searchProducts(Scanner scanner) {
        String keyword = InputValidator.readNonEmptyString(scanner, "Nhap tu khoa tim kiem: ");
        List<Product> results = productService.searchByName(keyword);
        TablePrinter.printHeader("KẾT QUẢ TÌM KIẾM: \"" + keyword + "\"");
        TablePrinter.printProductTable(results);
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        return String.format("%-" + n + "s", s.length() > n ? s.substring(0, n) : s);
    }
}
