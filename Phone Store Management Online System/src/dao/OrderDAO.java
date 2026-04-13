package dao;

import model.Order;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public int createOrder(Order order) {
        String sql = "INSERT INTO Orders (user_id, total_amount, order_status, delivery_address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            ps.setBigDecimal(2, order.getTotalAmount());
            ps.setString(3, "PENDING");
            ps.setString(4, order.getDeliveryAddress());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("Loi khi tao don hang: " + e.getMessage());
        }
        return -1;
    }

    public List<Order> findByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS customer_name FROM Orders o " +
                     "JOIN Users u ON o.user_id = u.user_id WHERE o.user_id = ? ORDER BY o.order_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) orders.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Loi khi lay don hang cua khach: " + e.getMessage());
        }
        return orders;
    }

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS customer_name FROM Orders o " +
                     "JOIN Users u ON o.user_id = u.user_id ORDER BY o.order_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) orders.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach don hang: " + e.getMessage());
        }
        return orders;
    }

    public Order findById(int orderId) {
        String sql = "SELECT o.*, u.full_name AS customer_name FROM Orders o " +
                     "JOIN Users u ON o.user_id = u.user_id WHERE o.order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("Loi khi tim don hang: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStatus(int orderId, String newStatus) {
        String sql = "UPDATE Orders SET order_status = ? WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi cap nhat trang thai don hang: " + e.getMessage());
        }
        return false;
    }

    private Order mapResultSet(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setUserId(rs.getInt("user_id"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setOrderStatus(rs.getString("order_status"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        o.setDeliveryAddress(rs.getString("delivery_address"));
        try { o.setCustomerName(rs.getString("customer_name")); } catch (SQLException ignored) {}
        return o;
    }
}
