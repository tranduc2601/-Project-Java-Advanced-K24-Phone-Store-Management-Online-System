package util;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TablePrinter {

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getInstance(new Locale("vi", "VN"));

    public static void printLine(int length) {
        System.out.println("+" + "-".repeat(length - 2) + "+");
    }

    public static void printHeader(String title) {
        System.out.println();
        printLine(70);
        System.out.printf("| %-66s |%n", title);
        printLine(70);
    }

    public static String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) return "0";
        return CURRENCY_FORMAT.format(amount) + " VND";
    }

    public static void printProductTable(List<model.Product> products) {
        if (products.isEmpty()) {
            System.out.println("Khong co san pham nao.");
            return;
        }
        String format = "| %-4s | %-25s | %-12s | %-15s | %-5s | %-10s | %-8s |%n";
        String line = "+" + "-".repeat(6) + "+" + "-".repeat(27) + "+" + "-".repeat(14) + "+"
                + "-".repeat(17) + "+" + "-".repeat(7) + "+" + "-".repeat(12) + "+" + "-".repeat(10) + "+";

        System.out.println(line);
        System.out.printf(format, "ID", "Ten san pham", "Danh muc", "Gia", "Kho", "Mau", "Bo nho");
        System.out.println(line);

        for (model.Product p : products) {
            String name = p.getProductName();
            if (name.length() > 25) name = name.substring(0, 22) + "...";
            String catName = p.getCategoryName() != null ? p.getCategoryName() : String.valueOf(p.getCategoryId());
            if (catName.length() > 12) catName = catName.substring(0, 9) + "...";

            System.out.printf(format,
                    p.getProductId(),
                    name,
                    catName,
                    formatCurrency(p.getPrice()),
                    p.getStock(),
                    p.getColor() != null ? (p.getColor().length() > 10 ? p.getColor().substring(0, 7) + "..." : p.getColor()) : "",
                    p.getStorageCapacity() != null ? p.getStorageCapacity() : ""
            );
        }
        System.out.println(line);
        System.out.println("Tong: " + products.size() + " san pham");
    }

    public static void printCategoryTable(List<model.Category> categories) {
        if (categories.isEmpty()) {
            System.out.println("Khong co danh muc nao.");
            return;
        }
        String format = "| %-4s | %-25s | %-35s | %-10s |%n";
        String line = "+" + "-".repeat(6) + "+" + "-".repeat(27) + "+" + "-".repeat(37) + "+" + "-".repeat(12) + "+";

        System.out.println(line);
        System.out.printf(format, "ID", "Ten danh muc", "Mo ta", "Trang thai");
        System.out.println(line);

        for (model.Category c : categories) {
            String desc = c.getDescription() != null ? c.getDescription() : "";
            if (desc.length() > 35) desc = desc.substring(0, 32) + "...";
            System.out.printf(format,
                    c.getCategoryId(),
                    c.getCategoryName(),
                    desc,
                    c.isStatus() ? "Hoat dong" : "Da an"
            );
        }
        System.out.println(line);
    }

    public static void printOrderTable(List<model.Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("Khong co don hang nao.");
            return;
        }
        String format = "| %-6s | %-20s | %-17s | %-12s | %-20s |%n";
        String line = "+" + "-".repeat(8) + "+" + "-".repeat(22) + "+" + "-".repeat(19) + "+"
                + "-".repeat(14) + "+" + "-".repeat(22) + "+";

        System.out.println(line);
        System.out.printf(format, "Ma DH", "Khach hang", "Tong tien", "Trang thai", "Ngay dat");
        System.out.println(line);

        for (model.Order o : orders) {
            String custName = o.getCustomerName() != null ? o.getCustomerName() : "ID:" + o.getUserId();
            if (custName.length() > 20) custName = custName.substring(0, 17) + "...";
            System.out.printf(format,
                    o.getOrderId(),
                    custName,
                    formatCurrency(o.getTotalAmount()),
                    o.getOrderStatus(),
                    o.getOrderDate() != null ? o.getOrderDate().toString().substring(0, 16) : ""
            );
        }
        System.out.println(line);
        System.out.println("Tong: " + orders.size() + " don hang");
    }
}
