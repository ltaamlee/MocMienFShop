package mocmien.com.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Shippers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipper {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ShipperID")
	private Integer shipperId;

	@OneToOne
	@JoinColumn(name = "UserID", nullable = false, unique = true)
	private User user;

	@Column(name = "VehicleType", length = 50, columnDefinition = "nvarchar(50)")
	private String vehicleType; // ví dụ: xe máy, xe tải, xe đạp,...

	@Column(name = "LicensePlate", length = 20)
	private String licensePlate;

	@Column(name = "DeliveryArea", length = 100, columnDefinition = "nvarchar(100)")
	private String deliveryArea; // ví dụ: Quận 1, Quận 7,...

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
