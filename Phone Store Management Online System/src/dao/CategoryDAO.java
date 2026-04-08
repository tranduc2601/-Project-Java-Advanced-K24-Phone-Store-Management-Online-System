package dao;

import model.Category;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho bảng Categories.
 */
public class CategoryDAO {

    /**
     * Lấy tất cả danh mục (bao gồm cả đã ẩn - dành cho Admin)
     */
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categories ORDER BY category_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách danh mục: " + e.getMessage());
        }
        return categories;
    }

    /**
     * Lấy danh mục đang hoạt động (status = 1)
     */
    public List<Category> findActive() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE status = 1 ORDER BY category_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh mục hoạt động: " + e.getMessage());
        }
        return categories;
    }

    /**
     * Tìm danh mục theo ID
     */
    public Category findById(int categoryId) {
        String sql = "SELECT * FROM Categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm danh mục: " + e.getMessage());
        }
        return null;
    }

    /**
     * Tìm danh mục theo tên (kiểm tra trùng)
     */
    public Category findByName(String name) {
        String sql = "SELECT * FROM Categories WHERE category_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm danh mục theo tên: " + e.getMessage());
        }
        return null;
    }

    /**
     * Thêm danh mục mới
     */
    public boolean add(Category category) {
        String sql = "INSERT INTO Categories (category_name, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("⚠️ Tên danh mục đã tồn tại!");
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thêm danh mục: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật danh mục
     */
    public boolean update(Category category) {
        String sql = "UPDATE Categories SET category_name = ?, description = ? WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getCategoryId());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("⚠️ Tên danh mục đã tồn tại!");
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật danh mục: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa mềm danh mục (Soft delete - đổi status = 0)
     */
    public boolean softDelete(int categoryId) {
        String sql = "UPDATE Categories SET status = 0 WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi xóa mềm danh mục: " + e.getMessage());
        }
        return false;
    }

    /**
     * Khôi phục danh mục đã xóa mềm
     */
    public boolean restore(int categoryId) {
        String sql = "UPDATE Categories SET status = 1 WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi khôi phục danh mục: " + e.getMessage());
        }
        return false;
    }

    private Category mapResultSet(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setCategoryId(rs.getInt("category_id"));
        c.setCategoryName(rs.getString("category_name"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getBoolean("status"));
        return c;
    }
}

