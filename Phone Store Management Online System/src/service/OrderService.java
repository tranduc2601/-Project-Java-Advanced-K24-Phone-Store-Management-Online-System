package service;

import dao.OrderDAO;
import dao.OrderDetailDAO;
import dao.ProductDAO;
import model.BestSellerItem;
import model.CartItem;
import model.Coupon;
import model.Order;
import model.OrderDetail;
import util.DatabaseConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public boolean checkout(int userId, List<CartItem> cart, String address, Coupon coupon) {
        if (cart == null || cart.isEmpty()) {
            System.out.println("Giỏ hàng trống! Không thể đặt hàng.");
            return false;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION


            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CartItem item : cart) {
                totalAmount = totalAmount.add(item.getSubTotal());
            }

            if (coupon != null && coupon.isValid()) {
                BigDecimal discountRate = BigDecimal.valueOf(coupon.getDiscountPercent())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal discountAmount = totalAmount.multiply(discountRate);
                totalAmount = totalAmount.subtract(discountAmount);
                System.out.println("Áp dụng mã giảm giá " + coupon.getCouponCode()
                        + " (-" + coupon.getDiscountPercent() + "%)");
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setDeliveryAddress(address);

            int orderId = orderDAO.createOrder(conn, order);
            if (orderId == -1) {
                throw new SQLException("Không thể tạo đơn hàng!");
            }

            for (CartItem item : cart) {
                OrderDetail detail = new OrderDetail(orderId, item.getProductId(),
                        item.getQuantity(), item.getUnitPrice());
                if (!orderDetailDAO.addDetail(conn, detail)) {
                    throw new SQLException("Không thể lưu chi tiết đơn hàng cho sản phẩm ID: " + item.getProductId());
                }

                if (!productDAO.deductStock(conn, item.getProductId(), item.getQuantity())) {
                    throw new SQLException("Không đủ hàng trong kho cho sản phẩm: " + item.getProductName());
                }
            }

            conn.commit();
            System.out.println("Đặt hàng thành công! Mã đơn hàng: #" + orderId);
            System.out.println("Tổng tiền: " + util.TablePrinter.formatCurrency(totalAmount));
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Đã rollback giao dịch do lỗi.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Lỗi khi đặt hàng: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Order> getOrdersByUserId(int userId) { return orderDAO.findByUserId(userId); }
    public List<Order> getAllOrders() { return orderDAO.findAll(); }
    public Order getOrderById(int orderId) { return orderDAO.findById(orderId); }
    public List<OrderDetail> getOrderDetails(int orderId) { return orderDetailDAO.findByOrderId(orderId); }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            System.out.println("Không tìm thấy đơn hàng với ID = " + orderId);
            return false;
        }

        String currentStatus = order.getOrderStatus();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            System.out.println("Không thể chuyển trạng thái từ " + currentStatus + " sang " + newStatus + "!");
            System.out.println("   Luồng hợp lệ: PENDING → SHIPPING → DELIVERED (hoặc → CANCELLED)");
            return false;
        }

        return orderDAO.updateStatus(orderId, newStatus);
    }

    private boolean isValidStatusTransition(String current, String next) {
        switch (current) {
            case "PENDING":
                return "SHIPPING".equals(next) || "CANCELLED".equals(next);
            case "SHIPPING":
                return "DELIVERED".equals(next) || "CANCELLED".equals(next);
            case "DELIVERED":
            case "CANCELLED":
                return false;
            default:
                return false;
        }
    }

    /**
     * Lấy Top 5 sản phẩm bán chạy nhất toàn thời gian bằng Stored Procedure (CallableStatement).
     */
    public List<BestSellerItem> getTop5BestSellers() {
        return orderDAO.getTop5BestSellers();
    }

    /**
     * Lấy Top 5 sản phẩm bán chạy nhất trong tháng hiện tại bằng PreparedStatement.
     */
    public List<BestSellerItem> getTop5BestSellersThisMonth() {
        return orderDAO.getTop5BestSellersThisMonth();
    }
}
