package mocmien.com.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CustomerID")
	private Integer customerId;

	@OneToOne
	@JoinColumn(name = "UserID", nullable = false, unique = true)
	private User user; // Liên kết với User (role = USER)
	
	@Column(name = "FullName", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
	private String fullName;

	@Column(name = "Phone", length = 15)
	private String phone; // Số điện thoại chính (nếu muốn lưu riêng)

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<Address> addresses; // Danh sách địa chỉ

	@OneToMany(mappedBy = "customer")
	private List<Order> orders; // Danh sách đơn hàng

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
