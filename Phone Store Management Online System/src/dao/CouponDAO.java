
package dao;

import model.Coupon;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CouponDAO {

    public Coupon findByCode(String code) {
        String sql = "SELECT * FROM Coupons WHERE coupon_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm coupon: " + e.getMessage());
        }
        return null;
    }

    public List<Coupon> findAll() {
        List<Coupon> coupons = new ArrayList<>();
        String sql = "SELECT * FROM Coupons ORDER BY coupon_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                coupons.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách coupon: " + e.getMessage());
        }
        return coupons;
    }

    public boolean add(Coupon coupon) {
        String sql = "INSERT INTO Coupons (coupon_code, discount_percent, valid_until) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, coupon.getCouponCode());
            ps.setInt(2, coupon.getDiscountPercent());
            ps.setDate(3, Date.valueOf(coupon.getValidUntil()));
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Mã coupon đã tồn tại!");
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm coupon: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Coupon coupon) {
        String sql = "UPDATE Coupons SET coupon_code = ?, discount_percent = ?, valid_until = ?, status = ? WHERE coupon_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, coupon.getCouponCode());
            ps.setInt(2, coupon.getDiscountPercent());
            ps.setDate(3, Date.valueOf(coupon.getValidUntil()));
            ps.setBoolean(4, coupon.isStatus());
            ps.setInt(5, coupon.getCouponId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật coupon: " + e.getMessage());
        }
        return false;
    }

    public boolean deactivate(int couponId) {
        String sql = "UPDATE Coupons SET status = 0 WHERE coupon_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi vô hiệu hóa coupon: " + e.getMessage());
        }
        return false;
    }

    private Coupon mapResultSet(ResultSet rs) throws SQLException {
        Coupon c = new Coupon();
        c.setCouponId(rs.getInt("coupon_id"));
        c.setCouponCode(rs.getString("coupon_code"));
        c.setDiscountPercent(rs.getInt("discount_percent"));
        Date d = rs.getDate("valid_until");
        if (d != null) c.setValidUntil(d.toLocalDate());
        c.setStatus(rs.getBoolean("status"));
        return c;
    }
}

