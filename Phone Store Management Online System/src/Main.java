import model.User;
import presentation.AdminMenu;
import presentation.AuthMenu;
import presentation.CustomerMenu;

import java.util.Scanner;

public class Main {
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
