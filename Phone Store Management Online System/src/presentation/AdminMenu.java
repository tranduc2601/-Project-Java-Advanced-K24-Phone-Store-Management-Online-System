package presentation;

import model.*;
import service.*;
import util.InputValidator;
import util.TablePrinter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Menu dành cho Admin - Quản trị hệ thống.
 */
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
            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║        🛠️ MENU QUẢN TRỊ VIÊN (ADMIN)        ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1. 📁 Quản lý Danh mục (Category)          ║");
            System.out.println("║  2. 📦 Quản lý Sản phẩm (Product)           ║");
            System.out.println("║  3. 📋 Quản lý Đơn hàng (Order)             ║");
            System.out.println("║  4. 🎫 Quản lý Mã giảm giá (Coupon)         ║");
            System.out.println("║  5. ⚡ Quản lý Flash Sale                    ║");
            System.out.println("║  0. 🔓 Đăng xuất                            ║");
            System.out.println("╚══════════════════════════════════════════════╝");

            int choice = InputValidator.readInt(scanner, "👉 Chọn chức năng: ");

            try {
                switch (choice) {
                    case 1: categoryMenu(scanner); break;
                    case 2: productMenu(scanner); break;
                    case 3: orderMenu(scanner); break;
                    case 4: couponMenu(scanner); break;
                    case 5: flashSaleMenu(scanner); break;
                    case 0:
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

    // ======================== CATEGORY MANAGEMENT ========================

    private void categoryMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- 📁 QUẢN LÝ DANH MỤC ---");
            System.out.println("1. Xem danh sách danh mục");
            System.out.println("2. Thêm danh mục mới");
            System.out.println("3. Sửa danh mục");
            System.out.println("4. Xóa mềm danh mục (Ẩn)");
            System.out.println("5. Khôi phục danh mục đã ẩn");
            System.out.println("0. Quay lại");

            int choice = InputValidator.readInt(scanner, "👉 Chọn: ");
            switch (choice) {
                case 1:
                    TablePrinter.printHeader("DANH SÁCH DANH MỤC");
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
                default: System.out.println("⚠️ Lựa chọn không hợp lệ!");
            }
        }
    }

    private void addCategory(Scanner scanner) {
        System.out.println("\n--- THÊM DANH MỤC MỚI ---");
        String name = InputValidator.readNonEmptyString(scanner, "Tên danh mục: ");
        System.out.print("Mô tả: ");
        String desc = scanner.nextLine().trim();

        if (categoryService.addCategory(name, desc)) {
            System.out.println("✅ Thêm danh mục thành công!");
        } else {
            System.out.println("❌ Thêm danh mục thất bại!");
        }
    }

    private void editCategory(Scanner scanner) {
        System.out.println("\n--- SỬA DANH MỤC ---");
        TablePrinter.printCategoryTable(categoryService.getAllCategories());

        int id = InputValidator.readInt(scanner, "Nhập ID danh mục cần sửa: ");
        Category cat = categoryService.getCategoryById(id);
        if (cat == null) {
            System.out.println("⚠️ Không tìm thấy danh mục!");
            return;
        }

        System.out.println("📌 Thông tin hiện tại:");
        System.out.println("   Tên: " + cat.getCategoryName());
        System.out.println("   Mô tả: " + cat.getDescription());

        String newName = InputValidator.readNonEmptyString(scanner, "Tên mới: ");
        System.out.print("Mô tả mới (Enter để giữ nguyên): ");
        String newDesc = scanner.nextLine().trim();
        if (newDesc.isEmpty()) newDesc = cat.getDescription();

        if (categoryService.updateCategory(id, newName, newDesc)) {
            System.out.println("✅ Cập nhật danh mục thành công!");
        } else {
            System.out.println("❌ Cập nhật thất bại!");
        }
    }

    private void deleteCategory(Scanner scanner) {
        System.out.println("\n--- XÓA MỀM DANH MỤC ---");
        TablePrinter.printCategoryTable(categoryService.getAllCategories());

        int id = InputValidator.readInt(scanner, "Nhập ID danh mục cần ẩn: ");
        if (InputValidator.confirmYesNo(scanner, "Bạn có chắc muốn ẩn danh mục ID=" + id + "?")) {
            if (categoryService.softDeleteCategory(id)) {
                System.out.println("✅ Đã ẩn danh mục thành công!");
            } else {
                System.out.println("❌ Ẩn danh mục thất bại!");
            }
        } else {
            System.out.println("↩️ Đã hủy thao tác.");
        }
    }

    private void restoreCategory(Scanner scanner) {
        System.out.println("\n--- KHÔI PHỤC DANH MỤC ---");
        TablePrinter.printCategoryTable(categoryService.getAllCategories());

        int id = InputValidator.readInt(scanner, "Nhập ID danh mục cần khôi phục: ");
        if (categoryService.restoreCategory(id)) {
            System.out.println("✅ Đã khôi phục danh mục!");
        } else {
            System.out.println("❌ Khôi phục thất bại!");
        }
    }

    // ======================== PRODUCT MANAGEMENT ========================

    private void productMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- 📦 QUẢN LÝ SẢN PHẨM ---");
            System.out.println("1. Xem danh sách sản phẩm (phân trang)");
            System.out.println("2. Thêm sản phẩm mới");
            System.out.println("3. Sửa sản phẩm");
            System.out.println("4. Xóa sản phẩm");
            System.out.println("5. Tìm kiếm sản phẩm theo tên");
            System.out.println("6. Sắp xếp sản phẩm theo giá");
            System.out.println("0. Quay lại");

            int choice = InputValidator.readInt(scanner, "👉 Chọn: ");
            switch (choice) {
                case 1: listProductsPaginated(scanner); break;
                case 2: addProduct(scanner); break;
                case 3: editProduct(scanner); break;
                case 4: deleteProduct(scanner); break;
                case 5: searchProduct(scanner); break;
                case 6: sortProducts(scanner); break;
                case 0: return;
                default: System.out.println("⚠️ Lựa chọn không hợp lệ!");
            }
        }
    }

    private void listProductsPaginated(Scanner scanner) {
        int totalProducts = productService.countActiveProducts();
        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);
        if (totalPages == 0) {
            System.out.println("📭 Không có sản phẩm nào.");
            return;
        }

        int currentPage = 1;
        while (true) {
            TablePrinter.printHeader("DANH SÁCH SẢN PHẨM - Trang " + currentPage + "/" + totalPages);
            List<Product> products = productService.getProductsPaginated(currentPage, PAGE_SIZE);
            TablePrinter.printProductTable(products);

            System.out.println("\n[N] Trang sau | [P] Trang trước | [0] Quay lại");
            System.out.print("👉 Chọn: ");
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
        System.out.println("\n--- THÊM SẢN PHẨM MỚI ---");

        // Hiển thị danh mục để chọn
        System.out.println("📁 Danh sách danh mục:");
        TablePrinter.printCategoryTable(categoryService.getActiveCategories());

        String name = InputValidator.readNonEmptyString(scanner, "Tên sản phẩm: ");
        int categoryId = InputValidator.readInt(scanner, "ID Danh mục: ");

        BigDecimal price;
        while (true) {
            price = InputValidator.readBigDecimal(scanner, "Giá (VND): ");
            if (InputValidator.isPositiveDecimal(price)) break;
            System.out.println("⚠️ Giá phải lớn hơn 0!");
        }

        int stock;
        while (true) {
            stock = InputValidator.readInt(scanner, "Số lượng tồn kho: ");
            if (InputValidator.isPositiveInt(stock)) break;
            System.out.println("⚠️ Số lượng phải lớn hơn 0!");
        }

        String color = InputValidator.readNonEmptyString(scanner, "Màu sắc: ");
        String storage = InputValidator.readNonEmptyString(scanner, "Dung lượng (VD: 128GB): ");

        if (productService.addProduct(name, categoryId, price, stock, color, storage)) {
            System.out.println("✅ Thêm sản phẩm thành công!");
        } else {
            System.out.println("❌ Thêm sản phẩm thất bại!");
        }
    }

    private void editProduct(Scanner scanner) {
        System.out.println("\n--- SỬA SẢN PHẨM ---");
        TablePrinter.printProductTable(productService.getAllActiveProducts());

        int id = InputValidator.readInt(scanner, "Nhập ID sản phẩm cần sửa: ");
        Product p = productService.getProductById(id);
        if (p == null) {
            System.out.println("⚠️ Không tìm thấy sản phẩm!");
            return;
        }

        // Hiển thị thông tin cũ trước khi sửa
        System.out.println("📌 Thông tin hiện tại:");
        System.out.println("   ID (không thể sửa): " + p.getProductId());
        System.out.println("   Tên: " + p.getProductName());
        System.out.println("   Danh mục ID: " + p.getCategoryId());
        System.out.println("   Giá: " + TablePrinter.formatCurrency(p.getPrice()));
        System.out.println("   Tồn kho: " + p.getStock());
        System.out.println("   Màu: " + p.getColor());
        System.out.println("   Dung lượng: " + p.getStorageCapacity());

        System.out.println("\n📝 Nhập thông tin mới (Enter để giữ nguyên):");

        System.out.print("Tên mới [" + p.getProductName() + "]: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = p.getProductName();

        System.out.print("ID Danh mục mới [" + p.getCategoryId() + "]: ");
        String catStr = scanner.nextLine().trim();
        int catId = catStr.isEmpty() ? p.getCategoryId() : Integer.parseInt(catStr);

        System.out.print("Giá mới [" + p.getPrice() + "]: ");
        String priceStr = scanner.nextLine().trim();
        BigDecimal price = priceStr.isEmpty() ? p.getPrice() : new BigDecimal(priceStr);

        System.out.print("Tồn kho mới [" + p.getStock() + "]: ");
        String stockStr = scanner.nextLine().trim();
        int stock = stockStr.isEmpty() ? p.getStock() : Integer.parseInt(stockStr);

        System.out.print("Màu mới [" + p.getColor() + "]: ");
        String color = scanner.nextLine().trim();
        if (color.isEmpty()) color = p.getColor();

        System.out.print("Dung lượng mới [" + p.getStorageCapacity() + "]: ");
        String storage = scanner.nextLine().trim();
        if (storage.isEmpty()) storage = p.getStorageCapacity();

        if (productService.updateProduct(id, name, catId, price, stock, color, storage)) {
            System.out.println("✅ Cập nhật sản phẩm thành công!");
        } else {
            System.out.println("❌ Cập nhật thất bại!");
        }
    }

    private void deleteProduct(Scanner scanner) {
        System.out.println("\n--- XÓA SẢN PHẨM ---");
        TablePrinter.printProductTable(productService.getAllActiveProducts());

        int id = InputValidator.readInt(scanner, "Nhập ID sản phẩm cần xóa: ");
        Product p = productService.getProductById(id);
        if (p == null) {
            System.out.println("⚠️ Không tìm thấy sản phẩm!");
            return;
        }

        System.out.println("📌 Sản phẩm sẽ bị xóa: " + p.getProductName() + " - " + TablePrinter.formatCurrency(p.getPrice()));

        // Xác nhận Y/N trước khi xóa
        if (InputValidator.confirmYesNo(scanner, "⚠️ Bạn có chắc chắn muốn xóa sản phẩm này?")) {
            if (productService.deleteProduct(id)) {
                System.out.println("✅ Đã xóa sản phẩm thành công!");
            } else {
                System.out.println("❌ Xóa sản phẩm thất bại!");
            }
        } else {
            System.out.println("↩️ Đã hủy thao tác xóa.");
        }
    }

    private void searchProduct(Scanner scanner) {
        System.out.println("\n--- TÌM KIẾM SẢN PHẨM ---");
        String keyword = InputValidator.readNonEmptyString(scanner, "Nhập từ khóa tìm kiếm: ");
        List<Product> results = productService.searchByName(keyword);
        TablePrinter.printHeader("KẾT QUẢ TÌM KIẾM: \"" + keyword + "\"");
        TablePrinter.printProductTable(results);
    }

    private void sortProducts(Scanner scanner) {
        System.out.println("\n--- SẮP XẾP SẢN PHẨM THEO GIÁ ---");
        System.out.println("1. Giá tăng dần ↑");
        System.out.println("2. Giá giảm dần ↓");
        int choice = InputValidator.readInt(scanner, "👉 Chọn: ");

        boolean ascending = (choice == 1);
        List<Product> sorted = productService.sortByPrice(ascending);
        TablePrinter.printHeader("SẢN PHẨM THEO GIÁ " + (ascending ? "TĂNG DẦN ↑" : "GIẢM DẦN ↓"));
        TablePrinter.printProductTable(sorted);
    }

    // ======================== ORDER MANAGEMENT ========================

    private void orderMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- 📋 QUẢN LÝ ĐƠN HÀNG ---");
            System.out.println("1. Xem tất cả đơn hàng");
            System.out.println("2. Xem chi tiết đơn hàng");
            System.out.println("3. Cập nhật trạng thái đơn hàng");
            System.out.println("0. Quay lại");

            int choice = InputValidator.readInt(scanner, "👉 Chọn: ");
            switch (choice) {
                case 1:
                    TablePrinter.printHeader("TẤT CẢ ĐƠN HÀNG");
                    TablePrinter.printOrderTable(orderService.getAllOrders());
                    break;
                case 2:
                    viewOrderDetail(scanner);
                    break;
                case 3:
                    updateOrderStatus(scanner);
                    break;
                case 0: return;
                default: System.out.println("⚠️ Lựa chọn không hợp lệ!");
            }
        }
    }

    private void viewOrderDetail(Scanner scanner) {
        int orderId = InputValidator.readInt(scanner, "Nhập mã đơn hàng: ");
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            System.out.println("⚠️ Không tìm thấy đơn hàng!");
            return;
        }

        System.out.println("\n📋 CHI TIẾT ĐƠN HÀNG #" + orderId);
        System.out.println("   Khách hàng: " + order.getCustomerName());
        System.out.println("   Ngày đặt: " + order.getOrderDate());
        System.out.println("   Địa chỉ: " + order.getDeliveryAddress());
        System.out.println("   Trạng thái: " + order.getOrderStatus());
        System.out.println("   Tổng tiền: " + TablePrinter.formatCurrency(order.getTotalAmount()));

        List<OrderDetail> details = orderService.getOrderDetails(orderId);
        System.out.println("\n   Sản phẩm trong đơn:");
        String format = "   | %-4s | %-25s | %-6s | %-15s | %-15s |%n";
        System.out.printf(format, "ID", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền");
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
        System.out.println("\n--- CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG ---");
        TablePrinter.printOrderTable(orderService.getAllOrders());

        int orderId = InputValidator.readInt(scanner, "Nhập mã đơn hàng: ");
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            System.out.println("⚠️ Không tìm thấy đơn hàng!");
            return;
        }

        System.out.println("📌 Trạng thái hiện tại: " + order.getOrderStatus());
        System.out.println("Luồng hợp lệ: PENDING → SHIPPING → DELIVERED (hoặc → CANCELLED)");
        System.out.println("1. SHIPPING");
        System.out.println("2. DELIVERED");
        System.out.println("3. CANCELLED");

        int choice = InputValidator.readInt(scanner, "👉 Chọn trạng thái mới: ");
        String newStatus;
        switch (choice) {
            case 1: newStatus = "SHIPPING"; break;
            case 2: newStatus = "DELIVERED"; break;
            case 3: newStatus = "CANCELLED"; break;
            default:
                System.out.println("⚠️ Lựa chọn không hợp lệ!");
                return;
        }

        if (orderService.updateOrderStatus(orderId, newStatus)) {
            System.out.println("✅ Cập nhật trạng thái thành công: " + newStatus);
        } else {
            System.out.println("❌ Cập nhật trạng thái thất bại!");
        }
    }

    // ======================== COUPON MANAGEMENT ========================

    private void couponMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- 🎫 QUẢN LÝ MÃ GIẢM GIÁ ---");
            System.out.println("1. Xem danh sách mã giảm giá");
            System.out.println("2. Thêm mã giảm giá mới");
            System.out.println("3. Vô hiệu hóa mã giảm giá");
            System.out.println("0. Quay lại");

            int choice = InputValidator.readInt(scanner, "👉 Chọn: ");
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
                default: System.out.println("⚠️ Lựa chọn không hợp lệ!");
            }
        }
    }

    private void listCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        if (coupons.isEmpty()) {
            System.out.println("📭 Chưa có mã giảm giá nào.");
            return;
        }
        System.out.println("\n🎫 DANH SÁCH MÃ GIẢM GIÁ:");
        String format = "| %-4s | %-15s | %-10s | %-12s | %-10s |%n";
        System.out.printf(format, "ID", "Mã Code", "Giảm (%)", "Hết hạn", "Trạng thái");
        System.out.println("-".repeat(65));
        for (Coupon c : coupons) {
            System.out.printf(format,
                    c.getCouponId(), c.getCouponCode(), c.getDiscountPercent() + "%",
                    c.getValidUntil() != null ? c.getValidUntil().toString() : "N/A",
                    c.isStatus() ? (c.isValid() ? "Hoạt động" : "Hết hạn") : "Vô hiệu"
            );
        }
    }

    private void addCoupon(Scanner scanner) {
        System.out.println("\n--- THÊM MÃ GIẢM GIÁ MỚI ---");
        String code = InputValidator.readNonEmptyString(scanner, "Mã giảm giá (VD: SALE20): ");
        int percent = InputValidator.readInt(scanner, "Phần trăm giảm giá (1-100): ");

        System.out.print("Ngày hết hạn (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine().trim();
        try {
            LocalDate validUntil = LocalDate.parse(dateStr);
            if (couponService.addCoupon(code, percent, validUntil)) {
                System.out.println("✅ Thêm mã giảm giá thành công!");
            } else {
                System.out.println("❌ Thêm mã giảm giá thất bại!");
            }
        } catch (DateTimeParseException e) {
            System.out.println("⚠️ Định dạng ngày không hợp lệ! Sử dụng yyyy-MM-dd.");
        }
    }

    private void deactivateCoupon(Scanner scanner) {
        listCoupons();
        int id = InputValidator.readInt(scanner, "Nhập ID coupon cần vô hiệu hóa: ");
        if (couponService.deactivateCoupon(id)) {
            System.out.println("✅ Đã vô hiệu hóa mã giảm giá!");
        } else {
            System.out.println("❌ Thao tác thất bại!");
        }
    }

    // ======================== FLASH SALE MANAGEMENT ========================

    private void flashSaleMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- ⚡ QUẢN LÝ FLASH SALE ---");
            System.out.println("1. Xem tất cả Flash Sale");
            System.out.println("2. Xem Flash Sale đang hoạt động");
            System.out.println("3. Tạo Flash Sale mới");
            System.out.println("4. Hủy Flash Sale");
            System.out.println("0. Quay lại");

            int choice = InputValidator.readInt(scanner, "👉 Chọn: ");
            switch (choice) {
                case 1: listFlashSales(flashSaleService.getAllFlashSales()); break;
                case 2: listFlashSales(flashSaleService.getActiveFlashSales()); break;
                case 3: addFlashSale(scanner); break;
                case 4: deactivateFlashSale(scanner); break;
                case 0: return;
                default: System.out.println("⚠️ Lựa chọn không hợp lệ!");
            }
        }
    }

    private void listFlashSales(List<FlashSale> sales) {
        if (sales.isEmpty()) {
            System.out.println("📭 Không có Flash Sale nào.");
            return;
        }
        System.out.println("\n⚡ DANH SÁCH FLASH SALE:");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String format = "| %-4s | %-25s | %-8s | %-16s | %-16s | %-10s |%n";
        System.out.printf(format, "ID", "Sản phẩm", "Giảm(%)", "Bắt đầu", "Kết thúc", "Trạng thái");
        System.out.println("-".repeat(95));
        for (FlashSale fs : sales) {
            System.out.printf(format,
                    fs.getFlashSaleId(),
                    fs.getProductName() != null ? (fs.getProductName().length() > 25 ? fs.getProductName().substring(0, 22) + "..." : fs.getProductName()) : "ID:" + fs.getProductId(),
                    fs.getDiscountPercent() + "%",
                    fs.getStartDate() != null ? fs.getStartDate().format(dtf) : "N/A",
                    fs.getEndDate() != null ? fs.getEndDate().format(dtf) : "N/A",
                    fs.isActive() ? "🔥 Active" : (fs.isStatus() ? "Chờ/Hết" : "Đã hủy")
            );
        }
    }

    private void addFlashSale(Scanner scanner) {
        System.out.println("\n--- TẠO FLASH SALE MỚI ---");
        TablePrinter.printProductTable(productService.getAllActiveProducts());

        int productId = InputValidator.readInt(scanner, "Nhập ID sản phẩm: ");
        if (productService.getProductById(productId) == null) {
            System.out.println("⚠️ Sản phẩm không tồn tại!");
            return;
        }

        int percent = InputValidator.readInt(scanner, "Phần trăm giảm giá (1-100): ");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            System.out.print("Thời gian bắt đầu (yyyy-MM-dd HH:mm): ");
            LocalDateTime start = LocalDateTime.parse(scanner.nextLine().trim(), dtf);
            System.out.print("Thời gian kết thúc (yyyy-MM-dd HH:mm): ");
            LocalDateTime end = LocalDateTime.parse(scanner.nextLine().trim(), dtf);

            if (flashSaleService.addFlashSale(productId, percent, start, end)) {
                System.out.println("✅ Tạo Flash Sale thành công!");
            } else {
                System.out.println("❌ Tạo Flash Sale thất bại!");
            }
        } catch (DateTimeParseException e) {
            System.out.println("⚠️ Định dạng thời gian không hợp lệ!");
        }
    }

    private void deactivateFlashSale(Scanner scanner) {
        listFlashSales(flashSaleService.getAllFlashSales());
        int id = InputValidator.readInt(scanner, "Nhập ID Flash Sale cần hủy: ");
        if (flashSaleService.deactivateFlashSale(id)) {
            System.out.println("✅ Đã hủy Flash Sale!");
        } else {
            System.out.println("❌ Thao tác thất bại!");
        }
    }
}

