package mocmien.com.dto.request.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import mocmien.com.enums.PromotionStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorPromotionStatusRequest {
	@NotNull
	private PromotionStatus status;

	public PromotionStatus getStatus() {
		return status;
	}

	public void setStatus(PromotionStatus status) {
		this.status = status;
	}

	
}