package util;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Tiện ích kiểm tra tính hợp lệ (Validation) dữ liệu đầu vào.
 */
public class InputValidator {

    /**
     * Kiểm tra email có chứa ký tự @
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.indexOf("@") > 0
                && email.indexOf("@") < email.length() - 1;
    }

    /**
     * Kiểm tra số điện thoại: phải là số và đúng 10 chữ số
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^0\\d{9}$");
    }

    /**
     * Kiểm tra chuỗi không rỗng
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Kiểm tra giá trị BigDecimal > 0
     */
    public static boolean isPositiveDecimal(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Kiểm tra giá trị int > 0
     */
    public static boolean isPositiveInt(int value) {
        return value > 0;
    }

    /**
     * Kiểm tra giá trị int >= 0
     */
    public static boolean isNonNegativeInt(int value) {
        return value >= 0;
    }

    /**
     * Hiển thị xác nhận Y/N và trả về kết quả
     */
    public static boolean confirmYesNo(Scanner scanner, String message) {
        System.out.print(message + " (Y/N): ");
        String input = scanner.nextLine().trim().toUpperCase();
        return input.equals("Y") || input.equals("YES");
    }

    /**
     * Đọc số nguyên từ người dùng, có kiểm tra lỗi
     */
    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Vui lòng nhập một số nguyên hợp lệ!");
            }
        }
    }

    /**
     * Đọc số thực BigDecimal từ người dùng, có kiểm tra lỗi
     */
    public static BigDecimal readBigDecimal(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                BigDecimal value = new BigDecimal(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Vui lòng nhập một số hợp lệ!");
            }
        }
    }

    /**
     * Đọc chuỗi không rỗng từ người dùng
     */
    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("⚠️ Không được để trống trường này!");
        }
    }
}

