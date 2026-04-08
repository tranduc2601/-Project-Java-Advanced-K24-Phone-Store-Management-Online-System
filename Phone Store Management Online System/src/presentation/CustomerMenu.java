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

/**
 * Menu dành cho Customer - Mua hàng.
 */
public class CustomerMenu {
    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();
    private final CouponService couponService = new CouponService();
    private final FlashSaleService flashSaleService = new FlashSaleService();

    // Giỏ hàng trong bộ nhớ (reset khi đăng xuất)
    private final List<CartItem> cart = new ArrayList<>();

    public void show(Scanner scanner, User customer) {
        cart.clear(); // Reset giỏ khi vào menu

        while (true) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║       🛒 MENU KHÁCH HÀNG (CUSTOMER)         ║");
            System.out.println("║    Xin chào, " + padRight(customer.getFullName(), 30) + " ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1. 📱 Xem sản phẩm còn hàng                ║");
            System.out.println("║  2. 🛒 Thêm sản phẩm vào giỏ hàng          ║");
            System.out.println("║  3. 📋 Xem giỏ hàng                         ║");
            System.out.println("║  4. ✅ Xác nhận đặt hàng (Checkout)         ║");
            System.out.println("║  5. 📜 Xem lịch sử đơn hàng                ║");
            System.out.println("║  6. 🔍 Tìm kiếm sản phẩm                   ║");
            System.out.println("║  0. 🔓 Đăng xuất                            ║");
            System.out.println("╚══════════════════════════════════════════════╝");

            int choice = InputValidator.readInt(scanner, "👉 Chọn chức năng: ");

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
                        System.out.println("🔓 Đã đăng xuất.");
                        return;
                    default:
                        System.out.println("⚠️ Lựa chọn không hợp lệ!");
                }
            } catch (Exception e) {
                System.out.println("❌ Đã xảy ra lỗi: " + e.getMessage());
            }
        }
    }

    /**
     * Hiển thị sản phẩm còn hàng (bao gồm Flash Sale nếu có)
     */
    private void browseProducts() {
        TablePrinter.printHeader("SẢN PHẨM CÒN HÀNG");
        List<Product> products = productService.getInStockProducts();

        if (products.isEmpty()) {
            System.out.println("📭 Hiện tại không có sản phẩm nào còn hàng.");
            return;
        }

        // Hiển thị có gắn thông tin Flash Sale
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
                salePrice = "🔥-" + sale.getDiscountPercent() + "% " + TablePrinter.formatCurrency(discounted);
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
        System.out.println("Tổng: " + products.size() + " sản phẩm còn hàng");
    }

    /**
     * Thêm sản phẩm vào giỏ hàng (kiểm tra stock)
     */
    private void addToCart(Scanner scanner) {
        browseProducts();
        System.out.println();
        int productId = InputValidator.readInt(scanner, "Nhập ID sản phẩm muốn mua: ");

        Product product = productService.getProductById(productId);
        if (product == null || !product.isStatus()) {
            System.out.println("⚠️ Sản phẩm không tồn tại hoặc đã ngừng kinh doanh!");
            return;
        }
        if (product.getStock() <= 0) {
            System.out.println("⚠️ Sản phẩm đã hết hàng!");
            return;
        }

        int quantity = InputValidator.readInt(scanner, "Số lượng muốn mua (tồn kho: " + product.getStock() + "): ");
        if (quantity <= 0) {
            System.out.println("⚠️ Số lượng phải lớn hơn 0!");
            return;
        }

        // Kiểm tra số lượng mua so với tồn kho (bao gồm cả số đã có trong giỏ)
        int alreadyInCart = 0;
        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                alreadyInCart = item.getQuantity();
                break;
            }
        }

        if (quantity + alreadyInCart > product.getStock()) {
            System.out.println("⚠️ Không đủ hàng! Tồn kho: " + product.getStock()
                    + ", đã trong giỏ: " + alreadyInCart
                    + ", bạn muốn thêm: " + quantity);
            return;
        }

        // Tính giá bán (áp dụng Flash Sale nếu có)
        BigDecimal unitPrice = product.getPrice();
        FlashSale sale = flashSaleService.getActiveFlashSaleForProduct(productId);
        if (sale != null) {
            unitPrice = product.getPrice()
                    .multiply(BigDecimal.valueOf(100 - sale.getDiscountPercent()))
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            System.out.println("🔥 Flash Sale -" + sale.getDiscountPercent() + "% áp dụng!");
        }

        // Kiểm tra xem đã có trong giỏ chưa → cộng dồn
        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                item.setQuantity(item.getQuantity() + quantity);
                item.setUnitPrice(unitPrice); // cập nhật giá mới nhất
                found = true;
                break;
            }
        }
        if (!found) {
            cart.add(new CartItem(productId, product.getProductName(), quantity, unitPrice));
        }

        System.out.println("✅ Đã thêm " + quantity + "x " + product.getProductName() + " vào giỏ hàng!");
        System.out.println("🛒 Giỏ hàng hiện có " + cart.size() + " mặt hàng.");
    }

    /**
     * Hiển thị giỏ hàng
     */
    private void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("🛒 Giỏ hàng trống!");
            return;
        }

        System.out.println("\n🛒 GIỎ HÀNG CỦA BẠN:");
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
        System.out.println("💰 TỔNG CỘNG: " + TablePrinter.formatCurrency(total));
    }

    /**
     * Xác nhận đặt hàng (Checkout)
     */
    private void checkout(Scanner scanner, User customer) {
        if (cart.isEmpty()) {
            System.out.println("🛒 Giỏ hàng trống! Hãy thêm sản phẩm trước khi đặt hàng.");
            return;
        }

        viewCart();
        System.out.println();

        String address = InputValidator.readNonEmptyString(scanner, "📍 Nhập địa chỉ giao hàng: ");

        // Hỏi mã giảm giá
        Coupon coupon = null;
        System.out.print("🎫 Nhập mã giảm giá (Enter để bỏ qua): ");
        String couponCode = scanner.nextLine().trim();
        if (!couponCode.isEmpty()) {
            coupon = couponService.validateAndGet(couponCode);
            if (coupon != null) {
                System.out.println("✅ Mã giảm giá hợp lệ: -" + coupon.getDiscountPercent() + "%");
            }
        }

        // Xác nhận
        if (!InputValidator.confirmYesNo(scanner, "Bạn có chắc chắn muốn đặt hàng?")) {
            System.out.println("↩️ Đã hủy đặt hàng.");
            return;
        }

        // Thực hiện checkout (Transaction)
        if (orderService.checkout(customer.getUserId(), cart, address, coupon)) {
            cart.clear(); // Xóa giỏ hàng sau khi đặt thành công
        }
    }

    /**
     * Xem lịch sử đơn hàng + theo dõi trạng thái
     */
    private void viewOrderHistory(Scanner scanner, User customer) {
        List<Order> orders = orderService.getOrdersByUserId(customer.getUserId());
        TablePrinter.printHeader("LỊCH SỬ ĐƠN HÀNG CỦA BẠN");
        TablePrinter.printOrderTable(orders);

        if (orders.isEmpty()) return;

        System.out.print("\nNhập mã đơn hàng để xem chi tiết (0 để quay lại): ");
        String input = scanner.nextLine().trim();
        if ("0".equals(input)) return;

        try {
            int orderId = Integer.parseInt(input);
            Order order = orderService.getOrderById(orderId);
            if (order == null || order.getUserId() != customer.getUserId()) {
                System.out.println("⚠️ Không tìm thấy đơn hàng!");
                return;
            }

            System.out.println("\n📋 CHI TIẾT ĐƠN HÀNG #" + orderId);
            System.out.println("   Ngày đặt: " + order.getOrderDate());
            System.out.println("   Địa chỉ giao: " + order.getDeliveryAddress());
            System.out.println("   Trạng thái: " + getStatusEmoji(order.getOrderStatus()) + " " + order.getOrderStatus());
            System.out.println("   Tổng tiền: " + TablePrinter.formatCurrency(order.getTotalAmount()));

            List<OrderDetail> details = orderService.getOrderDetails(orderId);
            System.out.println("\n   Sản phẩm:");
            for (OrderDetail d : details) {
                System.out.println("   - " + (d.getProductName() != null ? d.getProductName() : "SP #" + d.getProductId())
                        + " x" + d.getQuantity()
                        + " @ " + TablePrinter.formatCurrency(d.getUnitPrice()));
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Mã đơn hàng không hợp lệ!");
        }
    }

    private void searchProducts(Scanner scanner) {
        String keyword = InputValidator.readNonEmptyString(scanner, "🔍 Nhập từ khóa tìm kiếm: ");
        List<Product> results = productService.searchByName(keyword);
        TablePrinter.printHeader("KẾT QUẢ TÌM KIẾM: \"" + keyword + "\"");
        TablePrinter.printProductTable(results);
    }

    private String getStatusEmoji(String status) {
        switch (status) {
            case "PENDING": return "⏳";
            case "SHIPPING": return "🚚";
            case "DELIVERED": return "✅";
            case "CANCELLED": return "❌";
            default: return "❓";
        }
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        return String.format("%-" + n + "s", s.length() > n ? s.substring(0, n) : s);
    }
}

