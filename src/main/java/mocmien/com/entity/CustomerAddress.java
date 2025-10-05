package mocmien.com.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CustomerAddresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddress {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AddressID")
	private Integer addressId;

	@ManyToOne
	@JoinColumn(name = "CustomerID", nullable = false)
	private Customer customer; // liên kết với Customer

	@Column(name = "Address", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
	private String address;

	@Column(name = "Label", length = 50, columnDefinition = "nvarchar(50)")
	private String label; // ví dụ: Nhà riêng, Cơ quan

	@Column(name = "IsDefault")
	private Boolean isDefault = false; // địa chỉ mặc định

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
