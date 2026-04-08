package dao;

import model.Order;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho bảng Orders.
 */
public class OrderDAO {

    /**
     * Tạo đơn hàng mới (dùng trong Transaction - nhận Connection từ bên ngoài)
     * @return order_id được sinh tự động, hoặc -1 nếu lỗi
     */
    public int createOrder(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO Orders (user_id, total_amount, order_status, delivery_address) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, order.getUserId());
        ps.setBigDecimal(2, order.getTotalAmount());
        ps.setString(3, "PENDING");
        ps.setString(4, order.getDeliveryAddress());
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            return keys.getInt(1);
        }
        return -1;
    }

    /**
     * Lấy đơn hàng theo user_id (lịch sử mua hàng của khách)
     */
    public List<Order> findByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS customer_name FROM Orders o " +
                     "JOIN Users u ON o.user_id = u.user_id WHERE o.user_id = ? ORDER BY o.order_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy đơn hàng của khách: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Lấy tất cả đơn hàng (dành cho Admin)
     */
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS customer_name FROM Orders o " +
                     "JOIN Users u ON o.user_id = u.user_id ORDER BY o.order_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Tìm đơn hàng theo ID
     */
    public Order findById(int orderId) {
        String sql = "SELECT o.*, u.full_name AS customer_name FROM Orders o " +
                     "JOIN Users u ON o.user_id = u.user_id WHERE o.order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm đơn hàng: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cập nhật trạng thái đơn hàng
     * Luồng: PENDING -> SHIPPING -> DELIVERED (hoặc CANCELLED)
     */
    public boolean updateStatus(int orderId, String newStatus) {
        String sql = "UPDATE Orders SET order_status = ? WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
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
        try {
            o.setCustomerName(rs.getString("customer_name"));
        } catch (SQLException ignored) {}
        return o;
    }
}

