package model;

import java.time.LocalDateTime;

public class FlashSale {
    private int flashSaleId;
    private int productId;
    private int discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean status;
    // Extra for display
    private String productName;

    public FlashSale() {}

    public FlashSale(int productId, int discountPercent, LocalDateTime startDate, LocalDateTime endDate) {
        this.productId = productId;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = true;
    }

    public int getFlashSaleId() { return flashSaleId; }
    public void setFlashSaleId(int flashSaleId) { this.flashSaleId = flashSaleId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status && now.isAfter(startDate) && now.isBefore(endDate);
    }
}

