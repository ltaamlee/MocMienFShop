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
import mocmien.com.enums.PromotionStatus;
import mocmien.com.enums.PromotionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

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

	@Column(name = "value", nullable = false, columnDefinition = "DECIMAL(18,2)")
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

	@Column(name = "createdAt", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updatedAt")
	private LocalDateTime updatedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "NVARCHAR(20)")
	private PromotionStatus status = PromotionStatus.INACTIVE;
	
	public boolean isExpiredNow() {
		return endDate != null && endDate.isBefore(java.time.LocalDateTime.now());
	}

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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

	public PromotionStatus getStatus() {
		return status;
	}

	public void setStatus(PromotionStatus status) {
		this.status = status;
	}

	
	
	
}