package mocmien.com.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ShippingCompanies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingCompany {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CompanyID")
	private Integer companyId;

	@Column(name = "CompanyName", nullable = false, unique = true, columnDefinition = "nvarchar(100)")
	private String companyName;

	@Column(name = "ContactPhone", length = 20)
	private String contactPhone;

	@Column(name = "ContactEmail", length = 100)
	private String contactEmail;

	@Column(name = "BaseFee")
	private Double baseFee; // phí cơ bản (ví dụ 15.000đ)

	@Column(name = "FeePerKm")
	private Double feePerKm; // phí thêm theo km (nếu có)

	@Column(name = "DeliveryTimeEstimate", length = 50)
	private String deliveryTimeEstimate; // ví dụ: 1-3 ngày

	@Column(name = "Status")
	private Integer status = 1; // 1: hoạt động, 0: ngừng

	@Column(name = "CreatedAt")
	private LocalDateTime createdAt;

	@Column(name = "UpdatedAt")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
