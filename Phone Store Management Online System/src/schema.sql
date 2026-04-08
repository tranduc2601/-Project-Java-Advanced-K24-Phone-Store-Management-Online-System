-- 1. TẠO VÀ SỬ DỤNG DATABASE
CREATE DATABASE IF NOT EXISTS PhoneStoreDB;
USE PhoneStoreDB;

-- ==========================================
-- PHẦN 1: TẠO CÁC BẢNG (TABLES)
-- ==========================================

-- 2. Bảng Người dùng (Users)
-- Đáp ứng yêu cầu: Phân quyền Admin/Customer, lưu thông tin, mã hóa pass
CREATE TABLE Users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       phone VARCHAR(15) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL, -- Độ dài 255 để chứa chuỗi mã hóa BCrypt
                       role ENUM('ADMIN', 'CUSTOMER') DEFAULT 'CUSTOMER',
                       status BIT DEFAULT 1, -- 1: Hoạt động, 0: Bị khóa
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Bảng Danh mục (Categories)
-- Đáp ứng yêu cầu: Thêm, sửa, xóa mềm (Soft Delete)
CREATE TABLE Categories (
                            category_id INT AUTO_INCREMENT PRIMARY KEY,
                            category_name VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT,
                            status BIT DEFAULT 1 -- 1: Hiển thị, 0: Đã xóa (Soft delete)
);

-- 4. Bảng Sản phẩm (Products)
-- Đáp ứng yêu cầu: Quản lý kho, giá, validate số lượng > 0
CREATE TABLE Products (
                          product_id INT AUTO_INCREMENT PRIMARY KEY,
                          product_name VARCHAR(150) NOT NULL,
                          category_id INT NOT NULL,
                          price DECIMAL(18, 2) NOT NULL CHECK (price > 0),
                          stock INT NOT NULL CHECK (stock >= 0),
                          color VARCHAR(50),
                          storage_capacity VARCHAR(50), -- Ví dụ: 128GB, 256GB
                          status BIT DEFAULT 1, -- 1: Đang bán, 0: Ngừng kinh doanh
                          FOREIGN KEY (category_id) REFERENCES Categories(category_id)
);

-- 5. Bảng Đơn hàng (Orders)
-- Đáp ứng yêu cầu: Trạng thái đơn hàng, tổng tiền
CREATE TABLE Orders (
                        order_id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        total_amount DECIMAL(18, 2) NOT NULL DEFAULT 0,
                        order_status ENUM('PENDING', 'SHIPPING', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        delivery_address TEXT NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- 6. Bảng Chi tiết Đơn hàng (OrderDetails)
-- Lưu chi tiết từng mặt hàng trong đơn hàng
CREATE TABLE OrderDetails (
                              order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
                              order_id INT NOT NULL,
                              product_id INT NOT NULL,
                              quantity INT NOT NULL CHECK (quantity > 0),
                              unit_price DECIMAL(18, 2) NOT NULL,
                              FOREIGN KEY (order_id) REFERENCES Orders(order_id),
                              FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- Bảng phụ: Mã giảm giá (Coupon) - Tính năng nâng cao điểm cộng
CREATE TABLE Coupons (
                         coupon_id INT AUTO_INCREMENT PRIMARY KEY,
                         coupon_code VARCHAR(20) NOT NULL UNIQUE,
                         discount_percent INT NOT NULL CHECK (discount_percent > 0 AND discount_percent <= 100),
                         valid_until DATE,
                         status BIT DEFAULT 1
);

-- 8. Bảng Flash Sale (Khuyến mãi theo thời gian) - Tính năng nâng cao điểm cộng
CREATE TABLE FlashSales (
                            flash_sale_id INT AUTO_INCREMENT PRIMARY KEY,
                            product_id INT NOT NULL,
                            discount_percent INT NOT NULL CHECK (discount_percent > 0 AND discount_percent <= 100),
                            start_date DATETIME NOT NULL,
                            end_date DATETIME NOT NULL,
                            status BIT DEFAULT 1,
                            FOREIGN KEY (product_id) REFERENCES Products(product_id)
);


-- ==========================================
-- PHẦN 2: CHÈN DỮ LIỆU MẪU (DUMMY DATA)
-- ==========================================

-- Chèn Admin và Khách hàng mẫu
-- Mật khẩu đã được mã hóa BCrypt (mật khẩu gốc: 123456)
-- Nếu muốn test nhanh, hệ thống hỗ trợ so sánh plaintext fallback
INSERT INTO Users (full_name, email, phone, password, role) VALUES
    ('System Admin', 'admin@phonestore.com', '0123456789', '$2a$12$LJ3m4ys3Grghv0UyCsHxNOKXMQVDMnpHm2lSGqO2Kg96bDBMNwZO6', 'ADMIN'),
    ('Khách hàng A', 'khacha@gmail.com', '0987654321', '$2a$12$LJ3m4ys3Grghv0UyCsHxNOKXMQVDMnpHm2lSGqO2Kg96bDBMNwZO6', 'CUSTOMER');

-- Chèn Danh mục
INSERT INTO Categories (category_name, description) VALUES
    ('Apple', 'Điện thoại iPhone chính hãng'),
    ('Samsung', 'Điện thoại Samsung Galaxy'),
    ('Xiaomi', 'Điện thoại Xiaomi chính hãng'),
    ('OPPO', 'Điện thoại OPPO chính hãng');

-- Chèn Sản phẩm
INSERT INTO Products (product_name, category_id, price, stock, color, storage_capacity) VALUES
    ('iPhone 15 Pro Max', 1, 29000000, 50, 'Titan Tự Nhiên', '256GB'),
    ('iPhone 15 Plus', 1, 23000000, 30, 'Hồng', '128GB'),
    ('iPhone 16 Pro', 1, 31000000, 25, 'Titan Sa Mạc', '256GB'),
    ('Samsung Galaxy S24 Ultra', 2, 27000000, 40, 'Đen', '256GB'),
    ('Samsung Galaxy S24+', 2, 22000000, 35, 'Tím', '256GB'),
    ('Samsung Galaxy A55', 2, 9500000, 60, 'Xanh', '128GB'),
    ('Xiaomi 14 Ultra', 3, 23000000, 20, 'Đen', '512GB'),
    ('Xiaomi Redmi Note 13 Pro', 3, 7500000, 80, 'Xanh Lá', '256GB'),
    ('OPPO Find X7 Ultra', 4, 25000000, 15, 'Đen', '256GB'),
    ('OPPO Reno 11', 4, 9900000, 45, 'Xanh Dương', '256GB');

-- Chèn mã giảm giá mẫu
INSERT INTO Coupons (coupon_code, discount_percent, valid_until) VALUES
    ('WELCOME10', 10, '2026-12-31'),
    ('SALE20', 20, '2026-06-30'),
    ('VIP30', 30, '2026-12-31');


-- ==========================================
-- PHẦN 3: STORED PROCEDURE (Để dùng CallableStatement)
-- ==========================================

-- Thủ tục thống kê Top 5 sản phẩm bán chạy nhất (Để kiếm điểm cộng tính năng nâng cao)
DELIMITER //
CREATE PROCEDURE sp_GetTop5BestSellers()
BEGIN
SELECT
    p.product_id,
    p.product_name,
    SUM(od.quantity) AS total_sold,
    SUM(od.quantity * od.unit_price) AS total_revenue
FROM Products p
         JOIN OrderDetails od ON p.product_id = od.product_id
         JOIN Orders o ON od.order_id = o.order_id
WHERE o.order_status != 'CANCELLED'
GROUP BY p.product_id, p.product_name
ORDER BY total_sold DESC
    LIMIT 5;
END //
DELIMITER ;