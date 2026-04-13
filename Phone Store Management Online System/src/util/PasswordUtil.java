package util;

public class PasswordUtil {
    // cannot ma hoa mat khau
    public static String hashPassword(String rawPassword) {
        return rawPassword;
    }
    public static boolean checkPassword(String rawPassword, String storedPassword) {
        return rawPassword != null && rawPassword.equals(storedPassword);
    }
}
