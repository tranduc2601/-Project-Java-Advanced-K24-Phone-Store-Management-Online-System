package service;

import dao.OrderDAO;
import dao.OrderDetailDAO;
import dao.ProductDAO;
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

/**
 * Service xử lý nghiệp vụ đặt hàng.
 * Quy trình checkout sử dụng Transaction để đảm bảo toàn vẹn dữ liệu.
 */
public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Xác nhận đặt hàng (Checkout) - Sử dụng Transaction.
     * Thực hiện đồng thời 3 việc:
     * (1) Lưu vào bảng Orders
     * (2) Lưu vào bảng OrderDetails
     * (3) Tự động trừ số lượng tồn kho
     *
     * @param userId   ID khách hàng
     * @param cart     Danh sách sản phẩm trong giỏ hàng
     * @param address  Địa chỉ giao hàng
     * @param coupon   Mã giảm giá (có thể null)
     * @return true nếu đặt hàng thành công
     */
    public boolean checkout(int userId, List<CartItem> cart, String address, Coupon coupon) {
        if (cart == null || cart.isEmpty()) {
            System.out.println("⚠️ Giỏ hàng trống! Không thể đặt hàng.");
            return false;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

            // Tính tổng tiền
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CartItem item : cart) {
                totalAmount = totalAmount.add(item.getSubTotal());
            }

            // Áp dụng coupon giảm giá nếu có
            if (coupon != null && coupon.isValid()) {
                BigDecimal discountRate = BigDecimal.valueOf(coupon.getDiscountPercent())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal discountAmount = totalAmount.multiply(discountRate);
                totalAmount = totalAmount.subtract(discountAmount);
                System.out.println("🎫 Áp dụng mã giảm giá " + coupon.getCouponCode()
                        + " (-" + coupon.getDiscountPercent() + "%)");
            }

            // (1) Tạo đơn hàng
            Order order = new Order();
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setDeliveryAddress(address);

            int orderId = orderDAO.createOrder(conn, order);
            if (orderId == -1) {
                throw new SQLException("Không thể tạo đơn hàng!");
            }

            // (2) Lưu chi tiết đơn hàng + (3) Trừ tồn kho
            for (CartItem item : cart) {
                // Lưu chi tiết
                OrderDetail detail = new OrderDetail(orderId, item.getProductId(),
                        item.getQuantity(), item.getUnitPrice());
                if (!orderDetailDAO.addDetail(conn, detail)) {
                    throw new SQLException("Không thể lưu chi tiết đơn hàng cho sản phẩm ID: " + item.getProductId());
                }

                // Trừ tồn kho
                if (!productDAO.deductStock(conn, item.getProductId(), item.getQuantity())) {
                    throw new SQLException("Không đủ hàng trong kho cho sản phẩm: " + item.getProductName());
                }
            }

            conn.commit(); // COMMIT TRANSACTION
            System.out.println("✅ Đặt hàng thành công! Mã đơn hàng: #" + orderId);
            System.out.println("💰 Tổng tiền: " + util.TablePrinter.formatCurrency(totalAmount));
            return true;

        } catch (SQLException e) {
            // ROLLBACK nếu có lỗi
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("⚠️ Đã rollback giao dịch do lỗi.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("❌ Lỗi khi đặt hàng: " + e.getMessage());
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

    /**
     * Lấy lịch sử đơn hàng của khách hàng
     */
    public List<Order> getOrdersByUserId(int userId) {
        return orderDAO.findByUserId(userId);
    }

    /**
     * Lấy tất cả đơn hàng (Admin)
     */
    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }

    /**
     * Lấy đơn hàng theo ID
     */
    public Order getOrderById(int orderId) {
        return orderDAO.findById(orderId);
    }

    /**
     * Lấy chi tiết đơn hàng
     */
    public List<OrderDetail> getOrderDetails(int orderId) {
        return orderDetailDAO.findByOrderId(orderId);
    }

    /**
     * Cập nhật trạng thái đơn hàng.
     * Phải tuân theo luồng: PENDING -> SHIPPING -> DELIVERED (hoặc CANCELLED)
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            System.out.println("⚠️ Không tìm thấy đơn hàng với ID = " + orderId);
            return false;
        }

        String currentStatus = order.getOrderStatus();

        // Validate luồng trạng thái
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            System.out.println("⚠️ Không thể chuyển trạng thái từ " + currentStatus + " sang " + newStatus + "!");
            System.out.println("   Luồng hợp lệ: PENDING → SHIPPING → DELIVERED (hoặc → CANCELLED)");
            return false;
        }

        return orderDAO.updateStatus(orderId, newStatus);
    }

    /**
     * Kiểm tra chuyển trạng thái hợp lệ
     */
    private boolean isValidStatusTransition(String current, String next) {
        switch (current) {
            case "PENDING":
                return "SHIPPING".equals(next) || "CANCELLED".equals(next);
            case "SHIPPING":
                return "DELIVERED".equals(next) || "CANCELLED".equals(next);
            case "DELIVERED":
            case "CANCELLED":
                return false; // Trạng thái cuối, không thể chuyển tiếp
            default:
                return false;
        }
    }
}

