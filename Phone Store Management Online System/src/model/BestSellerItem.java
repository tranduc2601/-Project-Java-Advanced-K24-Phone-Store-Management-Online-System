package model;

import java.math.BigDecimal;

public class BestSellerItem {
    private int productId;
    private String productName;
    private int totalSold;
    private BigDecimal totalRevenue;

    // Constructor đầy đủ
    public BestSellerItem(int productId, String productName, int totalSold, BigDecimal totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.totalSold = totalSold;
        this.totalRevenue = totalRevenue;
    }

    // Getters
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getTotalSold() { return totalSold; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
}

