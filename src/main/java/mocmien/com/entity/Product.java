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
	@JoinColumn(name = "storeId", referencedColumnName = "id",  nullable = false)
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
	@OrderBy("index ASC")
	private List<ProductImage> images;
	
	// Review
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Review> reviews;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductFlower> productFlowers;

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
}