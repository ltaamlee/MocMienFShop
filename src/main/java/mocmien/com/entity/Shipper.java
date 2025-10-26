package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Shipper")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Liên kết với User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // Liên kết với Delivery
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliveryId", nullable = false)
    private Delivery delivery;

    @Column(name = "fullName", nullable = false,  columnDefinition = "NVARCHAR(200)")
    private String fullName;

    @Column(name = "vehicleNumber", nullable = false, unique = true, length = 50)
    private String vehicleNumber; // Biển số xe

    @Column(name = "vehicleType", nullable = false, unique = true, columnDefinition = "NVARCHAR(50)")
    private String vehicleType; // Loại phương tiện (xe máy, ô tô, ...)

    @Column(name = "license", length = 100)
    private String license; // Số bằng lái hoặc đường dẫn ảnh bằng lái

    @Column(name = "rating", precision = 2, scale = 1, nullable = false)
    private BigDecimal rating = BigDecimal.ZERO; // Điểm đánh giá trung bình

    @Column(name = "totalDelivery")
    private Integer totalDelivery = 0; // Tổng số đơn hàng đã giao

    @Column(name = "eWallet", precision = 12, scale = 2)
    private BigDecimal eWallet = BigDecimal.ZERO; // Số dư ví điện tử

    @Column(name = "isActive", nullable = false)
    private Boolean isActive = false; // Được cấp phép hoạt động hay không
    
    @OneToMany(mappedBy = "shipper", fetch = FetchType.LAZY)
    private List<Orders> orders; // Danh sách đơn hàng mà shipper đang phụ trách


    @Column(name = "createAt", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "updateAt")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}