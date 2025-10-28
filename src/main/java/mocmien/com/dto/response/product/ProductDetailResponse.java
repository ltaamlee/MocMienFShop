package mocmien.com.dto.response.product;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import mocmien.com.enums.ProductStatus;

@Data
@Builder
public class ProductDetailResponse {
    private Integer id;
    private String productName;
    private Integer categoryId;
    private String categoryName;

    private BigDecimal price;
    private BigDecimal promotionalPrice;
    private String size;

    private Integer stock;
    private Integer sold;

    private ProductStatus status;
    private Boolean isActive;

    private List<String> imageUrls;

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

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
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

	public Integer getSold() {
		return sold;
	}

	public void setSold(Integer sold) {
		this.sold = sold;
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
