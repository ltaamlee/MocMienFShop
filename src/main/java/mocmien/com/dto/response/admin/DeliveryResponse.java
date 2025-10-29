package mocmien.com.dto.response.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DeliveryResponse {

private Integer id;
    
    private String deliveryName;
    
    private String description;

    private BigDecimal basePrice;

    private BigDecimal pricePerKM;

    private Integer maxDistance;

    private Boolean isActive;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

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

	public DeliveryResponse(Integer id, String deliveryName, String description, BigDecimal basePrice,
			BigDecimal pricePerKM, Integer maxDistance, Boolean isActive, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.deliveryName = deliveryName;
		this.description = description;
		this.basePrice = basePrice;
		this.pricePerKM = pricePerKM;
		this.maxDistance = maxDistance;
		this.isActive = isActive;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}


    
    
    
}
