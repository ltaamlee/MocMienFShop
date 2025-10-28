package mocmien.com.dto.response.product;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import mocmien.com.enums.ProductStatus;

@Data
@Builder
public class ProductListItemResponse {
    private Integer id;
    private String productName;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal promotionalPrice;
    private Integer stock;
    private ProductStatus status;
    private String defaultImage; // ảnh đại diện (nếu có)
    private Boolean isActive;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getPromotionalPrice() {
		return promotionalPrice;
	}
	public void setPromotionalPrice(BigDecimal promotionalPrice) {
		this.promotionalPrice = promotionalPrice;
	}
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
	public ProductStatus getStatus() {
		return status;
	}
	public void setStatus(ProductStatus status) {
		this.status = status;
	}
	public String getDefaultImage() {
		return defaultImage;
	}
	public void setDefaultImage(String defaultImage) {
		this.defaultImage = defaultImage;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
    
}
