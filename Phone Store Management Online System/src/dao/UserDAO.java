package dao;

import model.User;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho bảng Users.
 * Tất cả câu lệnh SQL sử dụng PreparedStatement để chống SQL Injection.
 */
public class UserDAO {

    /**
     * Tìm người dùng theo email
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm người dùng theo email: " + e.getMessage());
        }
        return null;
    }

    /**
     * Tìm người dùng theo ID
     */
    public User findById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm người dùng theo ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Đăng ký người dùng mới (INSERT)
     */
    public boolean register(User user) {
        String sql = "INSERT INTO Users (full_name, email, phone, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("⚠️ Email hoặc số điện thoại đã tồn tại trong hệ thống!");
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đăng ký: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lấy danh sách tất cả người dùng
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY user_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách người dùng: " + e.getMessage());
        }
        return users;
    }

    /**
     * Cập nhật mật khẩu đã mã hóa cho người dùng (dùng khi cần hash lại mật khẩu cũ)
     */
    public boolean updatePassword(int userId, String hashedPassword) {
        String sql = "UPDATE Users SET password = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật mật khẩu: " + e.getMessage());
        }
        return false;
    }

    /**
     * Map ResultSet sang đối tượng User
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getBoolean("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}

