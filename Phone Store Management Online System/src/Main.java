import model.User;
import presentation.AdminMenu;
import presentation.AuthMenu;
import presentation.CustomerMenu;

import java.util.Scanner;

/**
 * ============================================================
 *   HỆ THỐNG QUẢN LÝ SHOP BÁN ĐIỆN THOẠI ONLINE
 *       Phone Store Management Online System
 * ============================================================
 *
 * Kiến trúc 5 lớp:
 *   - Model:        Các lớp thực thể (User, Product, Order...)
 *   - DAO:          Tương tác với Database (PreparedStatement)
 *   - Service:      Xử lý nghiệp vụ (Business Logic)
 *   - Presentation: Giao diện Console
 *   - Util:         Tiện ích chung (DB Connection, BCrypt, Validator...)
 *
 * Yêu cầu:
 *   - MySQL Server đang chạy với database PhoneStoreDB (chạy schema.sql)
 *   - Thư viện trong lib/:
 *       + mysql-connector-j-8.x.jar
 *       + jbcrypt-0.4.jar
 */
public class Main {
    // Chạy chương trình chính, hiển thị menu đăng nhập và điều hướng theo vai trò
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthMenu authMenu = new AuthMenu();
        AdminMenu adminMenu = new AdminMenu();
        CustomerMenu customerMenu = new CustomerMenu();

        System.out.println("PHONE STORE MANAGEMENT ONLINE SYSTEM");
        System.out.println("He thong Quan ly Shop Ban Dien Thoai Online");

        while (true) {
            try {
                User loggedInUser = authMenu.show(scanner);

                if (loggedInUser == null) {
                    continue;
                }

                if (loggedInUser.isAdmin()) {
                    adminMenu.show(scanner, loggedInUser);
                } else {
                    customerMenu.show(scanner, loggedInUser);
                }

            } catch (Exception e) {
                System.out.println("Da xay ra loi he thong: " + e.getMessage());
                System.out.println("Vui long thu lai.");
            }
        }
    }
}
