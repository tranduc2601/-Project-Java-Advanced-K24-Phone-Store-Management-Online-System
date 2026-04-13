package service;

import dao.CouponDAO;
import model.Coupon;
import util.InputValidator;

import java.time.LocalDate;
import java.util.List;

public class CouponService {
    private final CouponDAO couponDAO = new CouponDAO();

    public Coupon validateAndGet(String code) {
        if (!InputValidator.isNotEmpty(code)) return null;

        Coupon coupon = couponDAO.findByCode(code.trim().toUpperCase());
        if (coupon == null) {
            System.out.println("Ma giam gia khong ton tai!");
            return null;
        }
        if (!coupon.isStatus()) {
            System.out.println("Ma giam gia da bi vo hieu hoa!");
            return null;
        }
        if (!coupon.isValid()) {
            System.out.println("Ma giam gia da het han!");
            return null;
        }
        return coupon;
    }

    public List<Coupon> getAllCoupons() {
        return couponDAO.findAll();
    }

    public boolean addCoupon(String code, int discountPercent, LocalDate validUntil) {
        if (!InputValidator.isNotEmpty(code)) {
            System.out.println("Ma coupon khong duoc de trong!");
            return false;
        }
        if (discountPercent <= 0 || discountPercent > 100) {
            System.out.println("Phan tram giam gia phai tu 1 den 100!");
            return false;
        }
        if (validUntil == null || validUntil.isBefore(LocalDate.now())) {
            System.out.println("Ngay het han phai tu hom nay tro di!");
            return false;
        }

        Coupon coupon = new Coupon(code.trim().toUpperCase(), discountPercent, validUntil);
        return couponDAO.add(coupon);
    }

    public boolean deactivateCoupon(int couponId) {
        return couponDAO.deactivate(couponId);
    }
}
