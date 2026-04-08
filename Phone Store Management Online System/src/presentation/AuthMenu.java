package presentation;

import model.User;
import service.AuthService;
import util.InputValidator;

import java.util.Scanner;

/**
 * Menu đăng nhập / đăng ký hệ thống.
 */
public class AuthMenu {
    private final AuthService authService = new AuthService();

    /**
     * Hiển thị menu đăng nhập/đăng ký.
     * @return User đã đăng nhập, hoặc null nếu thoát
     */
    public User show(Scanner scanner) {
        while (true) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║   📱 HỆ THỐNG QUẢN LÝ SHOP BÁN ĐIỆN THOẠI  ║");
            System.out.println("║              PHONE STORE ONLINE              ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1. 🔑 Đăng nhập                            ║");
            System.out.println("║  2. 📝 Đăng ký tài khoản                    ║");
            System.out.println("║  0. 🚪 Thoát chương trình                   ║");
            System.out.println("╚══════════════════════════════════════════════╝");

            int choice = InputValidator.readInt(scanner, "👉 Chọn chức năng: ");

            switch (choice) {
                case 1:
                    User user = handleLogin(scanner);
                    if (user != null) return user;
                    break;
                case 2:
                    handleRegister(scanner);
                    break;
                case 0:
                    System.out.println("👋 Cảm ơn bạn đã sử dụng hệ thống. Tạm biệt!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("⚠️ Lựa chọn không hợp lệ! Vui lòng chọn lại.");
            }
        }
    }

    private User handleLogin(Scanner scanner) {
        System.out.println("\n--- 🔑 ĐĂNG NHẬP ---");
        String email = InputValidator.readNonEmptyString(scanner, "📧 Email: ");
        System.out.print("🔒 Mật khẩu: ");
        String password = scanner.nextLine().trim();

        User user = authService.login(email, password);
        if (user != null) {
            System.out.println("✅ Đăng nhập thành công! Xin chào, " + user.getFullName());
            System.out.println("🏷️ Vai trò: " + (user.isAdmin() ? "Quản trị viên (Admin)" : "Khách hàng (Customer)"));
        }
        return user;
    }

    private void handleRegister(Scanner scanner) {
        System.out.println("\n--- 📝 ĐĂNG KÝ TÀI KHOẢN MỚI ---");

        String fullName = InputValidator.readNonEmptyString(scanner, "👤 Họ và tên: ");

        String email;
        while (true) {
            email = InputValidator.readNonEmptyString(scanner, "📧 Email: ");
            if (InputValidator.isValidEmail(email)) break;
            System.out.println("⚠️ Email không hợp lệ! Email phải chứa ký tự @.");
        }

        String phone;
        while (true) {
            phone = InputValidator.readNonEmptyString(scanner, "📞 Số điện thoại (10 số): ");
            if (InputValidator.isValidPhone(phone)) break;
            System.out.println("⚠️ Số điện thoại không hợp lệ! Phải là 10 chữ số và bắt đầu bằng 0.");
        }

        String password;
        while (true) {
            password = InputValidator.readNonEmptyString(scanner, "🔒 Mật khẩu (tối thiểu 4 ký tự): ");
            if (password.length() >= 4) break;
            System.out.println("⚠️ Mật khẩu phải có ít nhất 4 ký tự!");
        }

        if (authService.register(fullName, email, phone, password)) {
            System.out.println("✅ Đăng ký thành công! Bạn có thể đăng nhập ngay.");
        } else {
            System.out.println("❌ Đăng ký thất bại! Vui lòng thử lại.");
        }
    }
}

