
package dao;

import model.BestSellerItem;
import model.Order;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

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
            System.err.println("Lỗi khi lấy đơn hàng của khách: " + e.getMessage());
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
            while (rs.next()) {
                orders.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
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
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm đơn hàng: " + e.getMessage());
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
            System.err.println("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
        }
        return false;
    }

    /**
     * Gọi Stored Procedure sp_GetTop5BestSellers() qua CallableStatement.
     * Trả về Top 5 sản phẩm bán chạy nhất toàn thời gian (không tính đơn CANCELLED).
     */
    public List<BestSellerItem> getTop5BestSellers() {
        List<BestSellerItem> list = new ArrayList<>();
        String sql = "{CALL sp_GetTop5BestSellers()}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                list.add(new BestSellerItem(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("total_sold"),
                        rs.getBigDecimal("total_revenue")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi gọi SP báo cáo Top 5: " + e.getMessage());
        }
        return list;
    }

    /**
     * Truy vấn Top 5 sản phẩm bán chạy nhất trong tháng/năm hiện tại bằng PreparedStatement.
     * Lọc theo MONTH và YEAR của order_date, bỏ qua đơn CANCELLED.
     */
    public List<BestSellerItem> getTop5BestSellersThisMonth() {
        List<BestSellerItem> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, " +
                     "SUM(od.quantity) AS total_sold, " +
                     "SUM(od.quantity * od.unit_price) AS total_revenue " +
                     "FROM Products p " +
                     "JOIN OrderDetails od ON p.product_id = od.product_id " +
                     "JOIN Orders o ON od.order_id = o.order_id " +
                     "WHERE o.order_status != 'CANCELLED' " +
                     "  AND MONTH(o.order_date) = MONTH(CURRENT_DATE()) " +
                     "  AND YEAR(o.order_date) = YEAR(CURRENT_DATE()) " +
                     "GROUP BY p.product_id, p.product_name " +
                     "ORDER BY total_sold DESC " +
                     "LIMIT 5";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new BestSellerItem(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("total_sold"),
                        rs.getBigDecimal("total_revenue")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn Top 5 tháng này: " + e.getMessage());
        }
        return list;
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

