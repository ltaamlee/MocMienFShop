package mocmien.com.dto.response.product;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocmien.com.enums.ProductStatus;

@Data
@Builder
@NoArgsConstructor

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
    
    private Integer storeId;
    private String storeName;
    
    private String mainImage; // ✅ Ảnh chính
    private String storeAvatar;
    private Long storeProductCount;
    private java.math.BigDecimal storeRating;
    private java.math.BigDecimal productRating;
    private Long productRatingCount;

    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }
    public String getStoreAvatar() { return storeAvatar; }
    public void setStoreAvatar(String storeAvatar) { this.storeAvatar = storeAvatar; }
    public Long getStoreProductCount() { return storeProductCount; }
    public void setStoreProductCount(Long storeProductCount) { this.storeProductCount = storeProductCount; }
    public java.math.BigDecimal getStoreRating() { return storeRating; }
    public void setStoreRating(java.math.BigDecimal storeRating) { this.storeRating = storeRating; }
    public java.math.BigDecimal getProductRating() { return productRating; }
    public void setProductRating(java.math.BigDecimal productRating) { this.productRating = productRating; }
    public Long getProductRatingCount() { return productRatingCount; }
    public void setProductRatingCount(Long productRatingCount) { this.productRatingCount = productRatingCount; }

    
    

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

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
    
	public static class Builder {
        private final ProductDetailResponse dto = new ProductDetailResponse();

        public Builder id(Integer id) {
            dto.setId(id);
            return this;
        }

        public Builder productName(String productName) {
            dto.setProductName(productName);
            return this;
        }

        public Builder categoryId(Integer categoryId) {
            dto.setCategoryId(categoryId);
            return this;
        }

        public Builder categoryName(String categoryName) {
            dto.setCategoryName(categoryName);
            return this;
        }

        public Builder price(BigDecimal price) {
            dto.setPrice(price);
            return this;
        }

        public Builder promotionalPrice(BigDecimal promotionalPrice) {
            dto.setPromotionalPrice(promotionalPrice);
            return this;
        }

        public Builder size(String size) {
            dto.setSize(size);
            return this;
        }

        public Builder stock(Integer stock) {
            dto.setStock(stock);
            return this;
        }

        public Builder sold(Integer sold) {
            dto.setSold(sold);
            return this;
        }

        public Builder status(ProductStatus status) {
            dto.setStatus(status);
            return this;
        }

        public Builder isActive(Boolean isActive) {
            dto.setIsActive(isActive);
            return this;
        }

        public Builder imageUrls(List<String> imageUrls) {
            dto.setImageUrls(imageUrls);
            return this;
        }

        public Builder storeId(Integer storeId) {
            dto.setStoreId(storeId);
            return this;
        }

        public Builder storeName(String storeName) {
            dto.setStoreName(storeName);
            return this;
        }

        public Builder mainImage(String mainImage) {
            dto.setMainImage(mainImage);
            return this;
        }

        public Builder storeAvatar(String storeAvatar) {
            dto.setStoreAvatar(storeAvatar);
            return this;
        }
        public Builder storeProductCount(Long count) {
            dto.setStoreProductCount(count);
            return this;
        }
        public Builder storeRating(java.math.BigDecimal v) {
            dto.setStoreRating(v);
            return this;
        }
        public Builder productRating(java.math.BigDecimal v) {
            dto.setProductRating(v);
            return this;
        }
        public Builder productRatingCount(Long v) {
            dto.setProductRatingCount(v);
            return this;
        }

        public ProductDetailResponse build() {
            return dto;
        }
        
    }

    public static Builder builder() {
        return new Builder();
    }
}
