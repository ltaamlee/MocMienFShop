package mocmien.com.entity;

import java.math.BigDecimal;
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
@Table(name = "Delivery")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "deliveryName", nullable = false, unique = true, length = 500)
	private String deliveryName;

	@Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
	private String description;

	@Column(name = "basePrice", nullable = false, precision = 18, scale = 2)
	private BigDecimal basePrice;

	@Column(name = "pricePerKM", precision = 18, scale = 2)
	private BigDecimal pricePerKM = BigDecimal.ZERO;

	@Column(name = "maxDistance")
	private Integer maxDistance;

	@Column(name = "isActive", nullable = false)
	private Boolean isActive = false;

	@Column(name = "createAt")
	private LocalDateTime createAt;

	@Column(name = "updateAt")
	private LocalDateTime updateAt;

	@PrePersist
	public void prePersist() {
		createAt = LocalDateTime.now();
		if (isActive == null)
			isActive = false;
	}

	@PreUpdate
	public void preUpdate() {
		updateAt = LocalDateTime.now();
	}
}
