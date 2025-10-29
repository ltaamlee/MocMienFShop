package mocmien.com.dto.request.promotion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mocmien.com.enums.PromotionType;

public class AdminPromitonCreateRequest {
	@NotBlank(message = "Tên khuyến mãi không được để trống")
	private String name;
	
	@NotNull(message = "Loại khuyến mãi không được để trống")	
	private PromotionType type; // PERCENT | AMOUNT | FREESHIP | GIFT
	
	@DecimalMin(value = "0.0", inclusive = true)
	private BigDecimal value;

	@NotNull
	private String banner;
	
	@NotNull
	private LocalDateTime startDate;
	@NotNull
	private LocalDateTime endDate;
	
	@AssertTrue(message = "Giá trị % phải trong 0–100; Giảm tiền phải ≥ 0; FREESHIP/GIFT không cần giá trị")
	public boolean isValueValid() {
		if (type == null)
			return true;
		if (type == PromotionType.PERCENT) {
			return value != null && value.compareTo(BigDecimal.ZERO) >= 0
					&& value.compareTo(new BigDecimal("100")) <= 0;
		}
		if (type == PromotionType.AMOUNT) {
			return value != null && value.compareTo(BigDecimal.ZERO) >= 0;
		}
		return value == null || value.compareTo(BigDecimal.ZERO) == 0;
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

}
