package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocmien.com.enums.PromotionType;

@Entity
@Table(name = "PromotionDetail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Khuyến mãi chính
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotionId", nullable = false, referencedColumnName = "id")
    private Promotion promotion;

    // Áp dụng cho sản phẩm (nullable nếu áp dụng toàn cửa hàng)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", referencedColumnName = "id")
    private Product product;

    // Áp dụng cho hạng khách hàng (nullable nếu áp dụng tất cả)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "levelId")
    private Level level;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "NVARCHAR(50)")
    private PromotionType type; // PERCENT, AMOUNT, FREESHIP,...

    @Column(name = "value", columnDefinition = "DECIMAL(18,2)")
    private BigDecimal value = BigDecimal.ZERO;

    // Banner và Ribbon (tùy chọn)
    @Column(name = "banner", columnDefinition = "VARCHAR(MAX)")
    private String banner;

    @Column(name = "ribbon", columnDefinition = "VARCHAR(MAX)")
    private String ribbon;

    // Thời gian áp dụng khuyến mãi riêng (nullable → mặc định lấy từ Promotion)
    @Column(name = "startDate")
    private LocalDateTime startDate;

    @Column(name = "endDate")
    private LocalDateTime endDate;

    // Thứ tự ưu tiên hiển thị khi có nhiều promotion
    @Column(name = "priority")
    private Integer priority = 0;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive = true;

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