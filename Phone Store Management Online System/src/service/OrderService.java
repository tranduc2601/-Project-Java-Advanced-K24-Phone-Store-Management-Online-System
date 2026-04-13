package service;

import dao.OrderDAO;
import dao.OrderDetailDAO;
import dao.ProductDAO;
import model.CartItem;
import model.Order;
import model.OrderDetail;
import util.TablePrinter;

import java.math.BigDecimal;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public boolean checkout(int userId, List<CartItem> cart, String address) {
        if (cart == null || cart.isEmpty()) {
            System.out.println("Gio hang trong! Khong the dat hang.");
            return false;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cart) {
            totalAmount = totalAmount.add(item.getSubTotal());
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setDeliveryAddress(address);

        int orderId = orderDAO.createOrder(order);
        if (orderId == -1) {
            System.out.println("Khong the tao don hang!");
            return false;
        }

        for (CartItem item : cart) {
            OrderDetail detail = new OrderDetail(orderId, item.getProductId(),
                    item.getQuantity(), item.getUnitPrice());
            if (!orderDetailDAO.addDetail(detail)) {
                System.out.println("Loi khi luu chi tiet don hang: " + item.getProductName());
                return false;
            }
            if (!productDAO.deductStock(item.getProductId(), item.getQuantity())) {
                System.out.println("Khong du hang cho san pham: " + item.getProductName());
                return false;
            }
        }

        System.out.println("Dat hang thanh cong! Ma don hang: #" + orderId);
        System.out.println("Tong tien: " + TablePrinter.formatCurrency(totalAmount));
        return true;
    }

    public List<Order> getOrdersByUserId(int userId) { return orderDAO.findByUserId(userId); }
    public List<Order> getAllOrders() { return orderDAO.findAll(); }
    public Order getOrderById(int orderId) { return orderDAO.findById(orderId); }
    public List<OrderDetail> getOrderDetails(int orderId) { return orderDetailDAO.findByOrderId(orderId); }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            System.out.println("Khong tim thay don hang voi ID = " + orderId);
            return false;
        }
        if (!isValidTransition(order.getOrderStatus(), newStatus)) {
            System.out.println("Khong the chuyen trang thai tu " + order.getOrderStatus() + " sang " + newStatus + "!");
            System.out.println("Luong hop le: PENDING -> SHIPPING -> DELIVERED (hoac -> CANCELLED)");
            return false;
        }
        return orderDAO.updateStatus(orderId, newStatus);
    }

    private boolean isValidTransition(String current, String next) {
        switch (current) {
            case "PENDING":  return "SHIPPING".equals(next)   || "CANCELLED".equals(next);
            case "SHIPPING": return "DELIVERED".equals(next)  || "CANCELLED".equals(next);
            default: return false;
        }
    }
}
