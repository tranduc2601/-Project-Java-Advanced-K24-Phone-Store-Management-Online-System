DROP DATABASE PhoneStoreDB;
CREATE DATABASE IF NOT EXISTS PhoneStoreDB;
USE PhoneStoreDB;

-- Bang nguoi dung, phan quyen Admin/Customer
CREATE TABLE Users (
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    full_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    phone      VARCHAR(15)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       ENUM('ADMIN', 'CUSTOMER') DEFAULT 'CUSTOMER',
    status     BIT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bang danh muc hang san xuat, ho tro xoa mem
CREATE TABLE Categories (
    category_id   INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description   TEXT,
    status        BIT DEFAULT 1
);

-- Bang san pham, luu gia/ton kho/mau/dung luong
CREATE TABLE Products (
    product_id       INT AUTO_INCREMENT PRIMARY KEY,
    product_name     VARCHAR(150) NOT NULL,
    category_id      INT NOT NULL,
    price            DECIMAL(18, 2) NOT NULL CHECK (price > 0),
    stock            INT NOT NULL CHECK (stock >= 0),
    color            VARCHAR(50),
    storage_capacity VARCHAR(50),
    status           BIT DEFAULT 1,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id)
);

-- Bang don hang, theo doi trang thai PENDING/SHIPPING/DELIVERED/CANCELLED
CREATE TABLE Orders (
    order_id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT NOT NULL,
    total_amount     DECIMAL(18, 2) NOT NULL DEFAULT 0,
    order_status     ENUM('PENDING', 'SHIPPING', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    order_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_address TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Bang chi tiet don hang, luu tung san pham trong don
CREATE TABLE OrderDetails (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id        INT NOT NULL,
    product_id      INT NOT NULL,
    quantity        INT NOT NULL CHECK (quantity > 0),
    unit_price      DECIMAL(18, 2) NOT NULL,
    FOREIGN KEY (order_id)   REFERENCES Orders(order_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

INSERT INTO Users (full_name, email, phone, password, role) VALUES
    ('System Admin', 'admin@phonestore.com', '0123456789', '123456', 'ADMIN'),
    ('Khach hang A', 'khacha@gmail.com',     '0987654321', '123456', 'CUSTOMER');

INSERT INTO Categories (category_name, description) VALUES
    ('Apple',   'Dien thoai iPhone chinh hang'),
    ('Samsung', 'Dien thoai Samsung Galaxy'),
    ('Xiaomi',  'Dien thoai Xiaomi chinh hang'),
    ('OPPO',    'Dien thoai OPPO chinh hang');

INSERT INTO Products (product_name, category_id, price, stock, color, storage_capacity) VALUES
    ('iPhone 15 Pro Max',        1, 29000000, 50, 'Titan Tu Nhien', '256GB'),
    ('iPhone 15 Plus',           1, 23000000, 30, 'Hong',           '128GB'),
    ('iPhone 16 Pro',            1, 31000000, 25, 'Titan Sa Mac',   '256GB'),
    ('Samsung Galaxy S24 Ultra', 2, 27000000, 40, 'Den',            '256GB'),
    ('Samsung Galaxy S24+',      2, 22000000, 35, 'Tim',            '256GB'),
    ('Samsung Galaxy A55',       2,  9500000, 60, 'Xanh',           '128GB'),
    ('Xiaomi 14 Ultra',          3, 23000000, 20, 'Den',            '512GB'),
    ('Xiaomi Redmi Note 13 Pro', 3,  7500000, 80, 'Xanh La',        '256GB'),
    ('OPPO Find X7 Ultra',       4, 25000000, 15, 'Den',            '256GB'),
    ('OPPO Reno 11',             4,  9900000, 45, 'Xanh Duong',     '256GB');
