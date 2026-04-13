
package dao;

import model.Product;
import util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private static final String SELECT_WITH_CATEGORY =
            "SELECT p.*, c.category_name FROM Products p " +
            "LEFT JOIN Categories c ON p.category_id = c.category_id";

    public List<Product> findAllPaginated(int page, int pageSize) {
        List<Product> products = new ArrayList<>();
        String sql = SELECT_WITH_CATEGORY + " WHERE p.status = 1 ORDER BY p.product_id LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
        }
        return products;
    }

    public int countActive() {
        String sql = "SELECT COUNT(*) FROM Products WHERE status = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm sản phẩm: " + e.getMessage());
        }
        return 0;
    }

    public List<Product> findAllActive() {
        List<Product> products = new ArrayList<>();
        String sql = SELECT_WITH_CATEGORY + " WHERE p.status = 1 ORDER BY p.product_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy sản phẩm: " + e.getMessage());
        }
        return products;
    }

    public List<Product> findInStock() {
        List<Product> products = new ArrayList<>();
        String sql = SELECT_WITH_CATEGORY + " WHERE p.status = 1 AND p.stock > 0 ORDER BY p.product_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy sản phẩm còn hàng: " + e.getMessage());
        }
        return products;
    }

    public Product findById(int productId) {
        String sql = SELECT_WITH_CATEGORY + " WHERE p.product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm sản phẩm: " + e.getMessage());
        }
        return null;
    }

    public List<Product> searchByName(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = SELECT_WITH_CATEGORY + " WHERE p.status = 1 AND p.product_name LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm sản phẩm: " + e.getMessage());
        }
        return products;
    }

    public List<Product> findAllSortedByPrice(boolean ascending) {
        List<Product> products = new ArrayList<>();
        String sql = SELECT_WITH_CATEGORY + " WHERE p.status = 1 ORDER BY p.price " + (ascending ? "ASC" : "DESC");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi sắp xếp sản phẩm: " + e.getMessage());
        }
        return products;
    }

    public boolean add(Product product) {
        String sql = "INSERT INTO Products (product_name, category_id, price, stock, color, storage_capacity) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getProductName());
            ps.setInt(2, product.getCategoryId());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getColor());
            ps.setString(6, product.getStorageCapacity());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Product product) {
        String sql = "UPDATE Products SET product_name = ?, category_id = ?, price = ?, " +
                     "stock = ?, color = ?, storage_capacity = ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getProductName());
            ps.setInt(2, product.getCategoryId());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getColor());
            ps.setString(6, product.getStorageCapacity());
            ps.setInt(7, product.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int productId) {
        String sql = "UPDATE Products SET status = 0 WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return false;
    }

    public boolean deductStock(Connection conn, int productId, int quantity) throws SQLException {
        String sql = "UPDATE Products SET stock = stock - ? WHERE product_id = ? AND stock >= ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, quantity);
        ps.setInt(2, productId);
        ps.setInt(3, quantity);
        int affected = ps.executeUpdate();
        return affected > 0;
    }

    private Product mapResultSet(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setProductName(rs.getString("product_name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStock(rs.getInt("stock"));
        p.setColor(rs.getString("color"));
        p.setStorageCapacity(rs.getString("storage_capacity"));
        p.setStatus(rs.getBoolean("status"));
        try {
            p.setCategoryName(rs.getString("category_name"));
        } catch (SQLException ignored) {}
        return p;
    }
}

