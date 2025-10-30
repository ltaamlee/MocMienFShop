# Hướng dẫn sử dụng chức năng Shipper

## 📦 Tổng quan
Hệ thống quản lý đơn hàng cho shipper giúp nhận và giao hàng một cách dễ dàng.

## 🚀 Quy trình làm việc

### 1. Đăng nhập
- Truy cập: `/shipper/login`
- Đăng nhập bằng tài khoản shipper

### 2. Bật trạng thái hoạt động
- Vào trang Dashboard: `/shipper/dashboard`
- Bật toggle "Trạng thái hoạt động" để nhận đơn
- 🟢 **Trực tuyến**: Có thể nhận đơn mới
- ⚫ **Ngoại tuyến**: Không nhận đơn mới

### 3. Nhận đơn hàng

#### Đơn có thể nhận
- Hiển thị các đơn hàng đã được vendor xác nhận (status: `CONFIRMED`)
- Chỉ hiển thị đơn thuộc delivery company của shipper
- Click nút **"Nhận đơn ngay"** để nhận đơn

**Luồng xử lý:**
```
CONFIRMED → Shipper nhận đơn → SHIPPING
```

### 4. Giao hàng

#### Đơn đang giao
Sau khi nhận đơn, có 2 lựa chọn:

**A. Bắt đầu giao hàng**
- Nút: **"Bắt đầu giao"** (nếu status = `CONFIRMED`)
- Cập nhật status: `CONFIRMED` → `SHIPPING`

**B. Giao thành công**
- Nút: **"Giao thành công"** (nếu status = `SHIPPING`)
- Cập nhật status: `SHIPPING` → `DELIVERED`
- ✅ Hoàn thành đơn hàng

**C. Hoàn hàng**
- Nút: **"Hoàn hàng"** (màu đỏ, nếu status = `SHIPPING`)
- Cập nhật status: `SHIPPING` → `RETURNED_REFUNDED`
- Trường hợp: Khách từ chối nhận, sai địa chỉ, etc.

## 🔄 Sơ đồ trạng thái đơn hàng

```
┌─────────────┐
│   CONFIRMED │  ← Vendor đã xác nhận, chưa có shipper
└──────┬──────┘
       │ Shipper nhận đơn
       ↓
┌─────────────┐
│   SHIPPING  │  ← Shipper đang giao
└──────┬──────┘
       │
       ├─→ Giao thành công → DELIVERED
       │
       └─→ Hoàn hàng → RETURNED_REFUNDED
```

## 🛠️ API Endpoints

### Nhận đơn
```http
PATCH /api/shipper/orders/{orderId}/accept
```
- **403**: Không có quyền (đơn không thuộc delivery của shipper)
- **409**: Đơn đã được nhận bởi shipper khác
- **204**: Thành công

### Cập nhật trạng thái
```http
PATCH /api/shipper/orders/{orderId}/status
Content-Type: application/json

{
  "status": "DELIVERED"
}
```

**Status hợp lệ:**
- `SHIPPING`: Bắt đầu giao
- `DELIVERED`: Giao thành công
- `RETURNED_REFUNDED`: Hoàn hàng

**Response codes:**
- **404**: Không tìm thấy đơn hoặc không phải đơn của shipper này
- **409**: Chuyển trạng thái không hợp lệ
- **204**: Thành công

## 📍 Bản đồ GPS
- Hệ thống tự động cập nhật vị trí hiện tại của shipper
- Giúp tracking realtime (nếu cần mở rộng)

## ⚠️ Lưu ý
1. Chỉ có thể nhận đơn khi đang **Trực tuyến**
2. Một đơn chỉ có thể được một shipper nhận
3. Không thể chuyển ngược trạng thái (VD: DELIVERED → SHIPPING)
4. Phải xác nhận trước khi thực hiện hành động quan trọng

## 🎨 Giao diện
- **Card xanh lá**: Đơn có thể nhận
- **Card xanh dương**: Đơn đang giao
- **Badge vàng**: Trạng thái CONFIRMED
- **Badge xanh dương**: Trạng thái SHIPPING
- **Badge xanh lá**: Trạng thái DELIVERED

## 📞 Hỗ trợ
Nếu có vấn đề, liên hệ admin hoặc vendor để được hỗ trợ.

