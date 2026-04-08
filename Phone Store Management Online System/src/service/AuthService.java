package service;

import dao.UserDAO;
import model.User;
import util.InputValidator;
import util.PasswordUtil;

/**
 * Service xử lý nghiệp vụ đăng nhập / đăng ký.
 */
public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Đăng nhập: Kiểm tra email + mật khẩu với Database.
     * Mật khẩu được so sánh bằng BCrypt.
     * @return User nếu đăng nhập thành công, null nếu sai thông tin
     */
    public User login(String email, String rawPassword) {
        if (!InputValidator.isNotEmpty(email) || !InputValidator.isNotEmpty(rawPassword)) {
            System.out.println("⚠️ Email và mật khẩu không được để trống!");
            return null;
        }

        User user = userDAO.findByEmail(email);
        if (user == null) {
            System.out.println("⚠️ Email không tồn tại trong hệ thống!");
            return null;
        }

        if (!user.isStatus()) {
            System.out.println("⚠️ Tài khoản đã bị khóa! Vui lòng liên hệ Admin.");
            return null;
        }

        if (!PasswordUtil.checkPassword(rawPassword, user.getPassword())) {
            System.out.println("⚠️ Mật khẩu không chính xác!");
            return null;
        }

        return user;
    }

    /**
     * Đăng ký tài khoản Customer mới.
     * Mật khẩu được mã hóa BCrypt trước khi lưu vào DB.
     */
    public boolean register(String fullName, String email, String phone, String rawPassword) {
        // Validate
        if (!InputValidator.isNotEmpty(fullName)) {
            System.out.println("⚠️ Họ tên không được để trống!");
            return false;
        }
        if (!InputValidator.isValidEmail(email)) {
            System.out.println("⚠️ Email không hợp lệ! Email phải chứa ký tự @.");
            return false;
        }
        if (!InputValidator.isValidPhone(phone)) {
            System.out.println("⚠️ Số điện thoại không hợp lệ! Phải là 10 chữ số và bắt đầu bằng 0.");
            return false;
        }
        if (!InputValidator.isNotEmpty(rawPassword) || rawPassword.length() < 4) {
            System.out.println("⚠️ Mật khẩu không được để trống và phải có ít nhất 4 ký tự!");
            return false;
        }

        // Kiểm tra email đã tồn tại chưa
        if (userDAO.findByEmail(email) != null) {
            System.out.println("⚠️ Email này đã được đăng ký!");
            return false;
        }

        // Mã hóa mật khẩu bằng BCrypt
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);

        User newUser = new User(fullName, email, phone, hashedPassword);
        return userDAO.register(newUser);
    }
}

