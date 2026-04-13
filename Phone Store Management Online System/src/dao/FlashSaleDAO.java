
package dao;

import model.FlashSale;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlashSaleDAO {

    public List<FlashSale> findAll() {
        List<FlashSale> sales = new ArrayList<>();
        String sql = "SELECT fs.*, p.product_name FROM FlashSales fs " +
                     "JOIN Products p ON fs.product_id = p.product_id ORDER BY fs.flash_sale_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sales.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Lỗi khi lấy danh sách Flash Sale: " + e.getMessage());
        }
        return sales;
    }

    public FlashSale findActiveByProductId(int productId) {
        String sql = "SELECT fs.*, p.product_name FROM FlashSales fs " +
                     "JOIN Products p ON fs.product_id = p.product_id " +
                     "WHERE fs.product_id = ? AND fs.status = 1 AND fs.start_date <= NOW() AND fs.end_date >= NOW()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println(" Lỗi khi tìm Flash Sale: " + e.getMessage());
        }
        return null;
    }

    public List<FlashSale> findAllActive() {
        List<FlashSale> sales = new ArrayList<>();
        String sql = "SELECT fs.*, p.product_name FROM FlashSales fs " +
                     "JOIN Products p ON fs.product_id = p.product_id " +
                     "WHERE fs.status = 1 AND fs.start_date <= NOW() AND fs.end_date >= NOW()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sales.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Lỗi khi lấy Flash Sale hoạt động: " + e.getMessage());
        }
        return sales;
    }

    public boolean add(FlashSale flashSale) {
        String sql = "INSERT INTO FlashSales (product_id, discount_percent, start_date, end_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flashSale.getProductId());
            ps.setInt(2, flashSale.getDiscountPercent());
            ps.setTimestamp(3, Timestamp.valueOf(flashSale.getStartDate()));
            ps.setTimestamp(4, Timestamp.valueOf(flashSale.getEndDate()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm Flash Sale: " + e.getMessage());
        }
        return false;
    }

    public boolean deactivate(int flashSaleId) {
        String sql = "UPDATE FlashSales SET status = 0 WHERE flash_sale_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flashSaleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi vô hiệu hóa Flash Sale: " + e.getMessage());
        }
        return false;
    }

    private FlashSale mapResultSet(ResultSet rs) throws SQLException {
        FlashSale fs = new FlashSale();
        fs.setFlashSaleId(rs.getInt("flash_sale_id"));
        fs.setProductId(rs.getInt("product_id"));
        fs.setDiscountPercent(rs.getInt("discount_percent"));
        Timestamp start = rs.getTimestamp("start_date");
        if (start != null) fs.setStartDate(start.toLocalDateTime());
        Timestamp end = rs.getTimestamp("end_date");
        if (end != null) fs.setEndDate(end.toLocalDateTime());
        fs.setStatus(rs.getBoolean("status"));
        try {
            fs.setProductName(rs.getString("product_name"));
        } catch (SQLException ignored) {}
        return fs;
    }
}

