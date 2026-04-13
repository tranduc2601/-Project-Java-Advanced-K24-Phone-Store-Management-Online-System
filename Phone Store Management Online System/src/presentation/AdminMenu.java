package presentation;

import model.*;
import service.*;
import util.InputValidator;
import util.TablePrinter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final CategoryService categoryService = new CategoryService();
    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();
    private final CouponService couponService = new CouponService();
    private final FlashSaleService flashSaleService = new FlashSaleService();

    private static final int PAGE_SIZE = 5;

    public void show(Scanner scanner, User admin) {
        while (true) {
            System.out.println();
            System.out.println("MENU ADMIN");
            System.out.println("1. Quan ly Danh muc");
            System.out.println("2. Quan ly San pham");
            System.out.println("3. Quan ly Don hang");
            System.out.println("4. Quan ly Ma giam gia");
            System.out.println("5. Quan ly Flash Sale");
            System.out.println("0. Dang xuat");

            int choice = InputValidator.readInt(scanner, "Chon: ");

            try {
                switch (choice) {
                    case 1: categoryMenu(scanner); break;
                    case 2: productMenu(scanner); break;
                    case 3: orderMenu(scanner); break;
                    case 4: couponMenu(scanner); break;
                    case 5: flashSaleMenu(scanner); break;
                    case 6: reportMenu(scanner); break;
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

    private void categoryMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- QUAN LY DANH MUC ---");
            System.out.println("1. Xem danh sach danh muc");
            System.out.println("2. Them danh muc moi");
            System.out.println("3. Sua danh muc");
            System.out.println("4. Xoa mem danh muc (An)");
            System.out.println("5. Khoi phuc danh muc da an");
            System.out.println("0. Quay lai");

            int choice = InputValidator.readInt(scanner, "Chon: ");
            switch (choice) {
                case 1:
                    TablePrinter.printHeader("DANH SACH DANH MUC");
                    TablePrinter.printCategoryTable(categoryService.getAllCategories());
                    break;
                case 2:
                    addCategory(scanner);
                    break;
                case 3:
                    editCategory(scanner);
                    break;
                case 4:
                    deleteCategory(scanner);
                    break;
                case 5:
                    restoreCategory(scanner);
                    break;
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
        if (cat == null) {
            System.out.println("Khong tim thay danh muc!");
            return;
        }

        System.out.println("Thong tin hien tai:");
        System.out.println("   Ten: " + cat.getCategoryName());
        System.out.println("   Mo ta: " + cat.getDescription());

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

        int id = InputValidator.readInt(scanner, "Nhap ID danh muc can an: ");
        if (InputValidator.confirmYesNo(scanner, "Ban co chac muon an danh muc ID=" + id + "?")) {
            if (categoryService.softDeleteCategory(id)) {
                System.out.println("Da an danh muc thanh cong!");
            } else {
                System.out.println("An danh muc that bai!");
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

    private void productMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- QUAN LY SAN PHAM ---");
            System.out.println("1. Xem danh sach san pham (phan trang)");
            System.out.println("2. Them san pham moi");
            System.out.println("3. Sua san pham");
            System.out.println("4. Xoa san pham");
            System.out.println("5. Tim kiem san pham theo ten");
            System.out.println("6. Sap xep san pham theo gia");
            System.out.println("0. Quay lai");

            int choice = InputValidator.readInt(scanner, "Chon: ");
            switch (choice) {
                case 1: listProductsPaginated(scanner); break;
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

    private void listProductsPaginated(Scanner scanner) {
        int totalProducts = productService.countActiveProducts();
        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);
        if (totalPages == 0) {
            System.out.println("Khong co san pham.");
            return;
        }

        int currentPage = 1;
        while (true) {
            TablePrinter.printHeader("DANH SACH SẢN PHẨM - Trang " + currentPage + "/" + totalPages);
            List<Product> products = productService.getProductsPaginated(currentPage, PAGE_SIZE);
            TablePrinter.printProductTable(products);

            System.out.println("\nN: Trang sau | P: Trang truoc | 0: Quay lai");
            System.out.print("Chon: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if ("N".equals(input) && currentPage < totalPages) {
                currentPage++;
            } else if ("P".equals(input) && currentPage > 1) {
                currentPage--;
            } else if ("0".equals(input)) {
                return;
            }
        }
    }

    private void addProduct(Scanner scanner) {
        System.out.println("\n--- THEM SAN PHAM MOI ---");

        System.out.println("Danh sach danh muc:");
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
        if (p == null) {
            System.out.println("Khong tim thay san pham!");
            return;
        }

        System.out.println("Thong tin hien tai:");
        System.out.println("   ID (khong the sua): " + p.getProductId());
        System.out.println("   Ten: " + p.getProductName());
        System.out.println("   Danh muc ID: " + p.getCategoryId());
        System.out.println("   Gia: " + TablePrinter.formatCurrency(p.getPrice()));
        System.out.println("   Ton kho: " + p.getStock());
        System.out.println("   Mau: " + p.getColor());
        System.out.println("   Dung luong: " + p.getStorageCapacity());

        System.out.println("\nNhap thong tin moi (Enter de giu nguyen):");

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
        if (p == null) {
            System.out.println("Khong tim thay san pham!");
            return;
        }

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
                case 2:
                    viewOrderDetail(scanner);
                    break;
                case 3:
                    updateOrderStatus(scanner);
                    break;
                case 0: return;
                default: System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private void viewOrderDetail(Scanner scanner) {
        int orderId = InputValidator.readInt(scanner, "Nhap ma don hang: ");
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            System.out.println("Khong tim thay don hang!");
            return;
        }

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
        if (order == null) {
            System.out.println("Khong tim thay don hang!");
            return;
        }

        System.out.println("Trang thai hien tai: " + order.getOrderStatus());
        System.out.println("Luu y: PENDING -> SHIPPING -> DELIVERED (hoac -> CANCELLED)");
        System.out.println("1. SHIPPING");
        System.out.println("2. DELIVERED");
        System.out.println("3. CANCELLED");

        int choice = InputValidator.readInt(scanner, "Chon trang thai moi: ");
        String newStatus;
        switch (choice) {
            case 1: newStatus = "SHIPPING"; break;
            case 2: newStatus = "DELIVERED"; break;
            case 3: newStatus = "CANCELLED"; break;
            default:
                System.out.println("Lua chon khong hop le.");
                return;
        }

        if (orderService.updateOrderStatus(orderId, newStatus)) {
            System.out.println("Cap nhat trang thai thanh cong: " + newStatus);
        } else {
            System.out.println("Cap nhat trang thai that bai!");
        }
    }

    private void couponMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- QUAN LY MA GIAM GIA ---");
            System.out.println("1. Xem danh sach ma giam gia");
            System.out.println("2. Them ma giam gia moi");
            System.out.println("3. Vo hieu hoa ma giam gia");
            System.out.println("0. Quay lai");

            int choice = InputValidator.readInt(scanner, "Chon: ");
            switch (choice) {
                case 1:
                    listCoupons();
                    break;
                case 2:
                    addCoupon(scanner);
                    break;
                case 3:
                    deactivateCoupon(scanner);
                    break;
                case 0: return;
                default: System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private void listCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        if (coupons.isEmpty()) {
            System.out.println("Chua co ma giam gia nao.");
            return;
        }
        System.out.println("\nDANH SACH MA GIAM GIA:");
        String format = "| %-4s | %-15s | %-10s | %-12s | %-10s |%n";
        System.out.printf(format, "ID", "Ma Code", "Giam (%)", "Het han", "Trang thai");
        System.out.println("-".repeat(65));
        for (Coupon c : coupons) {
            System.out.printf(format,
                    c.getCouponId(), c.getCouponCode(), c.getDiscountPercent() + "%",
                    c.getValidUntil() != null ? c.getValidUntil().toString() : "N/A",
                    c.isStatus() ? (c.isValid() ? "Hoat dong" : "Het han") : "Vo hieu"
            );
        }
    }

    private void addCoupon(Scanner scanner) {
        System.out.println("\n--- THEM MA GIAM GIA MOI ---");
        String code = InputValidator.readNonEmptyString(scanner, "Ma giam gia (VD: SALE20): ");
        int percent = InputValidator.readInt(scanner, "Phan tram giam gia (1-100): ");

        System.out.print("Ngay het han (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine().trim();
        try {
            LocalDate validUntil = LocalDate.parse(dateStr);
            if (couponService.addCoupon(code, percent, validUntil)) {
                System.out.println("Them ma giam gia thanh cong!");
            } else {
                System.out.println("Them ma giam gia that bai!");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Dinh dang ngay khong hop le! Su dung yyyy-MM-dd.");
        }
    }

    private void deactivateCoupon(Scanner scanner) {
        listCoupons();
        int id = InputValidator.readInt(scanner, "Nhap ID coupon can vo hieu hoa: ");
        if (couponService.deactivateCoupon(id)) {
            System.out.println("Da vo hieu hoa ma giam gia!");
        } else {
            System.out.println("Thao tac that bai!");
        }
    }

    private void flashSaleMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- QUAN LY FLASH SALE ---");
            System.out.println("1. Xem tat ca Flash Sale");
            System.out.println("2. Xem Flash Sale dang hoat dong");
            System.out.println("3. Tao Flash Sale moi");
            System.out.println("4. Huy Flash Sale");
            System.out.println("0. Quay lai");

            int choice = InputValidator.readInt(scanner, "Chon: ");
            switch (choice) {
                case 1: listFlashSales(flashSaleService.getAllFlashSales()); break;
                case 2: listFlashSales(flashSaleService.getActiveFlashSales()); break;
                case 3: addFlashSale(scanner); break;
                case 4: deactivateFlashSale(scanner); break;
                case 0: return;
                default: System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private void listFlashSales(List<FlashSale> sales) {
        if (sales.isEmpty()) {
            System.out.println("Khong co Flash Sale nao.");
            return;
        }
        System.out.println("\nDANH SACH FLASH SALE:");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String format = "| %-4s | %-25s | %-8s | %-16s | %-16s | %-10s |%n";
        System.out.printf(format, "ID", "San pham", "Giam(%)", "Bat dau", "Ket thuc", "Trang thai");
        System.out.println("-".repeat(95));
        for (FlashSale fs : sales) {
            System.out.printf(format,
                    fs.getFlashSaleId(),
                    fs.getProductName() != null ? (fs.getProductName().length() > 25 ? fs.getProductName().substring(0, 22) + "..." : fs.getProductName()) : "ID:" + fs.getProductId(),
                    fs.getDiscountPercent() + "%",
                    fs.getStartDate() != null ? fs.getStartDate().format(dtf) : "N/A",
                    fs.getEndDate() != null ? fs.getEndDate().format(dtf) : "N/A",
                    fs.isActive() ? "Active" : (fs.isStatus() ? "Cho/Het" : "Da huy")
            );
        }
    }

    private void addFlashSale(Scanner scanner) {
        System.out.println("\n--- TẠO FLASH SALE MỚI ---");
        TablePrinter.printProductTable(productService.getAllActiveProducts());

        int productId = InputValidator.readInt(scanner, "Nhap ID san pham: ");
        if (productService.getProductById(productId) == null) {
            System.out.println("San pham khong ton tai!");
            return;
        }

        int percent = InputValidator.readInt(scanner, "Phan tram giam gia (1-100): ");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            System.out.print("Thoi gian bat dau (yyyy-MM-dd HH:mm): ");
            LocalDateTime start = LocalDateTime.parse(scanner.nextLine().trim(), dtf);
            System.out.print("Thoi gian ket thuc (yyyy-MM-dd HH:mm): ");
            LocalDateTime end = LocalDateTime.parse(scanner.nextLine().trim(), dtf);

            if (flashSaleService.addFlashSale(productId, percent, start, end)) {
                System.out.println("Tao Flash Sale thanh cong!");
            } else {
                System.out.println("Tao Flash Sale that bai!");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Dinh dang thoi gian khong hop le!");
        }
    }

    private void deactivateFlashSale(Scanner scanner) {
        listFlashSales(flashSaleService.getAllFlashSales());
        int id = InputValidator.readInt(scanner, "Nhap ID Flash Sale can huy: ");
        if (flashSaleService.deactivateFlashSale(id)) {
            System.out.println("Da huy Flash Sale!");
        } else {
            System.out.println("Thao tac that bai!");
        }
    }

    // ==================== MENU BÁO CÁO ====================

    /**
     * Menu báo cáo thống kê doanh thu và sản phẩm bán chạy.
     */
    private void reportMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- BAO CAO THONG KE ---");
            System.out.println("1. Top 5 san pham ban chay nhat thang nay");
            System.out.println("2. Top 5 san pham ban chay nhat toan thoi gian (Stored Procedure)");
            System.out.println("0. Quay lai");

            int choice = InputValidator.readInt(scanner, "Chon: ");
            switch (choice) {
                case 1:
                    showTop5ThisMonth();
                    break;
                case 2:
                    showTop5AllTime();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    /**
     * Hiển thị Top 5 sản phẩm bán chạy nhất trong tháng hiện tại.
     * Dùng PreparedStatement với filter MONTH/YEAR.
     */
    private void showTop5ThisMonth() {
        YearMonth now = YearMonth.now();
        TablePrinter.printHeader("TOP 5 SAN PHAM BAN CHAY NHAT - THANG "
                + now.getMonthValue() + "/" + now.getYear());

        List<BestSellerItem> list = orderService.getTop5BestSellersThisMonth();
        printBestSellerTable(list, "thang " + now.getMonthValue() + "/" + now.getYear());
    }

    /**
     * Hiển thị Top 5 sản phẩm bán chạy nhất toàn thời gian.
     * Gọi Stored Procedure sp_GetTop5BestSellers() qua CallableStatement.
     */
    private void showTop5AllTime() {
        TablePrinter.printHeader("TOP 5 SAN PHAM BAN CHAY NHAT - TOAN THOI GIAN");
        System.out.println("   (Goi tu Stored Procedure: sp_GetTop5BestSellers)");

        List<BestSellerItem> list = orderService.getTop5BestSellers();
        printBestSellerTable(list, "toan thoi gian");
    }

    /**
     * In bảng kết quả báo cáo Top sản phẩm bán chạy ra Console.
     *
     * @param list   danh sách BestSellerItem
     * @param period tên khoảng thời gian để hiển thị trong footer
     */
    private void printBestSellerTable(List<BestSellerItem> list, String period) {
        if (list.isEmpty()) {
            System.out.println("   Chua co du lieu ban hang trong " + period + ".");
            return;
        }

        String format  = "| %-4s | %-5s | %-28s | %-10s | %-18s |%n";
        String line    = "+" + "-".repeat(6) + "+" + "-".repeat(7) + "+" + "-".repeat(30)
                       + "+" + "-".repeat(12) + "+" + "-".repeat(20) + "+";

        System.out.println(line);
        System.out.printf(format, "Hang", "ID", "Ten san pham", "Da ban", "Doanh thu");
        System.out.println(line);

        int rank = 1;
        for (BestSellerItem item : list) {
            String name = item.getProductName();
            if (name != null && name.length() > 28) name = name.substring(0, 25) + "...";

            System.out.printf(format,
                    "#" + rank++,
                    item.getProductId(),
                    name != null ? name : "N/A",
                    item.getTotalSold() + " sp",
                    TablePrinter.formatCurrency(item.getTotalRevenue())
            );
        }
        System.out.println(line);
        System.out.println("   Bao cao " + period + " - Tong " + list.size() + " san pham.");
    }
}

