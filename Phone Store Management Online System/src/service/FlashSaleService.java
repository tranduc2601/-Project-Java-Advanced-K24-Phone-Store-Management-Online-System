package service;

import dao.FlashSaleDAO;
import model.FlashSale;

import java.time.LocalDateTime;
import java.util.List;

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
            System.out.println("Phan tram giam gia phai tu 1 den 100!");
            return false;
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            System.out.println("Thoi gian khong hop le! Ngay ket thuc phai sau ngay bat dau.");
            return false;
        }

        FlashSale fs = new FlashSale(productId, discountPercent, startDate, endDate);
        return flashSaleDAO.add(fs);
    }

    public boolean deactivateFlashSale(int flashSaleId) {
        return flashSaleDAO.deactivate(flashSaleId);
    }
}
