package presentation;

import model.User;
import service.AuthService;
import util.InputValidator;

import java.util.Scanner;

public class AuthMenu {
    private final AuthService authService = new AuthService();

    public User show(Scanner scanner) {
        while (true) {
            System.out.println();
            System.out.println("PHONE STORE ONLINE");
            System.out.println("1. Dang nhap");
            System.out.println("2. Dang ky");
            System.out.println("0. Thoat");

            int choice = InputValidator.readInt(scanner, "Chon: ");

            switch (choice) {
                case 1:
                    User user = handleLogin(scanner);
                    if (user != null) return user;
                    break;
                case 2:
                    handleRegister(scanner);
                    break;
                case 0:
                    System.out.println("Tam biet.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private User handleLogin(Scanner scanner) {
        System.out.println("\n--- DANG NHAP ---");
        String email = InputValidator.readNonEmptyString(scanner, "Email: ");
        System.out.print("Mat khau: ");
        String password = scanner.nextLine().trim();

        User user = authService.login(email, password);
        if (user != null) {
            System.out.println("Dang nhap thanh cong! Xin chao, " + user.getFullName());
            System.out.println("Vai tro: " + (user.isAdmin() ? "Quan tri vien (Admin)" : "Khach hang (Customer)"));
        }
        return user;
    }

    private void handleRegister(Scanner scanner) {
        System.out.println("\n--- DANG KY TAI KHOAN MOI ---");

        String fullName = InputValidator.readNonEmptyString(scanner, "Ho va ten: ");

        String email;
        while (true) {
            email = InputValidator.readNonEmptyString(scanner, "Email: ");
            if (InputValidator.isValidEmail(email)) break;
            System.out.println("Email khong hop le! Email phai chua ky tu @.");
        }

        String phone;
        while (true) {
            phone = InputValidator.readNonEmptyString(scanner, "So dien thoai (10 so): ");
            if (InputValidator.isValidPhone(phone)) break;
            System.out.println("So dien thoai khong hop le! Phai la 10 chu so va bat dau bang 0.");
        }

        String password;
        while (true) {
            password = InputValidator.readNonEmptyString(scanner, "Mat khau (toi thieu 4 ky tu): ");
            if (password.length() >= 4) break;
            System.out.println("Mat khau phai co it nhat 4 ky tu!");
        }

        if (authService.register(fullName, email, phone, password)) {
            System.out.println("Dang ky thanh cong! Ban co the dang nhap ngay.");
        } else {
            System.out.println("Dang ky that bai! Vui long thu lai.");
        }
    }
}
