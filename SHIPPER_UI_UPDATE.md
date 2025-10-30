# 🚀 Shipper UI - Cập nhật giao diện hoàn toàn mới

## ✅ Đã hoàn thành

### 1. **Header (Thanh đầu trang)**
**File:** `src/main/resources/templates/shipper/fragments/header.html`
**CSS:** `src/main/resources/static/styles/css/shipper/header.css`

**Tính năng:**
- ✨ Gradient background đẹp (tím-xanh)
- 🖼️ Logo + tên brand "Shipper"
- 👤 Thông tin user (tên, avatar, role)
- 📱 Responsive tốt (ẩn text trên mobile nhỏ)
- 🎨 Sticky header để luôn hiển thị khi scroll

**Màu sắc chính:** `#667eea` → `#764ba2` (gradient)

---

### 2. **Footer/Navigation (Thanh điều hướng dưới)**
**File:** `src/main/resources/templates/shipper/fragments/sidebar.html`
**CSS:** `src/main/resources/static/styles/css/shipper/sidebar.css`

**Tính năng:**
- 📍 Fixed bottom navigation (mobile-first design)
- 🏠 4 menu chính:
  - **Trang chủ** (Dashboard)
  - **Đơn hàng** (Orders)
  - **Ví** (eWallet)
  - **Cá nhân** (Profile)
- 🎯 Active state rõ ràng (màu tím khi đang ở trang)
- ✨ Icon + Text, hover effect mượt mà
- 📱 Responsive: ẩn text trên màn hình siêu nhỏ

---

### 3. **Layout chung**
**File:** `src/main/resources/templates/shipper/layout.html`

**Cập nhật:**
- Import CSS header + sidebar
- Import Font Awesome 6.5.1
- Thêm padding-bottom cho body (tránh bị che bởi bottom nav)
- Reset CSS cơ bản (*, box-sizing)
- Base styles cho `.page`, `.mobile-card`, `.btn`
- Toggle switch CSS đẹp

**Màu nền:** `#f3f4f6` (xám nhạt)

---

### 4. **Trang Dashboard**
**File:** `src/main/resources/templates/shipper/dashboard.html`
**CSS:** `src/main/resources/static/styles/css/shipper/dashboard.css`

**Tính năng:**
- 👋 Welcome message với tên shipper
- 📊 **3 stat cards:**
  1. Đơn nhận hôm nay (tím)
  2. Đã giao thành công (xanh lá)
  3. Thu nhập hôm nay (vàng)
- 📦 Section "Đơn hàng sắp tới"
  - Empty state khi không có đơn
  - Hiển thị: mã đơn, khách hàng, địa chỉ, status
  - Link đến trang order
- 🎨 Hover effects, border-left màu sắc
- 📱 Grid responsive (3 cột → 1 cột mobile)

---

### 5. **Trang Profile**
**File:** `src/main/resources/templates/shipper/profile.html`

**Tính năng:**
- 🎨 **Profile Header Card:**
  - Gradient background (giống header)
  - Avatar lớn (100px, bo tròn, viền trắng)
  - Tên + role shipper
  - 3 stats mini (Đơn giao, Đánh giá, Thành công %)
- 📝 **Form cập nhật thông tin:**
  - Biển số xe
  - Loại phương tiện
  - Giấy phép lái xe
  - Nút "Lưu thay đổi"
- 🚪 Nút đăng xuất (màu đỏ)
- ✨ Focus state đẹp cho input (border tím + shadow)

---

### 6. **Trang Order (app.html)**
**File:** `src/main/resources/templates/shipper/app.html`
**JavaScript:** `src/main/resources/static/styles/js/shipper/app.js`

**Đã có sẵn từ lần cập nhật trước:**
- Toggle trạng thái trực tuyến/ngoại tuyến
- Bản đồ GPS
- Danh sách "Đơn có thể nhận"
- Danh sách "Đơn đang giao"
- Các nút hành động (Nhận đơn, Bắt đầu giao, Giao thành công, Hoàn hàng)

---

## 🎨 Bảng màu chính

| Màu | Hex | Sử dụng |
|-----|-----|---------|
| Primary (Tím) | `#667eea` | Header, buttons, links, active states |
| Primary Dark | `#5568d3` | Hover states |
| Secondary (Tím đậm) | `#764ba2` | Gradient end |
| Success (Xanh lá) | `#10b981` | Success states, delivered status |
| Warning (Vàng) | `#f59e0b` | Warning states, income |
| Danger (Đỏ) | `#ef4444` | Logout, cancel actions |
| Gray 50 | `#f9fafb` | Background light |
| Gray 100 | `#f3f4f6` | Main background |
| Gray 600 | `#4b5563` | Secondary text |
| Gray 900 | `#111827` | Primary text |

---

## 📁 Cấu trúc File

```
src/main/resources/
├── templates/shipper/
│   ├── fragments/
│   │   ├── header.html          ✅ Mới
│   │   └── sidebar.html         ✅ Cập nhật
│   ├── layout.html              ✅ Cập nhật
│   ├── dashboard.html           ✅ Cập nhật
│   ├── profile.html             ✅ Hoàn toàn mới
│   └── app.html                 ✅ Đã có (order page)
│
└── static/styles/css/shipper/
    ├── header.css               ✅ Hoàn toàn mới
    ├── sidebar.css              ✅ Hoàn toàn mới
    ├── dashboard.css            ✅ Hoàn toàn mới
    └── base.css                 ✅ Đã có
```

---

## 🚀 Hướng dẫn sử dụng

### 1. Đăng nhập
Truy cập: `/shipper/login` → Đăng nhập bằng tài khoản shipper

### 2. Dashboard
- Xem thống kê tổng quan (đơn hàng, thu nhập)
- Click "Xem tất cả" để chuyển sang trang Order

### 3. Order (Đơn hàng)
- Bật toggle để "Trực tuyến"
- Nhận đơn từ danh sách "Đơn có thể nhận"
- Cập nhật trạng thái: Bắt đầu giao → Giao thành công/Hoàn hàng

### 4. Profile (Cá nhân)
- Xem thông tin cá nhân
- Cập nhật biển số xe, loại xe, giấy phép
- Đăng xuất

### 5. eWallet (Ví)
- Xem số dư, lịch sử giao dịch
- (Trang này có thể cần cập nhật thêm nếu cần)

---

## 📱 Responsive Design

### Desktop (>768px)
- Header full width, hiển thị đầy đủ text
- Stats grid 3 cột
- Bottom nav có thể chuyển thành sidebar trái (nếu cần)

### Tablet (480px - 768px)
- Header thu nhỏ một chút
- Stats grid 2 cột
- Text vẫn hiển thị đầy đủ

### Mobile (<480px)
- Header: chỉ logo + avatar (ẩn text)
- Stats grid 1 cột
- Bottom nav: icon + text nhỏ

### Mobile nhỏ (<360px)
- Bottom nav: chỉ icon (ẩn text)
- Padding giảm xuống

---

## 🎯 Các trang cần cập nhật tiếp theo (nếu có)

1. ✅ **Login page** - Đã có
2. ✅ **Dashboard** - Đã cập nhật
3. ✅ **Order (app.html)** - Đã có
4. ✅ **Profile** - Đã cập nhật
5. ⚠️ **eWallet** - Có thể cần cập nhật giao diện
6. ⚠️ **Register** - Có thể cần cập nhật giao diện

---

## 💡 Lưu ý khi phát triển

1. **Font Awesome 6.5.1** đã được import trong layout
2. **Leaflet CSS** đã được import cho bản đồ
3. Tất cả CSS được tách riêng vào folder `css/shipper/`
4. Layout sử dụng Thymeleaf Layout Dialect
5. CSRF token được set trong layout
6. User info được truyền vào model từ controller

---

## 🐛 Troubleshooting

### Header không hiển thị màu gradient?
- Kiểm tra file `header.css` đã được import trong layout chưa
- Xóa cache trình duyệt (Ctrl + Shift + R)

### Bottom nav bị che nội dung?
- Kiểm tra `padding-bottom: 70px` trong body

### Avatar không hiển thị?
- Kiểm tra path `/styles/image/customer/default-avatar.png` có tồn tại không
- Kiểm tra user object có avatar không

### Active state không work?
- Kiểm tra `th:classappend` trong sidebar.html
- Đảm bảo request URI được check đúng

---

## 📧 Hỗ trợ

Nếu có vấn đề, kiểm tra:
1. Console browser (F12) để xem lỗi JS/CSS
2. Server logs để xem lỗi backend
3. Network tab để xem request/response

---

**🎉 Hoàn thành!** Giao diện shipper đã được cập nhật hoàn toàn với UI/UX hiện đại, responsive và dễ sử dụng!

