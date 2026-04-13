package model;

import java.math.BigDecimal;

/**
 * Model chứa thông tin một dòng trong báo cáo Top sản phẩm bán chạy.
 * Dùng để nhận dữ liệu trả về từ Stored Procedure sp_GetTop5BestSellers().
 */
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

