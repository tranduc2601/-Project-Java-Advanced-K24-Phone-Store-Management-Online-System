package presentation;

import model.*;
import service.*;
import util.InputValidator;
import util.TablePrinter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final CategoryService categoryService = new CategoryService();
    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();

    public void show(Scanner scanner, User admin) {
        while (true) {
            System.out.println();
            System.out.println("MENU ADMIN");
            System.out.println("1. Quan ly Danh muc");
            System.out.println("2. Quan ly San pham");
            System.out.println("3. Quan ly Don hang");
            System.out.println("0. Dang xuat");

            int choice = InputValidator.readInt(scanner, "Chon: ");
            try {
                switch (choice) {
                    case 1: categoryMenu(scanner); break;
                    case 2: productMenu(scanner); break;
                    case 3: orderMenu(scanner); break;
                    case 0:
                        System.out.println("Da dang xuat.");
                        return;
                    default:
                        System.out.println("Lua chon khong hop le.");
                }
            } catch (Exception e) {
                System.out.println("Da xay ra loi: " + e.getMessage());
            }
        }
    }

    // DANH MUC

    private void categoryMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- QUAN LY DANH MUC ---");
            System.out.println("1. Xem danh sach danh muc");
            System.out.println("2. Them danh muc moi");
            System.out.println("3. Sua danh muc");
            System.out.println("4. Xoa mem danh muc");
            System.out.println("5. Khoi phuc danh muc");
            System.out.println("0. Quay lai");

            int choice = InputValidator.readInt(scanner, "Chon: ");
            switch (choice) {
                case 1:
                    TablePrinter.printHeader("DANH SACH DANH MUC");
                    TablePrinter.printCategoryTable(categoryService.getAllCategories());
                    break;
                case 2: addCategory(scanner); break;
                case 3: editCategory(scanner); break;
                case 4: deleteCategory(scanner); break;
                case 5: restoreCategory(scanner); break;
                case 0: return;
                default: System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private void addCategory(Scanner scanner) {
        System.out.println("\n--- THEM DANH MUC MOI ---");
        String name = InputValidator.readNonEmptyString(scanner, "Ten danh muc: ");
        System.out.print("Mo ta: ");
        String desc = scanner.nextLine().trim();
        if (categoryService.addCategory(name, desc)) {
            System.out.println("Them danh muc thanh cong!");
        } else {
            System.out.println("Them danh muc that bai!");
        }
    }

    private void editCategory(Scanner scanner) {
        System.out.println("\n--- SUA DANH MUC ---");
        TablePrinter.printCategoryTable(categoryService.getAllCategories());
        int id = InputValidator.readInt(scanner, "Nhap ID danh muc can sua: ");
        Category cat = categoryService.getCategoryById(id);
        if (cat == null) { System.out.println("Khong tim thay danh muc!"); return; }

        System.out.println("Ten hien tai: " + cat.getCategoryName());
        System.out.println("Mo ta hien tai: " + cat.getDescription());
        String newName = InputValidator.readNonEmptyString(scanner, "Ten moi: ");
        System.out.print("Mo ta moi (Enter de giu nguyen): ");
        String newDesc = scanner.nextLine().trim();
        if (newDesc.isEmpty()) newDesc = cat.getDescription();

        if (categoryService.updateCategory(id, newName, newDesc)) {
            System.out.println("Cap nhat danh muc thanh cong!");
        } else {
            System.out.println("Cap nhat that bai!");
        }
    }

    private void deleteCategory(Scanner scanner) {
        System.out.println("\n--- XOA MEM DANH MUC ---");
        TablePrinter.printCategoryTable(categoryService.getAllCategories());
        int id = InputValidator.readInt(scanner, "Nhap ID danh muc can xoa: ");
        if (InputValidator.confirmYesNo(scanner, "Ban co chac muon xoa danh muc ID=" + id + "?")) {
            if (categoryService.softDeleteCategory(id)) {
                System.out.println("Da xoa danh muc thanh cong!");
            } else {
                System.out.println("Xoa danh muc that bai!");
            }
        } else {
            System.out.println("Da huy thao tac.");
        }
    }

    private void restoreCategory(Scanner scanner) {
        System.out.println("\n--- KHOI PHUC DANH MUC ---");
        TablePrinter.printCategoryTable(categoryService.getAllCategories());
        int id = InputValidator.readInt(scanner, "Nhap ID danh muc can khoi phuc: ");
        if (categoryService.restoreCategory(id)) {
            System.out.println("Da khoi phuc danh muc!");
        } else {
            System.out.println("Khoi phuc that bai!");
        }
    }

    // SAN PHAM

    private void productMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- QUAN LY SAN PHAM ---");
            System.out.println("1. Xem danh sach san pham");
            System.out.println("2. Them san pham moi");
            System.out.println("3. Sua san pham");
            System.out.println("4. Xoa san pham");
            System.out.println("5. Tim kiem san pham theo ten");
            System.out.println("6. Sap xep san pham theo gia");
            System.out.println("0. Quay lai");

            int choice = InputValidator.readInt(scanner, "Chon: ");
            switch (choice) {
                case 1:
                    TablePrinter.printHeader("DANH SACH SAN PHAM");
                    TablePrinter.printProductTable(productService.getAllActiveProducts());
                    break;
                case 2: addProduct(scanner); break;
                case 3: editProduct(scanner); break;
                case 4: deleteProduct(scanner); break;
                case 5: searchProduct(scanner); break;
                case 6: sortProducts(scanner); break;
                case 0: return;
                default: System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private void addProduct(Scanner scanner) {
        System.out.println("\n--- THEM SAN PHAM MOI ---");
        TablePrinter.printCategoryTable(categoryService.getActiveCategories());
        String name = InputValidator.readNonEmptyString(scanner, "Ten san pham: ");
        int categoryId = InputValidator.readInt(scanner, "ID Danh muc: ");

        BigDecimal price;
        while (true) {
            price = InputValidator.readBigDecimal(scanner, "Gia (VND): ");
            if (InputValidator.isPositiveDecimal(price)) break;
            System.out.println("Gia phai lon hon 0!");
        }
        int stock;
        while (true) {
            stock = InputValidator.readInt(scanner, "So luong ton kho: ");
            if (InputValidator.isPositiveInt(stock)) break;
            System.out.println("So luong phai lon hon 0!");
        }
        String color = InputValidator.readNonEmptyString(scanner, "Mau sac: ");
        String storage = InputValidator.readNonEmptyString(scanner, "Dung luong (VD: 128GB): ");

        if (productService.addProduct(name, categoryId, price, stock, color, storage)) {
            System.out.println("Them san pham thanh cong!");
        } else {
            System.out.println("Them san pham that bai!");
        }
    }

    private void editProduct(Scanner scanner) {
        System.out.println("\n--- SUA SAN PHAM ---");
        TablePrinter.printProductTable(productService.getAllActiveProducts());
        int id = InputValidator.readInt(scanner, "Nhap ID san pham can sua: ");
        Product p = productService.getProductById(id);
        if (p == null) { System.out.println("Khong tim thay san pham!"); return; }

        System.out.println("Thong tin hien tai:");
        System.out.println("   ID (khong the sua): " + p.getProductId());
        System.out.println("   Ten: " + p.getProductName());
        System.out.println("   Danh muc ID: " + p.getCategoryId());
        System.out.println("   Gia: " + TablePrinter.formatCurrency(p.getPrice()));
        System.out.println("   Ton kho: " + p.getStock());
        System.out.println("   Mau: " + p.getColor());
        System.out.println("   Dung luong: " + p.getStorageCapacity());
        System.out.println("Nhap thong tin moi (Enter de giu nguyen):");

        System.out.print("Ten moi [" + p.getProductName() + "]: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = p.getProductName();

        System.out.print("ID Danh muc moi [" + p.getCategoryId() + "]: ");
        String catStr = scanner.nextLine().trim();
        int catId = catStr.isEmpty() ? p.getCategoryId() : Integer.parseInt(catStr);

        System.out.print("Gia moi [" + p.getPrice() + "]: ");
        String priceStr = scanner.nextLine().trim();
        BigDecimal price = priceStr.isEmpty() ? p.getPrice() : new BigDecimal(priceStr);

        System.out.print("Ton kho moi [" + p.getStock() + "]: ");
        String stockStr = scanner.nextLine().trim();
        int stock = stockStr.isEmpty() ? p.getStock() : Integer.parseInt(stockStr);

        System.out.print("Mau moi [" + p.getColor() + "]: ");
        String color = scanner.nextLine().trim();
        if (color.isEmpty()) color = p.getColor();

        System.out.print("Dung luong moi [" + p.getStorageCapacity() + "]: ");
        String storage = scanner.nextLine().trim();
        if (storage.isEmpty()) storage = p.getStorageCapacity();

        if (productService.updateProduct(id, name, catId, price, stock, color, storage)) {
            System.out.println("Cap nhat san pham thanh cong!");
        } else {
            System.out.println("Cap nhat that bai!");
        }
    }

    private void deleteProduct(Scanner scanner) {
        System.out.println("\n--- XOA SAN PHAM ---");
        TablePrinter.printProductTable(productService.getAllActiveProducts());
        int id = InputValidator.readInt(scanner, "Nhap ID san pham can xoa: ");
        Product p = productService.getProductById(id);
        if (p == null) { System.out.println("Khong tim thay san pham!"); return; }

        System.out.println("San pham se bi xoa: " + p.getProductName() + " - " + TablePrinter.formatCurrency(p.getPrice()));
        if (InputValidator.confirmYesNo(scanner, "Ban co chac chan muon xoa san pham nay?")) {
            if (productService.deleteProduct(id)) {
                System.out.println("Da xoa san pham thanh cong!");
            } else {
                System.out.println("Xoa san pham that bai!");
            }
        } else {
            System.out.println("Da huy thao tac xoa.");
        }
    }

    private void searchProduct(Scanner scanner) {
        System.out.println("\n--- TIM KIEM SAN PHAM ---");
        String keyword = InputValidator.readNonEmptyString(scanner, "Nhap tu khoa tim kiem: ");
        List<Product> results = productService.searchByName(keyword);
        TablePrinter.printHeader("KET QUA TIM KIEM: \"" + keyword + "\"");
        TablePrinter.printProductTable(results);
    }

    private void sortProducts(Scanner scanner) {
        System.out.println("\n--- SAP XEP SAN PHAM THEO GIA ---");
        System.out.println("1. Gia tang dan");
        System.out.println("2. Gia giam dan");
        int choice = InputValidator.readInt(scanner, "Chon: ");
        boolean ascending = (choice == 1);
        List<Product> sorted = productService.sortByPrice(ascending);
        TablePrinter.printHeader("SAN PHAM THEO GIA " + (ascending ? "TANG DAN" : "GIAM DAN"));
        TablePrinter.printProductTable(sorted);
    }

    // DON HANG

    private void orderMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- QUAN LY DON HANG ---");
            System.out.println("1. Xem tat ca don hang");
            System.out.println("2. Xem chi tiet don hang");
            System.out.println("3. Cap nhat trang thai don hang");
            System.out.println("0. Quay lai");

            int choice = InputValidator.readInt(scanner, "Chon: ");
            switch (choice) {
                case 1:
                    TablePrinter.printHeader("TAT CA DON HANG");
                    TablePrinter.printOrderTable(orderService.getAllOrders());
                    break;
                case 2: viewOrderDetail(scanner); break;
                case 3: updateOrderStatus(scanner); break;
                case 0: return;
                default: System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private void viewOrderDetail(Scanner scanner) {
        int orderId = InputValidator.readInt(scanner, "Nhap ma don hang: ");
        Order order = orderService.getOrderById(orderId);
        if (order == null) { System.out.println("Khong tim thay don hang!"); return; }

        System.out.println("\nCHI TIET DON HANG #" + orderId);
        System.out.println("   Khach hang: " + order.getCustomerName());
        System.out.println("   Ngay dat: " + order.getOrderDate());
        System.out.println("   Dia chi: " + order.getDeliveryAddress());
        System.out.println("   Trang thai: " + order.getOrderStatus());
        System.out.println("   Tong tien: " + TablePrinter.formatCurrency(order.getTotalAmount()));

        List<OrderDetail> details = orderService.getOrderDetails(orderId);
        System.out.println("\n   San pham trong don:");
        String format = "   | %-4s | %-25s | %-6s | %-15s | %-15s |%n";
        System.out.printf(format, "ID", "Ten san pham", "SL", "Don gia", "Thanh tien");
        System.out.println("   " + "-".repeat(75));
        for (OrderDetail d : details) {
            System.out.printf(format,
                    d.getProductId(),
                    d.getProductName() != null ? d.getProductName() : "N/A",
                    d.getQuantity(),
                    TablePrinter.formatCurrency(d.getUnitPrice()),
                    TablePrinter.formatCurrency(d.getUnitPrice().multiply(java.math.BigDecimal.valueOf(d.getQuantity())))
            );
        }
    }

    private void updateOrderStatus(Scanner scanner) {
        System.out.println("\n--- CAP NHAT TRANG THAI DON HANG ---");
        TablePrinter.printOrderTable(orderService.getAllOrders());
        int orderId = InputValidator.readInt(scanner, "Nhap ma don hang: ");
        Order order = orderService.getOrderById(orderId);
        if (order == null) { System.out.println("Khong tim thay don hang!"); return; }

        System.out.println("Trang thai hien tai: " + order.getOrderStatus());
        System.out.println("1. SHIPPING");
        System.out.println("2. DELIVERED");
        System.out.println("3. CANCELLED");
        int choice = InputValidator.readInt(scanner, "Chon trang thai moi: ");
        String newStatus;
        switch (choice) {
            case 1: newStatus = "SHIPPING"; break;
            case 2: newStatus = "DELIVERED"; break;
            case 3: newStatus = "CANCELLED"; break;
            default: System.out.println("Lua chon khong hop le."); return;
        }
        if (orderService.updateOrderStatus(orderId, newStatus)) {
            System.out.println("Cap nhat trang thai thanh cong: " + newStatus);
        } else {
            System.out.println("Cap nhat trang thai that bai!");
        }
    }
}
