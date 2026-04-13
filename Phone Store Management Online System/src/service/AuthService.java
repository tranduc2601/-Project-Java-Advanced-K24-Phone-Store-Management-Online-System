package service;

import dao.UserDAO;
import model.User;
import util.InputValidator;
import util.PasswordUtil;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String rawPassword) {
        if (!InputValidator.isNotEmpty(email) || !InputValidator.isNotEmpty(rawPassword)) {
            System.out.println("Email va mat khau khong duoc de trong!");
            return null;
        }

        User user = userDAO.findByEmail(email);
        if (user == null) {
            System.out.println("Email khong ton tai trong he thong!");
            return null;
        }

        if (!user.isStatus()) {
            System.out.println("Tai khoan da bi khoa! Vui long lien he Admin.");
            return null;
        }

        if (!PasswordUtil.checkPassword(rawPassword, user.getPassword())) {
            System.out.println("Mat khau khong chinh xac!");
            return null;
        }

        return user;
    }

    public boolean register(String fullName, String email, String phone, String rawPassword) {
        if (!InputValidator.isNotEmpty(fullName)) {
            System.out.println("Ho ten khong duoc de trong!");
            return false;
        }
        if (!InputValidator.isValidEmail(email)) {
            System.out.println("Email khong hop le! Email phai chua ky tu @.");
            return false;
        }
        if (!InputValidator.isValidPhone(phone)) {
            System.out.println("So dien thoai khong hop le! Phai la 10 chu so va bat dau bang 0.");
            return false;
        }
        if (!InputValidator.isNotEmpty(rawPassword) || rawPassword.length() < 4) {
            System.out.println("Mat khau khong duoc de trong va phai co it nhat 4 ky tu!");
            return false;
        }
        if (userDAO.findByEmail(email) != null) {
            System.out.println("Email nay da duoc dang ky!");
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(rawPassword);

        User newUser = new User(fullName, email, phone, hashedPassword);
        return userDAO.register(newUser);
    }
}
