# 📱 Phone Store Management Online System
## Hệ thống Quản lý Shop Bán Điện Thoại Online

---

## 📋 Mô tả dự án
Hệ thống quản lý cửa hàng điện thoại online được xây dựng bằng Java Console Application, kết nối MySQL Database. Áp dụng kiến trúc phân lớp 5 tầng.

## 🏗️ Kiến trúc phân lớp

```
src/
├── Main.java                    # Entry point
├── model/                       # Lớp 1: Model (Thực thể)
│   ├── User.java
│   ├── Category.java
│   ├── Product.java
│   ├── Order.java
│   ├── OrderDetail.java
│   ├── CartItem.java
│   ├── Coupon.java
│   └── FlashSale.java
├── dao/                         # Lớp 2: DAO (Tương tác DB)
│   ├── UserDAO.java
│   ├── CategoryDAO.java
│   ├── ProductDAO.java
│   ├── OrderDAO.java
│   ├── OrderDetailDAO.java
│   ├── CouponDAO.java
│   └── FlashSaleDAO.java
├── service/                     # Lớp 3: Service (Nghiệp vụ)
│   ├── AuthService.java
│   ├── CategoryService.java
│   ├── ProductService.java
│   ├── OrderService.java
│   ├── CouponService.java
│   └── FlashSaleService.java
├── presentation/                # Lớp 4: Presentation (Giao diện Console)
│   ├── AuthMenu.java
│   ├── AdminMenu.java
│   └── CustomerMenu.java
├── util/                        # Lớp 5: Util (Tiện ích chung)
│   ├── DatabaseConnection.java
│   ├── PasswordUtil.java
│   ├── InputValidator.java
│   └── TablePrinter.java
├── schema.sql                   # Script tạo Database
lib/
├── mysql-connector-j-8.3.0.jar  # MySQL JDBC Driver
└── jbcrypt-0.4.jar              # Thư viện mã hóa BCrypt
```

## 🚀 Hướng dẫn cài đặt

### Bước 1: Cài đặt MySQL
- Cài đặt MySQL Server (khuyến nghị 8.0+)
- Mở MySQL Workbench hoặc terminal MySQL

### Bước 2: Tạo Database
Chạy file `src/schema.sql` trong MySQL:
```sql
source đường_dẫn_đến/schema.sql
```

### Bước 3: Tải thư viện JAR
Tải 2 file JAR sau và đặt vào thư mục `lib/`:

1. **MySQL Connector/J** (JDBC Driver):
   - Tải tại: https://dev.mysql.com/downloads/connector/j/
   - Chọn "Platform Independent" → tải file `.zip` → giải nén lấy file `.jar`
   - Đổi tên thành: `mysql-connector-j-8.3.0.jar`

2. **jBCrypt** (Mã hóa mật khẩu):
   - Tải tại: https://www.mindrot.org/projects/jBCrypt/
   - Hoặc từ Maven Central: https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar
   - Tên file: `jbcrypt-0.4.jar`

### Bước 4: Cấu hình kết nối Database
Mở file `src/util/DatabaseConnection.java` và sửa thông tin:
```java
private static final String URL = "jdbc:mysql://localhost:3306/PhoneStoreDB";
private static final String USER = "root";      // Tên đăng nhập MySQL
private static final String PASS = "";           // Mật khẩu MySQL
```

### Bước 5: Chạy chương trình
- Mở project trong IntelliJ IDEA
- Đảm bảo thư viện JAR đã được thêm vào Project (File → Project Structure → Libraries)
- Chạy file `Main.java`

## 👤 Tài khoản mẫu

| Vai trò  | Email                    | Mật khẩu |
|----------|--------------------------|-----------|
| Admin    | admin@phonestore.com     | 123456    |
| Customer | khacha@gmail.com         | 123456    |

> **Lưu ý:** Mật khẩu trong DB đã được mã hóa BCrypt. Hệ thống cũng hỗ trợ so sánh mật khẩu plaintext (fallback) để thuận tiện test.

## 🔐 Bảo mật
- ✅ Mật khẩu mã hóa BCrypt
- ✅ PreparedStatement chống SQL Injection
- ✅ Exception Handling tránh crash
- ✅ Validation dữ liệu đầu vào

## 📦 Tính năng

### Module 1: Đăng nhập & Đăng ký
- Đăng nhập với email/mật khẩu
- Phân quyền Admin/Customer
- Đăng ký tài khoản mới (mã hóa BCrypt)

### Module 2: Quản lý Danh mục (Admin)
- CRUD danh mục
- Xóa mềm (Soft Delete)
- Kiểm tra trùng tên

### Module 3: Quản lý Sản phẩm (Admin)
- Danh sách có phân trang
- Thêm/Sửa/Xóa sản phẩm
- Tìm kiếm theo tên (LIKE)
- Sắp xếp theo giá

### Module 4: Mua hàng (Customer)
- Xem sản phẩm còn hàng
- Giỏ hàng (in-memory)
- Checkout với Transaction
- Tự động trừ tồn kho
- Áp dụng mã giảm giá
- Xem lịch sử đơn hàng

### Module 5: Quản lý Đơn hàng (Admin)
- Xem tất cả đơn hàng
- Cập nhật trạng thái (PENDING → SHIPPING → DELIVERED/CANCELLED)

### Module 6: Tính năng nâng cao
- ⚡ Flash Sale: Giảm giá theo % trong khoảng thời gian
- 🎫 Coupon: Mã giảm giá áp dụng khi checkout

