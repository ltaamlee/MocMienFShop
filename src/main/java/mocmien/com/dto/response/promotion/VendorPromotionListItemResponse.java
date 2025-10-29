package mocmien.com.dto.response.promotion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocmien.com.enums.PromotionStatus;
import mocmien.com.enums.PromotionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorPromotionListItemResponse {
    private Integer id;
    private String name;
    private PromotionType type;
    private BigDecimal value;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PromotionStatus status;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public PromotionStatus getStatus() {
		return status;
	}
	public void setStatus(PromotionStatus status) {
		this.status = status;
	}
    
    
}
