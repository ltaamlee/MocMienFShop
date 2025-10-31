# XÂY DỰNG WEBSITE BÁN HOA ONLINE MỘC MIÊN

Thành viên nhóm 10:
23110312 - Lê Thị Thanh Tâm
23110330 - Nguyễn Phương Thi
23110347 - Đoàn Thị Thu Trang

# 📚 Tài Liệu Hướng Dẫn Xây Dựng và Phát Triển MocMienFShop

## 📋 Mục Lục

1. [Tổng Quan Dự Án](#1-tổng-quan-dự-án)
2. [Kiến Trúc Hệ Thống](#2-kiến-trúc-hệ-thống)
3. [Công Nghệ Sử Dụng](#3-công-nghệ-sử-dụng)
4. [Cấu Trúc Database](#4-cấu-trúc-database)
5. [Hướng Dẫn Cài Đặt](#5-hướng-dẫn-cài-đặt)
6. [Cấu Trúc Source Code](#6-cấu-trúc-source-code)

---

## 1. Tổng Quan Dự Án

### 1.1. Giới Thiệu

**MocMienFShop** là một ứng dụng thương mại điện tử chuyên bán hoa tươi trực tuyến, được xây dựng bằng Spring Boot. Hệ thống hỗ trợ đa vai trò người dùng: Admin, Customer, Vendor (Chủ cửa hàng), và Shipper (Người giao hàng).

### 1.2. Tính Năng Chính

- 🛍️ **Quản lý sản phẩm và cửa hàng**: Quản lý danh mục, sản phẩm, cửa hàng
- 🛒 **Giỏ hàng và thanh toán**: Hỗ trợ giỏ hàng, checkout, thanh toán MoMo và COD
- 📦 **Quản lý đơn hàng**: Theo dõi trạng thái đơn hàng từ NEW đến DELIVERED
- 🚚 **Vận chuyển**: Tính phí vận chuyển theo khoảng cách, quản lý shipper
- 💰 **Khuyến mãi**: Hệ thống khuyến mãi theo cửa hàng và toàn hệ thống
- ⭐ **Đánh giá sản phẩm**: Khách hàng có thể đánh giá và xem review
- 🔐 **Bảo mật**: JWT authentication, OAuth2 (Google), Spring Security
- 📧 **Thông báo**: Email notification, hệ thống thông báo trong ứng dụng

### 1.3. Các Vai Trò Người Dùng

| Vai Trò | Mô Tả | Quyền Hạn |
|---------|-------|-----------|
| **ADMIN** | Quản trị viên hệ thống | Quản lý toàn bộ hệ thống: users, stores, products, categories, promotions, deliveries |
| **CUSTOMER** | Khách hàng | Xem sản phẩm, thêm vào giỏ, đặt hàng, thanh toán, đánh giá sản phẩm |
| **VENDOR** | Chủ cửa hàng | Quản lý cửa hàng, sản phẩm, đơn hàng, khuyến mãi, doanh thu |
| **SHIPPER** | Người giao hàng | Nhận đơn hàng, cập nhật trạng thái giao hàng, quản lý ví điện tử |

---

## 2. Kiến Trúc Hệ Thống

### 2.1. Kiến Trúc Tổng Quan

```
┌─────────────────────────────────────────────────────────┐
│                    Frontend Layer                        │
│  (Thymeleaf Templates + CSS + JavaScript)               │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│                  Controller Layer                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐│
│  │  Admin   │  │ Customer │  │  Vendor  │  │ Shipper ││
│  │Controller│  │Controller│  │Controller│  │Controller││
│  └──────────┘  └──────────┘  └──────────┘  └─────────┘│
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│                   Service Layer                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐│
│  │   User       │  │   Product    │  │    Order     ││
│  │   Service    │  │   Service    │  │   Service   ││
│  └──────────────┘  └──────────────┘  └──────────────┘│
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐│
│  │   Payment    │  │  Shipping    │  │ Notification ││
│  │   Service    │  │   Service    │  │   Service   ││
│  └──────────────┘  └──────────────┘  └──────────────┘│
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│                  Repository Layer                        │
│  (Spring Data JPA - Repository Interfaces)             │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│                  Database Layer                          │
│              SQL Server Database                         │
└──────────────────────────────────────────────────────────┘
```

### 2.2. Luồng Xử Lý Request

```
Client Request
    ↓
Security Filter (JWT Authentication)
    ↓
Controller (Receive Request)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Database Access)
    ↓
SQL Server Database
    ↓
Repository (Return Data)
    ↓
Service (Process Data)
    ↓
Controller (Return Response)
    ↓
View (Thymeleaf Template)
    ↓
Client Response
```

---

## 3. Công Nghệ Sử Dụng

### 3.1. Backend

| Công Nghệ | Phiên Bản | Mục Đích |
|-----------|-----------|----------|
| **Java** | 21 | Ngôn ngữ lập trình chính |
| **Spring Boot** | 3.5.6 | Framework chính |
| **Spring Security** | - | Bảo mật, xác thực |
| **Spring Data JPA** | - | Truy cập database |
| **Hibernate** | - | ORM framework |
| **JWT (jjwt)** | 0.11.5 | Token authentication |
| **OAuth2 Client** | - | Google login |
| **Thymeleaf** | - | Template engine |
| **Lombok** | - | Giảm boilerplate code |

### 3.2. Frontend

| Công Nghệ | Mục Đích |
|-----------|----------|
| **HTML5/CSS3** | Giao diện người dùng |
| **JavaScript** | Tương tác client-side |
| **Bootstrap** (nếu có) | Responsive design |

### 3.3. Database

| Công Nghệ | Phiên Bản | Mục Đích |
|-----------|-----------|----------|
| **SQL Server** | - | Database chính |
| **H2** | - | Database cho testing |

### 3.4. Dịch Vụ Bên Thứ Ba

| Dịch Vụ | Mục Đích |
|---------|----------|
| **Cloudinary** | Lưu trữ và quản lý hình ảnh |
| **MoMo Payment Gateway** | Thanh toán trực tuyến |
| **Gmail SMTP** | Gửi email thông báo |

### 3.5. Build Tool

| Tool | Phiên Bản |
|------|-----------|
| **Maven** | - |

---

## 4. Cấu Trúc Database

### 4.1. Sơ Đồ Quan Hệ Database (ERD)

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│     Role     │         │     User     │         │ UserProfile  │
│──────────────│         │──────────────│         │──────────────│
│ id           │◄──┐     │ userId (PK)  │         │ id (PK)      │
│ roleName     │   │     │ username     │◄────────┤ userId (FK)  │
│ description  │   │     │ password     │    1:1  │ fullName     │
└──────────────┘   │     │ email        │         │ idCard       │
                   │     │ phone        │         │ dob          │
                   │     │ status       │         │ gender       │
                   │     │ roleId (FK)  │───┐     │ level (FK)   │
                   │     │ isActive     │   │     │ point        │
                   │     │ createdAt    │   │     │ eWallet      │
                   │     │ updatedAt    │   │     └──────────────┘
                   │     │ lastLoginAt  │   │
                   │     └──────────────┘   │     ┌──────────────┐
                   │                        │     │     Level    │
                   │                        │     │──────────────│
                   │                        │     │ id (PK)      │
                   │                        └─────┤ levelName    │
                   │                              │ discountRate │
                   │                              │ minPoint     │
                   └──────────────────────────────┤ maxPoint     │
                                                   └──────────────┘

┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   Category   │         │   Product    │         │ ProductImage │
│──────────────│         │──────────────│         │──────────────│
│ id (PK)      │         │ id (PK)      │         │ id (PK)      │
│ categoryName │◄──┐     │ categoryId   │         │ productId(FK)│
│ slug         │   │     │ storeId (FK) │───┐     │ imageUrl     │
│ description  │   │     │ productName  │   │     │ imageIndex   │
│ image        │   │     │ slug         │   │     └──────────────┘
└──────────────┘   │     │ price        │   │
                   │     │ promotional  │   │     ┌──────────────┐
                   │     │ Price        │   │     │    Review    │
                   │     │ size         │   │     │──────────────│
                   └─────┤ stock        │   │     │ id (PK)      │
                         │ sold         │   │     │ productId(FK)│
                         │ rating       │   │     │ customerId   │
                         │ isActive     │   │     │ rating       │
                         │ isSelling    │   │     │ comment      │
                         │ isAvailable  │   │     │ createdAt    │
                         │ createdAt    │   │     └──────────────┘
                         │ updatedAt    │   │
                         └──────────────┘   │
                                            │
                         ┌──────────────┐   │
                         │    Store    │   │
                         │──────────────│   │
                         │ id (PK)      │◄──┘
                         │ vendorId(FK) │
                         │ level (FK)  │
                         │ storeName   │
                         │ phone       │
                         │ address     │
                         │ latitude    │
                         │ longitude   │
                         │ avatar      │
                         │ cover       │
                         │ slug        │
                         └──────────────┘

┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│     Cart    │         │   CartItem   │         │    Orders    │
│──────────────│         │──────────────│         │──────────────│
│ id (PK)      │         │ id (PK)      │         │ id (PK)      │
│ customerId   │◄──┐     │ cartId (FK)  │         │ customerId   │
│ createdAt    │   │     │ productId(FK)│         │ storeId (FK) │
│ updatedAt    │   │     │ quantity     │         │ promotionId  │
└──────────────┘   │     │ createdAt    │         │ deliveryId   │
                   │     │ updatedAt    │         │ shipperId    │
                   └─────┤              │         │ status       │
                         └──────────────┘         │ isPaid       │
                                                  │ paymentMethod│
┌──────────────┐         ┌──────────────┐         │ amountFrom   │
│ OrderDetail │         │  Promotion  │         │   Customer   │
│──────────────│         │──────────────│         │ shippingFee  │
│ id (PK)      │         │ id (PK)      │         │ note         │
│ orderId (FK) │◄──┐     │ promotionType│         │ createdAt    │
│ productId(FK)│   │     │ discountType │         │ updatedAt    │
│ quantity     │   │     │ discountValue│         └──────────────┘
│ price        │   │     │ startDate    │
│ promotional  │   │     │ endDate      │
│ Price        │   │     │ isActive     │
└──────────────┘   │     └──────────────┘
                   │
                   └──────────────────┘

┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   Delivery   │         │   Shipper    │         │CustomerAddress│
│──────────────│         │──────────────│         │──────────────│
│ id (PK)      │         │ id (PK)      │         │ id (PK)      │
│ deliveryName │         │ userId (FK)  │         │ customerId(FK)│
│ minDistance  │         │ phone        │         │ receiverName │
│ maxDistance  │         │ vehicleType  │         │ phone        │
│ pricePerKm   │         │ isOnline     │         │ address      │
│              │         │ eWallet      │         │ latitude     │
└──────────────┘         └──────────────┘         │ longitude    │
                                                   │ isDefault    │
                                                   └──────────────┘
```

### 4.2. Mô Tả Các Bảng Chính

#### 4.2.1. Bảng Users
- **Mục đích**: Lưu thông tin tài khoản người dùng
- **Các trường chính**:
  - `userId`: Khóa chính
  - `username`: Tên đăng nhập (unique)
  - `password`: Mật khẩu (đã mã hóa)
  - `email`: Email (unique)
  - `phone`: Số điện thoại
  - `status`: Trạng thái (ONLINE/OFFLINE)
  - `isActive`: Tài khoản có bị khóa không
  - `roleId`: Vai trò (FK → Role)
  - `code`: Mã OTP/verification

#### 4.2.2. Bảng UserProfile
- **Mục đích**: Lưu thông tin chi tiết của người dùng
- **Quan hệ**: 1:1 với Users
- **Các trường chính**:
  - `id`: Khóa chính
  - `userId`: Khóa ngoại đến Users
  - `fullName`: Họ và tên
  - `idCard`: CMND/CCCD
  - `dob`: Ngày sinh
  - `gender`: Giới tính
  - `level`: Cấp độ khách hàng (FK → Level)
  - `point`: Điểm tích lũy
  - `eWallet`: Ví điện tử

#### 4.2.3. Bảng Product
- **Mục đích**: Lưu thông tin sản phẩm
- **Quan hệ**: Nhiều sản phẩm thuộc 1 Category, 1 Store
- **Các trường chính**:
  - `id`: Khóa chính
  - `categoryId`: Danh mục (FK)
  - `storeId`: Cửa hàng (FK)
  - `productName`: Tên sản phẩm
  - `slug`: URL-friendly name
  - `price`: Giá gốc
  - `promotionalPrice`: Giá khuyến mãi
  - `stock`: Tồn kho
  - `sold`: Đã bán
  - `rating`: Đánh giá trung bình
  - `isActive`: Được phép bán
  - `isSelling`: Đang mở bán
  - `isAvailable`: Còn hàng

#### 4.2.4. Bảng Orders
- **Mục đích**: Lưu thông tin đơn hàng
- **Quan hệ**: 1 đơn hàng thuộc 1 Customer, 1 Store, có thể có Promotion, Delivery, Shipper
- **Các trường chính**:
  - `id`: Mã đơn hàng (format: ORD-yyyyMMdd-XXXXXX)
  - `customerId`: Khách hàng (FK → UserProfile)
  - `storeId`: Cửa hàng (FK → Store)
  - `promotionId`: Khuyến mãi (FK → Promotion, nullable)
  - `deliveryId`: Đơn vị vận chuyển (FK → Delivery, nullable)
  - `shipperId`: Người giao hàng (FK → Shipper, nullable)
  - `status`: Trạng thái (NEW, PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELED, RETURNED_REFUNDED)
  - `isPaid`: Đã thanh toán chưa
  - `paymentMethod`: Phương thức thanh toán (COD, MOMO)
  - `amountFromCustomer`: Tổng khách hàng trả
  - `shippingFee`: Phí vận chuyển
  - `note`: Ghi chú

#### 4.2.5. Bảng Store
- **Mục đích**: Lưu thông tin cửa hàng
- **Quan hệ**: 1 Store thuộc 1 Vendor (User), có 1 Level
- **Các trường chính**:
  - `id`: Khóa chính
  - `vendorId`: Chủ cửa hàng (FK → User)
  - `level`: Hạng cửa hàng (FK → Level)
  - `storeName`: Tên cửa hàng
  - `address`: Địa chỉ
  - `latitude`, `longitude`: Tọa độ GPS
  - `phone`: Số điện thoại
  - `avatar`: Ảnh đại diện
  - `cover`: Ảnh bìa
  - `slug`: URL-friendly name

#### 4.2.6. Bảng Cart & CartItem
- **Mục đích**: Quản lý giỏ hàng
- **Quan hệ**: 1 Cart có nhiều CartItem
- **Các trường chính**:
  - `Cart`: `customerId` (FK → UserProfile)
  - `CartItem`: `cartId` (FK → Cart), `productId` (FK → Product), `quantity`

#### 4.2.7. Bảng Promotion
- **Mục đích**: Quản lý khuyến mãi
- **Các trường chính**:
  - `id`: Khóa chính
  - `promotionType`: Loại khuyến mãi (STORE, SYSTEM)
  - `discountType`: Kiểu giảm giá (PERCENTAGE, FIXED_AMOUNT)
  - `discountValue`: Giá trị giảm
  - `startDate`, `endDate`: Thời gian hiệu lực
  - `isActive`: Còn hoạt động

#### 4.2.8. Bảng Delivery
- **Mục đích**: Quản lý đơn vị vận chuyển
- **Các trường chính**:
  - `id`: Khóa chính
  - `deliveryName`: Tên đơn vị
  - `minDistance`, `maxDistance`: Khoảng cách phục vụ (km)
  - `pricePerKm`: Giá mỗi km

#### 4.2.9. Bảng Shipper
- **Mục đích**: Quản lý người giao hàng
- **Quan hệ**: 1 Shipper thuộc 1 User
- **Các trường chính**:
  - `id`: Khóa chính
  - `userId`: Tài khoản (FK → User)
  - `phone`: Số điện thoại
  - `vehicleType`: Loại xe
  - `isOnline`: Đang online
  - `eWallet`: Ví điện tử

---

## 5. Hướng Dẫn Cài Đặt

### 5.1. Yêu Cầu Hệ Thống

- **Java**: JDK 21 trở lên
- **Maven**: 3.6+ 
- **SQL Server**: 2019+ hoặc SQL Server Express
- **IDE**: IntelliJ IDEA / Eclipse / VS Code

### 5.2. Cài Đặt SQL Server

1. Tải và cài đặt SQL Server từ [Microsoft](https://www.microsoft.com/sql-server)
2. Tạo database tên `MocMien`:
   ```sql
   CREATE DATABASE MocMien;
   ```
3. Cấu hình SQL Server Authentication (Mixed Mode) nếu cần

### 5.3. Cấu Hình Environment Variables

Tạo file `.env` hoặc cấu hình trong `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MocMien;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=your_password

# JWT
app.jwtSecret=your_secret_key_here
app.jwtExpirationMs=86400000

# Google OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# MoMo Payment
momo.partnerCode=your_partner_code
momo.accessKey=your_access_key
momo.secretKey=your_secret_key
momo.requestUrl=https://test-payment.momo.vn/v2/gateway/api/create
momo.notifyUrl=your_callback_url
momo.returnUrl=http://localhost:8090/checkout/momo/return

# Email
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### 5.4. Clone và Build Project

```bash
# Clone repository
git clone <repository-url>
cd MocMienFShop

# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

Hoặc sử dụng Maven Wrapper:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### 5.5. Truy Cập Ứng Dụng

- **URL**: http://localhost:8090
- **Port**: 8090 (có thể thay đổi trong `application.properties`)

---

## 6. Cấu Trúc Source Code

### 6.1. Cấu Trúc Thư Mục

```
MocMienFShop/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── mocmien/
│   │   │       └── com/
│   │   │           ├── config/              # Cấu hình (Security, WebSocket, etc.)
│   │   │           ├── controller/          # Controllers
│   │   │           │   ├── admin/           # Admin controllers
│   │   │           │   ├── auth/           # Authentication
│   │   │           │   ├── customer/        # Customer controllers
│   │   │           │   ├── payment/         # Payment (MoMo)
│   │   │           │   ├── shipper/        # Shipper controllers
│   │   │           │   └── vendor/          # Vendor controllers
│   │   │           ├── dto/                 # Data Transfer Objects
│   │   │           │   ├── request/         # Request DTOs
│   │   │           │   └── response/        # Response DTOs
│   │   │           ├── entity/              # JPA Entities
│   │   │           ├── enums/               # Enumerations
│   │   │           ├── exception/          # Custom exceptions
│   │   │           ├── integration/         # External integrations
│   │   │           ├── repository/          # JPA Repositories
│   │   │           ├── security/            # Security (JWT, UserDetails)
│   │   │           ├── service/             # Business logic
│   │   │           │   └── impl/           # Service implementations
│   │   │           └── util/               # Utility classes
│   │   └── resources/
│   │       ├── application.properties       # Configuration
│   │       ├── static/                     # CSS, JS, images
│   │       │   └── styles/
│   │       └── templates/                  # Thymeleaf templates
│   │           ├── admin/                  # Admin views
│   │           ├── auth/                   # Auth views
│   │           ├── customer/               # Customer views
│   │           ├── shipper/                # Shipper views
│   │           └── vendor/                # Vendor views
│   └── test/                               # Test files
├── pom.xml                                 # Maven dependencies
└── README.md                               # This file
```


