package mocmien.com.dto.request.delivery;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DeliveryRequest {

    @NotBlank(message = "Tên phương thức vận chuyển không được để trống")
    @Size(max = 500, message = "Tên phương thức vận chuyển không được vượt quá 500 ký tự")
    private String deliveryName;

    private String description;

    @NotNull(message = "Giá cơ bản không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá cơ bản phải lớn hơn hoặc bằng 0")
    private BigDecimal basePrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá mỗi km phải lớn hơn hoặc bằng 0")
    private BigDecimal pricePerKM;

    @DecimalMin(value = "0", inclusive = true, message = "Khoảng cách tối đa phải là số nguyên không âm")
    private Integer maxDistance;

    @NotNull(message = "Trạng thái hoạt động không được để trống")
    private Boolean isActive;

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
    
    
    
    
}