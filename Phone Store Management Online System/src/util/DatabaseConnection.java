package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Quản lý kết nối cơ sở dữ liệu MySQL.
 * Cấu hình: sửa URL, USER, PASS cho phù hợp với máy của bạn.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/PhoneStoreDB?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root"; // Đ
    private static final String PASS = "tmd2601."; // Đổi mật khẩu MySQL của bạn ở đây

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy MySQL JDBC Driver! Hãy thêm mysql-connector-j vào thư mục lib/");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

