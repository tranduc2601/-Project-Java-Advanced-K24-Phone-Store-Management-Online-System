package dao;

import model.OrderDetail;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho bảng OrderDetails.
 */
public class OrderDetailDAO {

    /**
     * Thêm chi tiết đơn hàng (dùng trong Transaction - nhận Connection từ bên ngoài)
     */
    public boolean addDetail(Connection conn, OrderDetail detail) throws SQLException {
        String sql = "INSERT INTO OrderDetails (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, detail.getOrderId());
        ps.setInt(2, detail.getProductId());
        ps.setInt(3, detail.getQuantity());
        ps.setBigDecimal(4, detail.getUnitPrice());
        return ps.executeUpdate() > 0;
    }

    /**
     * Lấy chi tiết đơn hàng theo order_id
     */
    public List<OrderDetail> findByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT od.*, p.product_name FROM OrderDetails od " +
                     "JOIN Products p ON od.product_id = p.product_id WHERE od.order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderDetail d = new OrderDetail();
                d.setOrderDetailId(rs.getInt("order_detail_id"));
                d.setOrderId(rs.getInt("order_id"));
                d.setProductId(rs.getInt("product_id"));
                d.setQuantity(rs.getInt("quantity"));
                d.setUnitPrice(rs.getBigDecimal("unit_price"));
                d.setProductName(rs.getString("product_name"));
                details.add(d);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
        }
        return details;
    }
}

