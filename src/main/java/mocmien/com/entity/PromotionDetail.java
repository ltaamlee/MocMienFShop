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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public PromotionType getType() {
		return type;
	}

	public void setType(PromotionType type) {
		this.type = type;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getRibbon() {
		return ribbon;
	}

	public void setRibbon(String ribbon) {
		this.ribbon = ribbon;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	public LocalDateTime getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}

	
}