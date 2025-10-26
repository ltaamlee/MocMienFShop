package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private Customer customer;

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
    @Column(name = "status", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String status; 
    // Ví dụ: NEW, CONFIRMED, SHIPPING, DELIVERED, CANCELED, RETURNED

    // Thanh toán
    @Column(name = "isPaid", nullable = false)
    private Boolean isPaid = false; // true = đã thanh toán online
    
    @Column(name = "paymentMethod", columnDefinition = "NVARCHAR(50)", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "amountFromCustomer", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountFromCustomer = BigDecimal.ZERO; // khách trả

    @Column(name = "amountFromStore", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountFromStore = BigDecimal.ZERO; // phí cửa hàng trả hệ thống

    @Column(name = "amountToStore", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountToStore = BigDecimal.ZERO; // tiền cửa hàng nhận lại

    @Column(name = "amountToSys", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountToSys = BigDecimal.ZERO; // tiền hệ thống thu được

    // Ghi chú đơn hàng
    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    // Quan hệ với OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    // Thời gian
    @Column(name = "createAt", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "updateAt")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();

        // Nếu chưa có id thì tự tạo theo timestamp
        if (id == null || id.isEmpty()) {
            id = "ORD-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}