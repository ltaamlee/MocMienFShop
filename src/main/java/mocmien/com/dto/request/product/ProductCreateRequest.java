package mocmien.com.dto.request.product;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.Data;
import mocmien.com.enums.ProductStatus;

@Data
public class ProductCreateRequest {
	@NotBlank
	private String productName;
	@NotNull
	private Integer categoryId;
	@NotNull
	@DecimalMin("0.0")
	private BigDecimal price; // chỉ nhập price
	@NotBlank
	private String size;
	@NotNull
	@Min(0)
	private Integer stock;
	private ProductStatus status = ProductStatus.SELLING; // SELLING | STOPPED | OUT_OF_STOCK
	private Boolean isActive = true;
	private List<String> imageUrls; // URLs sau khi upload
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
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
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public List<String> getImageUrls() {
		return imageUrls;
	}
	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}
	
	
}