package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Delivery")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "deliveryName", nullable = false, unique = true, columnDefinition = "NVARCHAR(500)")
	private String deliveryName; 

	@Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
	private String description;

	@Column(name = "basePrice", nullable = false, columnDefinition = "DECIMAL(18,2)")
	private BigDecimal basePrice;

	@Column(name = "pricePerKM", columnDefinition = "DECIMAL(18,2)")
	private BigDecimal pricePerKM = BigDecimal.ZERO;

	@Column(name = "maxDistance")
	private Integer maxDistance;

	@Column(name = "isActive", nullable = false)
	private Boolean isActive = false;

	@Column(name = "createAt")
	private LocalDateTime createAt;

	@Column(name = "updateAt")
	private LocalDateTime updateAt;

	@PrePersist
	public void prePersist() {
		createAt = LocalDateTime.now();
		if (isActive == null)
			isActive = false;
	}

	@PreUpdate
	public void preUpdate() {
		updateAt = LocalDateTime.now();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDeliveryName() {
		return deliveryName;
	}

	public void setDeliveryName(String deliveryName) {
		this.deliveryName = deliveryName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public BigDecimal getPricePerKM() {
		return pricePerKM;
	}

	public void setPricePerKM(BigDecimal pricePerKM) {
		this.pricePerKM = pricePerKM;
	}

	public Integer getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Integer maxDistance) {
		this.maxDistance = maxDistance;
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
