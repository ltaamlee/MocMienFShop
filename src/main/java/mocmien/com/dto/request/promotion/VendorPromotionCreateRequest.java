package mocmien.com.dto.request.promotion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.*;
import lombok.*;
import mocmien.com.enums.PromotionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorPromotionCreateRequest {
	@NotBlank
	private String name;
	@NotNull
	private PromotionType type; // PERCENT | AMOUNT | FREESHIP | GIFT
	@DecimalMin(value = "0.0", inclusive = true) // có thể null khi FREESHIP/GIFT
	private BigDecimal value;
	@NotNull
	private LocalDateTime startDate;
	@NotNull
	private LocalDateTime endDate;
	private List<Integer> productIds;

	// ✅ ràng buộc theo type
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
		// FREESHIP/GIFT: cho phép null hoặc 0
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

	public List<Integer> getProductIds() {
		return productIds;
	}

	public void setProductIds(List<Integer> productIds) {
		this.productIds = productIds;
	}

}