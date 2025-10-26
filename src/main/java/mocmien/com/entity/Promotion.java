package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocmien.com.enums.PromotionType;

@Entity
@Table(name = "Promotion")
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FK: Cửa hàng áp dụng khuyến mãi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", referencedColumnName = "id")
    private Store store;

    // FK: Người tạo khuyến mãi (admin)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    @Column(name = "name", nullable = false, unique = true, columnDefinition = "NVARCHAR(500)")
    private String name;

    @Column(name = "type", nullable = false, columnDefinition = "NVARCHAR(50)")
    private PromotionType type; 
    // Ví dụ: "PERCENT", "GIFT", "FREESHIP"

    @Column(name = "value", nullable = false, precision = 10, scale = 2)
    private BigDecimal value; 
    
    // Banner và Ribbon (tùy chọn)
    @Column(name = "banner", columnDefinition = "VARCHAR(MAX)")
    private String banner;

    @Column(name = "startDate")
    private LocalDateTime startDate;

    @Column(name = "endDate")
    private LocalDateTime endDate;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive = false; // Khuyến mãi đang được kích hoạt hay không

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