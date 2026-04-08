package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Tiện ích mã hóa mật khẩu sử dụng BCrypt.
 * Yêu cầu thư viện jbcrypt-0.4.jar trong thư mục lib/
 */
public class PasswordUtil {

    /**
     * Mã hóa mật khẩu bằng BCrypt.
     * @param rawPassword Mật khẩu dạng text thuần
     * @return Chuỗi mật khẩu đã được mã hóa BCrypt
     */
    public static String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }

    /**
     * Kiểm tra mật khẩu nhập vào có khớp với mật khẩu đã mã hóa không.
     * @param rawPassword Mật khẩu người dùng nhập
     * @param hashedPassword Mật khẩu đã mã hóa từ Database
     * @return true nếu khớp, false nếu không
     */
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(rawPassword, hashedPassword);
        } catch (Exception e) {
            // Trường hợp mật khẩu trong DB chưa được mã hóa BCrypt (dữ liệu cũ)
            return rawPassword.equals(hashedPassword);
        }
    }
}

