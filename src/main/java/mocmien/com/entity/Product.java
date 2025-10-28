package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// Khóa ngoại: Category
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryId", referencedColumnName = "id", nullable = false)
	private Category category;

	// Khóa ngoại: Store
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "storeId", referencedColumnName = "id", nullable = false)
	private Store store;

	@Column(name = "productName", nullable = false, unique = true, length = 500, columnDefinition = "NVARCHAR(500)")
	private String productName;

	@Column(name = "slug", unique = true, length = 500, columnDefinition = "NVARCHAR(500)")
	private String slug;

	@Column(name = "price", nullable = false, columnDefinition = "DECIMAL(18,2)")
	private BigDecimal price; // Giá gốc

	@Column(name = "promotionalPrice", columnDefinition = "DECIMAL(18,2)")
	private BigDecimal promotionalPrice; // Giá sau khuyến mãi

	@Column(name = "size", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
	private String size;

	@Column(name = "stock", nullable = false)
	private Integer stock = 0; // Số lượng sản phẩm còn lại

	@Column(name = "sold", nullable = false)
	private Integer sold = 0; // Số lượng đã bán

	@Column(name = "rating", nullable = false, columnDefinition = "DECIMAL(2,1)")
	private BigDecimal rating = BigDecimal.ZERO; // trung bình đánh giá (0–5)

	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true; // Được cấp phép bán hay không

	@Column(name = "isSelling", nullable = false)
	private Boolean isSelling = true; // Đang mở bán hay ẩn

	@Column(name = "isAvailable", nullable = false)
	private Boolean isAvailable = true; // Còn hàng hay hết hàng

	@Column(name = "createAt", nullable = false)
	private LocalDateTime createAt;

	@Column(name = "updateAt")
	private LocalDateTime updateAt;

	// ProductImage
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("imageIndex ASC")
	private List<ProductImage> images;

	// Review
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Review> reviews;

	@PrePersist
	protected void onCreate() {
		createAt = LocalDateTime.now();
		updateAt = LocalDateTime.now();
		if (promotionalPrice == null)
			promotionalPrice = price;
	}

	@PreUpdate
	protected void onUpdate() {
		updateAt = LocalDateTime.now();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
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

	public BigDecimal getRating() {
		return rating;
	}

	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsSelling() {
		return isSelling;
	}

	public void setIsSelling(Boolean isSelling) {
		this.isSelling = isSelling;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	public LocalDateTime getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}

	public List<ProductImage> getImages() {
		return images;
	}

	public void setImages(List<ProductImage> images) {
		this.images = images;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

}