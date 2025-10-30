package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Shipper")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Liên kết với User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // Liên kết với Delivery
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliveryId", nullable = false)
    private Delivery delivery;

    @Column(name = "vehicleNumber", nullable = false, unique = true, length = 50)
    private String vehicleNumber; // Biển số xe

    @Column(name = "vehicleType", nullable = false, unique = true, columnDefinition = "NVARCHAR(50)")
    private String vehicleType; // Loại phương tiện (xe máy, ô tô, ...)

    @Column(name = "license", length = 100)
    private String license; // Số bằng lái hoặc đường dẫn ảnh bằng lái

    @Column(name = "rating", columnDefinition = "DECIMAL(2,1)", nullable = false)
    private BigDecimal rating = BigDecimal.ZERO; // Điểm đánh giá trung bình

    @Column(name = "totalDelivery")
    private Integer totalDelivery = 0; // Tổng số đơn hàng đã giao

    @Column(name = "eWallet", columnDefinition = "DECIMAL(18,2)")
    private BigDecimal eWallet = BigDecimal.ZERO; // Số dư ví điện tử

    @Column(name = "isActive", nullable = false)
    private Boolean isActive = false; // Được cấp phép hoạt động (duyệt tài khoản)

    @Column(name = "isOnline", nullable = false)
    private Boolean isOnline = false; // Trạng thái trực tuyến để nhận đơn
    
    @OneToMany(mappedBy = "shipper", fetch = FetchType.LAZY)
    private List<Orders> orders; // Danh sách đơn hàng mà shipper đang phụ trách

    // Vị trí hiện tại (tùy chọn)
    @Column(name = "currentLat")
    private Double currentLat;

    @Column(name = "currentLng")
    private Double currentLng;

    @Column(name = "currentAddress", columnDefinition = "NVARCHAR(500)")
    private String currentAddress;


    @Column(name = "createAt", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "updateAt")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public BigDecimal getRating() {
		return rating;
	}

	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

	public Integer getTotalDelivery() {
		return totalDelivery;
	}

	public void setTotalDelivery(Integer totalDelivery) {
		this.totalDelivery = totalDelivery;
	}

	public BigDecimal geteWallet() {
		return eWallet;
	}

	public void seteWallet(BigDecimal eWallet) {
		this.eWallet = eWallet;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public List<Orders> getOrders() {
		return orders;
	}

	public void setOrders(List<Orders> orders) {
		this.orders = orders;
	}

	public Double getCurrentLat() {
		return currentLat;
	}

	public void setCurrentLat(Double currentLat) {
		this.currentLat = currentLat;
	}

	public Double getCurrentLng() {
		return currentLng;
	}

	public void setCurrentLng(Double currentLng) {
		this.currentLng = currentLng;
	}

	public String getCurrentAddress() {
		return currentAddress;
	}

	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
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
    
    
    
}