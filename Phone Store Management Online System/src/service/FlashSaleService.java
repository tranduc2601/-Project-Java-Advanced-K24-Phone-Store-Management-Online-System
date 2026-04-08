package service;

import dao.FlashSaleDAO;
import model.FlashSale;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service xử lý nghiệp vụ Flash Sale (khuyến mãi giảm giá theo thời gian).
 */
public class FlashSaleService {
    private final FlashSaleDAO flashSaleDAO = new FlashSaleDAO();

    public List<FlashSale> getAllFlashSales() {
        return flashSaleDAO.findAll();
    }

    public List<FlashSale> getActiveFlashSales() {
        return flashSaleDAO.findAllActive();
    }

    public FlashSale getActiveFlashSaleForProduct(int productId) {
        return flashSaleDAO.findActiveByProductId(productId);
    }

    public boolean addFlashSale(int productId, int discountPercent, LocalDateTime startDate, LocalDateTime endDate) {
        if (discountPercent <= 0 || discountPercent > 100) {
            System.out.println("⚠️ Phần trăm giảm giá phải từ 1 đến 100!");
            return false;
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            System.out.println("⚠️ Thời gian không hợp lệ! Ngày kết thúc phải sau ngày bắt đầu.");
            return false;
        }

        FlashSale fs = new FlashSale(productId, discountPercent, startDate, endDate);
        return flashSaleDAO.add(fs);
    }

    public boolean deactivateFlashSale(int flashSaleId) {
        return flashSaleDAO.deactivate(flashSaleId);
    }
}

