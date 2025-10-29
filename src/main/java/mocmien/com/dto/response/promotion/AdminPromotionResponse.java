package mocmien.com.dto.response.promotion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import mocmien.com.enums.PromotionStatus;
import mocmien.com.enums.PromotionType;

public class AdminPromotionResponse {
	private Integer id;
	private String name;
	private PromotionType type;
	private BigDecimal value;
	private String banner;
	private String ribbon;
	private PromotionStatus status;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime createdAt;
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
	public PromotionStatus getStatus() {
		return status;
	}
	public void setStatus(PromotionStatus status) {
		this.status = status;
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
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
}
