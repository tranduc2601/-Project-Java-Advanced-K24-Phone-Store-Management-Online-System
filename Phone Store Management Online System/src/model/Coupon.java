package model;

import java.time.LocalDate;

public class Coupon {
    private int couponId;
    private String couponCode;
    private int discountPercent;
    private LocalDate validUntil;
    private boolean status;

    public Coupon() {}

    public Coupon(String couponCode, int discountPercent, LocalDate validUntil) {
        this.couponCode = couponCode;
        this.discountPercent = discountPercent;
        this.validUntil = validUntil;
        this.status = true;
    }

    public int getCouponId() { return couponId; }
    public void setCouponId(int couponId) { this.couponId = couponId; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public boolean isValid() {
        return status && validUntil != null && !validUntil.isBefore(LocalDate.now());
    }
}

