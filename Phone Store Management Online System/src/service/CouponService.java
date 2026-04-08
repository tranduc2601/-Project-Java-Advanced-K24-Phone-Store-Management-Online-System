package service;

import dao.CouponDAO;
import model.Coupon;
import util.InputValidator;

import java.time.LocalDate;
import java.util.List;

/**
 * Service xử lý nghiệp vụ mã giảm giá (Coupon).
 */
public class CouponService {
    private final CouponDAO couponDAO = new CouponDAO();

    /**
     * Xác thực và lấy coupon hợp lệ
     */
    public Coupon validateAndGet(String code) {
        if (!InputValidator.isNotEmpty(code)) {
            return null;
        }
        Coupon coupon = couponDAO.findByCode(code.trim().toUpperCase());
        if (coupon == null) {
            System.out.println("⚠️ Mã giảm giá không tồn tại!");
            return null;
        }
        if (!coupon.isStatus()) {
            System.out.println("⚠️ Mã giảm giá đã bị vô hiệu hóa!");
            return null;
        }
        if (!coupon.isValid()) {
            System.out.println("⚠️ Mã giảm giá đã hết hạn!");
            return null;
        }
        return coupon;
    }

    public List<Coupon> getAllCoupons() {
        return couponDAO.findAll();
    }

    public boolean addCoupon(String code, int discountPercent, LocalDate validUntil) {
        if (!InputValidator.isNotEmpty(code)) {
            System.out.println("⚠️ Mã coupon không được để trống!");
            return false;
        }
        if (discountPercent <= 0 || discountPercent > 100) {
            System.out.println("⚠️ Phần trăm giảm giá phải từ 1 đến 100!");
            return false;
        }
        if (validUntil == null || validUntil.isBefore(LocalDate.now())) {
            System.out.println("⚠️ Ngày hết hạn phải từ hôm nay trở đi!");
            return false;
        }

        Coupon coupon = new Coupon(code.trim().toUpperCase(), discountPercent, validUntil);
        return couponDAO.add(coupon);
    }

    public boolean deactivateCoupon(int couponId) {
        return couponDAO.deactivate(couponId);
    }
}

