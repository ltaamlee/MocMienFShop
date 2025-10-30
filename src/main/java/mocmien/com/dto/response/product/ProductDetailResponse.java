package mocmien.com.dto.response.product;

import java.math.BigDecimal;
import java.util.List;

import mocmien.com.enums.ProductStatus;

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

    private BigDecimal giaKhuyenMai;
    private Integer discountPercent;
    private String ribbonText;
    private Integer promotionId;

    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }

    
    

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

	public BigDecimal getGiaKhuyenMai() {
		return giaKhuyenMai;
	}

	public void setGiaKhuyenMai(BigDecimal giaKhuyenMai) {
		this.giaKhuyenMai = giaKhuyenMai;
	}

	public Integer getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(Integer discountPercent) {
		this.discountPercent = discountPercent;
	}

	public String getRibbonText() {
		return ribbonText;
	}

	public void setRibbonText(String ribbonText) {
		this.ribbonText = ribbonText;
	}

	public Integer getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Integer promotionId) {
		this.promotionId = promotionId;
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

        public ProductDetailResponse build() {
            return dto;
        }
        
        public Builder mainImage(String mainImage) {
            dto.setMainImage(mainImage);
            return this;
        }

        public Builder giaKhuyenMai(BigDecimal giaKhuyenMai) {
            dto.setGiaKhuyenMai(giaKhuyenMai);
            return this;
        }

        public Builder discountPercent(Integer discountPercent) {
            dto.setDiscountPercent(discountPercent);
            return this;
        }

        public Builder ribbonText(String ribbonText) {
            dto.setRibbonText(ribbonText);
            return this;
        }

        public Builder promotionId(Integer promotionId) {
            dto.setPromotionId(promotionId);
            return this;
        }

    }

    public static Builder builder() {
        return new Builder();
    }
}
