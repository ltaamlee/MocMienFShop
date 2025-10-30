package mocmien.com.dto.request.commission;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminCommissionCreateRequest {
    private Integer storeId; // null = áp dụng toàn bộ shop

    @NotNull
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    private BigDecimal ratePercent; // 0..100

    private String note;
	public Integer getStoreId() {
		return storeId;
	}
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}
	public BigDecimal getRatePercent() {
		return ratePercent;
	}
	public void setRatePercent(BigDecimal ratePercent) {
		this.ratePercent = ratePercent;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
    
    
}



