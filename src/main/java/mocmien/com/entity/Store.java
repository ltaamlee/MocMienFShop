package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocmien.com.enums.Rank;

@Entity
@Table(name = "Store")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Store {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	// Hạng cửa hàng
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "level", nullable = false)
	private Level level;

    // Chủ cửa hàng
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendorId", nullable = false)
    private User vendor;

    @Column(name = "storeName", nullable = false, unique = true, columnDefinition = "nvarchar(500)")
    private String storeName;

	@Column(name = "phone", columnDefinition = "varchar(20)")
	private String phone;
    
    @Column(name = "address", columnDefinition = "nvarchar(500)")
    private String address;

	@Column(name = "avatar", columnDefinition = "varchar(MAX)")
	private String avatar;

	@Column(name = "cover", columnDefinition = "varchar(MAX)")
	private String cover;

    @Column(name = "slug", unique = true, columnDefinition = "nvarchar(500)")
    private String slug;


	// Danh sách ảnh nổi bật
	@ElementCollection
	@Column(name = "featureImages")
	private List<String> featureImages;

	@Column(name = "point", nullable = false, columnDefinition = "int default 0")
	private Integer point = 0;

	@Column(name = "eWallet", nullable = false, columnDefinition = "DECIMAL(18,2)")
	private BigDecimal eWallet = BigDecimal.ZERO;

	@Column(name = "rating", nullable = false, columnDefinition = "DECIMAL(2,1)")
	private BigDecimal rating = BigDecimal.ZERO;

	@Column(name = "isActive", nullable = false, columnDefinition = "BIT DEFAULT 1")
	private boolean isActive = true;

	@Column(name = "isOpen", nullable = false)
	private boolean isOpen = true;

	@Column(name = "createdAt")
	private LocalDateTime createdAt;

	@Column(name = "updatedAt")
	private LocalDateTime updatedAt;

	// ==============================
	// Callback tự động cập nhật ngày
	// ==============================
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public User getVendor() {
		return vendor;
	}

	public void setVendor(User vendor) {
		this.vendor = vendor;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public List<String> getFeatureImages() {
		return featureImages;
	}

	public void setFeatureImages(List<String> featureImages) {
		this.featureImages = featureImages;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public BigDecimal geteWallet() {
		return eWallet;
	}

	public void seteWallet(BigDecimal eWallet) {
		this.eWallet = eWallet;
	}

	public BigDecimal getRating() {
		return rating;
	}

	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public LocalDateTime getCreateAt() {
		return createdAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createdAt = createAt;
	}

	public LocalDateTime getUpdateAt() {
		return updatedAt;
	}

	public void setUpdateAt(LocalDateTime updateAt) {
		this.updatedAt = updateAt;
	}

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

	
}