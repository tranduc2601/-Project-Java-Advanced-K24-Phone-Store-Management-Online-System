package model;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private String productName;
    private int categoryId;
    private BigDecimal price;
    private int stock;
    private String color;
    private String storageCapacity;
    private boolean status;
    // Extra field for display (JOIN with Categories)
    private String categoryName;

    public Product() {}

    public Product(String productName, int categoryId, BigDecimal price, int stock,
                   String color, String storageCapacity) {
        this.productName = productName;
        this.categoryId = categoryId;
        this.price = price;
        this.stock = stock;
        this.color = color;
        this.storageCapacity = storageCapacity;
        this.status = true;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getStorageCapacity() { return storageCapacity; }
    public void setStorageCapacity(String storageCapacity) { this.storageCapacity = storageCapacity; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}

