# ✅ Tính phí vận chuyển dựa trên khoảng cách (Geocoding)

## 🎯 Vấn đề đã giải quyết
- ❌ **Trước:** Không tính được phí ship vì thiếu tọa độ (latitude/longitude) của Shop và Địa chỉ khách hàng
- ✅ **Sau:** Tự động lấy tọa độ từ địa chỉ (Geocoding) và tính khoảng cách để tính phí ship

---

## 🚀 Giải pháp

### 1. **Geocoding Service** (Lấy tọa độ từ địa chỉ)
**File:** `src/main/java/mocmien/com/integration/geocoding/GeocodingService.java`

**API sử dụng:** Nominatim OpenStreetMap (miễn phí)
- URL: `https://nominatim.openstreetmap.org/search`
- Không cần API key
- Rate limit: 1 request/second (cần cache kết quả)

**Methods:**
```java
// Lấy tọa độ từ địa chỉ
Map<String, Double> geocodeAddress(String address)
// Returns: {"latitude": 10.xxx, "longitude": 106.xxx}

// Tạo địa chỉ đầy đủ từ các thành phần
String buildFullAddress(String street, String ward, String district, String city)
```

---

### 2. **Shipping Service** (Tính phí ship)
**File:** `src/main/java/mocmien/com/service/impl/ShippingServiceImpl.java`

#### **Flow tính phí:**

```
1. Lấy tọa độ Shop
   ├─ Nếu đã có trong DB → Dùng luôn
   └─ Nếu chưa có → Geocode từ địa chỉ Shop

2. Lấy tọa độ Địa chỉ khách hàng
   ├─ Nếu đã có trong DB → Dùng luôn
   └─ Nếu chưa có → Geocode từ địa chỉ

3. Tính khoảng cách (Haversine formula)
   └─ Kết quả: X km

4. Kiểm tra bán kính giao hàng
   ├─ Nếu <= 3km → Miễn phí ship (₫0)
   ├─ Nếu > 100km → Không giao (throw Exception)
   └─ Nếu 3-100km → Tính phí

5. Tính phí ship
   Formula: Phí cơ bản + (Khoảng cách × 3000) + (Trọng lượng/1000 × 2000)
   └─ Làm tròn lên nghìn
```

---

## 💰 Công thức tính phí ship

```java
Phí cơ bản    = ₫15,000
Phí khoảng cách = Khoảng cách (km) × ₫3,000/km
Phí trọng lượng = Trọng lượng (kg) × ₫2,000/kg

Tổng phí = Phí cơ bản + Phí khoảng cách + Phí trọng lượng
```

### Ví dụ:
```
Shop ở Quận 1, Khách ở Quận 7
- Khoảng cách: 8 km
- Trọng lượng: 1.5 kg (1500g)

Phí cơ bản: ₫15,000
Phí khoảng cách: 8 × ₫3,000 = ₫24,000
Phí trọng lượng: 1.5 × ₫2,000 = ₫3,000

Tổng: ₫42,000 → Làm tròn: ₫42,000
```

---

## ⚙️ Cấu hình (application.properties)

```properties
# Bán kính miễn phí ship (km)
shipping.free_radius_km=3

# Bán kính giao hàng tối đa (km)
shipping.max_radius_km=100

# Phí ship mặc định nếu không tính được (₫)
shipping.fallback_fee=30000
```

---

## 📊 Haversine Formula (Tính khoảng cách)

Công thức tính khoảng cách giữa 2 điểm trên bề mặt Trái Đất:

```java
R = 6371 km (bán kính Trái Đất)

a = sin²(Δlat/2) + cos(lat1) × cos(lat2) × sin²(Δlon/2)
c = 2 × atan2(√a, √(1−a))
distance = R × c
```

**Độ chính xác:** ±0.5% (vài trăm mét)

---

## 🔄 Workflow trong Checkout

### **Trang Checkout (`/checkout`)**

```java
1. Load trang checkout
2. Lấy địa chỉ mặc định của khách hàng
3. Lấy Shop từ sản phẩm đầu tiên trong giỏ
4. Tính phí ship:
   shippingService.calculateShippingFee(store, address, weight)
5. Hiển thị:
   - Tổng sản phẩm: ₫XXX
   - Phí vận chuyển: ₫YYY
   - Tổng thanh toán: ₫ZZZ
```

### **Xác nhận đơn hàng (`/checkout/confirm`)**

```java
1. Validate lại khoảng cách
2. Nếu quá xa (>100km) → Redirect error
3. Tạo Order
4. Cập nhật shippingFee vào Order
5. Cập nhật amountFromCustomer = total + shippingFee
6. Save Order
7. Redirect success/payment
```

---

## 📝 Entity Changes (Cần có fields)

### **Store**
```java
private BigDecimal latitude;   // Vĩ độ
private BigDecimal longitude;  // Kinh độ
private String address;        // Địa chỉ đầy đủ
```

### **CustomerAddress**
```java
private BigDecimal latitude;   // Vĩ độ
private BigDecimal longitude;  // Kinh độ
private String line;           // Số nhà
private String ward;           // Phường/Xã
private String district;       // Quận/Huyện
private String province;       // Tỉnh/TP
```

### **Orders**
```java
private BigDecimal shippingFee;  // Phí vận chuyển
```

---

## 🎯 Cách sử dụng

### 1. **Trong Controller**
```java
@Autowired
private ShippingService shippingService;

// Tính phí ship
BigDecimal fee = shippingService.calculateShippingFee(
    store,          // Shop
    address,        // Địa chỉ khách hàng
    1500            // Trọng lượng (gram)
);
```

### 2. **Xử lý Exception**
```java
try {
    BigDecimal fee = shippingService.calculateShippingFee(store, address, weight);
    // Success: hiển thị phí
} catch (IllegalArgumentException e) {
    // Quá xa hoặc không tìm được địa chỉ
    model.addAttribute("error", e.getMessage());
} catch (Exception e) {
    // Lỗi khác: dùng phí mặc định
    BigDecimal fee = BigDecimal.valueOf(30000);
}
```

---

## ⚠️ Lưu ý

### 1. **Rate Limiting**
Nominatim có giới hạn 1 request/second. Nên:
- Cache kết quả geocoding vào DB (lưu latitude/longitude)
- Không geocode quá nhiều lần cho cùng 1 địa chỉ

### 2. **Độ chính xác**
- Địa chỉ càng chi tiết → kết quả càng chính xác
- Ví dụ tốt: "123 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP.HCM, Việt Nam"
- Ví dụ xấu: "Quận 1" (quá chung chung)

### 3. **Fallback**
Nếu không geocode được:
- Kiểm tra địa chỉ có đầy đủ không
- Thử với địa chỉ ngắn gọn hơn (bỏ số nhà)
- Dùng phí mặc định (₫30,000)

### 4. **Performance**
- Geocoding mất ~1-2 giây/request
- Nên hiển thị loading spinner khi tính phí
- Có thể cache kết quả trong session

---

## 🔧 Troubleshooting

### Lỗi: "Không xác định được khoảng cách"
**Nguyên nhân:** Không geocode được địa chỉ

**Giải pháp:**
1. Kiểm tra địa chỉ Shop có đầy đủ không
2. Kiểm tra địa chỉ khách hàng có đầy đủ không
3. Thử geocode thủ công: https://nominatim.openstreetmap.org/
4. Thêm latitude/longitude vào DB manually

### Lỗi: "Đơn hàng không áp dụng tại vị trí của bạn"
**Nguyên nhân:** Khoảng cách > 100km

**Giải pháp:**
1. Tăng `shipping.max_radius_km` trong config
2. Hoặc thông báo khách hàng không giao tới địa chỉ này

### Geocoding trả về sai vị trí
**Giải pháp:**
1. Kiểm tra định dạng địa chỉ
2. Thêm "Việt Nam" vào cuối địa chỉ
3. Thử bỏ số nhà, chỉ giữ phường/quận/thành phố

---

## 📈 Cải tiến trong tương lai

1. **Cache Geocoding kết quả vào DB**
   - Lưu latitude/longitude khi tạo mới Store/Address
   - Không cần geocode lại mỗi lần

2. **Dùng Google Maps Geocoding API** (chính xác hơn)
   - Cần API key (có giới hạn miễn phí 2500 request/ngày)
   - URL: `https://maps.googleapis.com/maps/api/geocode/json`

3. **Cho phép Vendor nhập tọa độ thủ công**
   - Thêm map picker trong trang tạo/sửa Store
   - Click vào map → lấy lat/lng

4. **Tính phí linh hoạt hơn**
   - Phí khác nhau theo vùng
   - Giảm giá ship cho đơn hàng lớn
   - Phí tăng giờ cao điểm

---

## ✅ Testing

### Test case 1: Trong bán kính miễn phí (< 3km)
```
Shop: Quận 1, TP.HCM
Customer: Quận 1, TP.HCM (cách 2km)
→ Phí ship: ₫0
```

### Test case 2: Khoảng cách vừa phải (3-100km)
```
Shop: Quận 1, TP.HCM
Customer: Quận 7, TP.HCM (cách 8km)
Trọng lượng: 1.5kg
→ Phí ship: ₫15,000 + ₫24,000 + ₫3,000 = ₫42,000
```

### Test case 3: Quá xa (> 100km)
```
Shop: TP.HCM
Customer: Hà Nội (cách 1500km)
→ Lỗi: "Đơn hàng không áp dụng tại vị trí của bạn"
```

---

**🎉 Hoàn thành!** Giờ hệ thống có thể tự động tính phí ship dựa trên khoảng cách thực tế!

