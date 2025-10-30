package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocmien.com.enums.OrderStatus;
import mocmien.com.enums.PaymentMethod;

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    @Column(name = "id", length = 100)
    private String id; // Mã đơn hàng (tạo từ thời gian hoặc custom generator)

    // Khóa ngoại: Khách hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", referencedColumnName = "id", nullable = false)
    private UserProfile customer;

    // Khóa ngoại: Cửa hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", referencedColumnName = "id", nullable = false)
    private Store store;

    // Khóa ngoại: Khuyến mãi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotionId", referencedColumnName = "id")
    private Promotion promotion;

    // Khóa ngoại: Đơn vị giao hàng (nếu có)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliveryId", referencedColumnName = "id")
    private Delivery delivery;

    // Khóa ngoại: Shipper
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipperId", referencedColumnName = "id")
    private Shipper shipper;

    // Trạng thái đơn hàng
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "NVARCHAR(100)")
    private OrderStatus status; 
    // Ví dụ: NEW, CONFIRMED, SHIPPING, DELIVERED, CANCELED, RETURNED

    // Thanh toán
    @Column(name = "isPaid", nullable = false)
    private Boolean isPaid = false; // true = đã thanh toán online
    
    @Enumerated(EnumType.STRING)
    @Column(name = "paymentMethod", columnDefinition = "NVARCHAR(50)", nullable = false)
    private PaymentMethod paymentMethod;
 
    @Column(name = "amountFromCustomer", nullable = false, columnDefinition = "DECIMAL(18,2)")
    private BigDecimal amountFromCustomer = BigDecimal.ZERO; // khách trả

    @Column(name = "amountFromStore", nullable = false, columnDefinition = "DECIMAL(18,2)")
    private BigDecimal amountFromStore = BigDecimal.ZERO; // phí cửa hàng trả hệ thống

    @Column(name = "amountToStore", nullable = false, columnDefinition = "DECIMAL(18,2)")
    private BigDecimal amountToStore = BigDecimal.ZERO; // tiền cửa hàng nhận lại

    @Column(name = "amountToSys", nullable = false, columnDefinition = "DECIMAL(18,2)")
    private BigDecimal amountToSys = BigDecimal.ZERO; // tiền hệ thống thu được

    // Ghi chú đơn hàng
    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    // Quan hệ với OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    // Thời gian
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (id == null || id.isEmpty()) {
            String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uuidPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // 6 ký tự đầu
            id = "ORD-" + datePart + "-" + uuidPart;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Transient
    public BigDecimal getTotalAmount() {
        if (orderDetails == null) return BigDecimal.ZERO;
        return orderDetails.stream()
            .map(d -> d.getPromotionalPrice() != null ? 
                     d.getPromotionalPrice().multiply(BigDecimal.valueOf(d.getQuantity())) :
                     d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    
    // ==============================
    // Getters & Setters
    // ==============================


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserProfile getCustomer() {
		return customer;
	}

	public void setCustomer(UserProfile customer) {
		this.customer = customer;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public Shipper getShipper() {
		return shipper;
	}

	public void setShipper(Shipper shipper) {
		this.shipper = shipper;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BigDecimal getAmountFromCustomer() {
		return amountFromCustomer;
	}

	public void setAmountFromCustomer(BigDecimal amountFromCustomer) {
		this.amountFromCustomer = amountFromCustomer;
	}

	public BigDecimal getAmountFromStore() {
		return amountFromStore;
	}

	public void setAmountFromStore(BigDecimal amountFromStore) {
		this.amountFromStore = amountFromStore;
	}

	public BigDecimal getAmountToStore() {
		return amountToStore;
	}

	public void setAmountToStore(BigDecimal amountToStore) {
		this.amountToStore = amountToStore;
	}

	public BigDecimal getAmountToSys() {
		return amountToSys;
	}

	public void setAmountToSys(BigDecimal amountToSys) {
		this.amountToSys = amountToSys;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

    
}