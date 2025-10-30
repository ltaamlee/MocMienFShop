package mocmien.com.dto.response.promotion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import mocmien.com.enums.PromotionStatus;
import mocmien.com.enums.PromotionType;

public class AdminStorePromotionResponse {
    private Integer id;
    private String name;
    private PromotionType type;
    private BigDecimal value;
    private PromotionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer storeId;
    private String storeName;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PromotionType getType() { return type; }
    public void setType(PromotionType type) { this.type = type; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public PromotionStatus getStatus() { return status; }
    public void setStatus(PromotionStatus status) { this.status = status; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Integer getStoreId() { return storeId; }
    public void setStoreId(Integer storeId) { this.storeId = storeId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
}


