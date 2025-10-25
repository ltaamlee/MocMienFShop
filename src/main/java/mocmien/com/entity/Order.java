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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "oderId")
	private Integer orderId;

	@ManyToOne
	@JoinColumn(name = "customerId", nullable = false)
	private Customer customer; // khách hàng

	@ManyToOne
	@JoinColumn(name = "sellerId", nullable = false)
	private Seller seller; // shop bán hàng

	@ManyToOne
	@JoinColumn(name = "ShipperId")
	private Shipper shipper; // shipper phụ trách


	@Column(name = "OrderStatus")
	private String orderStatus;
	// NEW, CONFIRMED, SHIPPING, DELIVERED, CANCELED, RETURNED

	@Column(name = "TotalAmount")
	private Double totalAmount; // tổng tiền đơn hàng

	@Column(name = "PaymentMethod", length = 50)
	private String paymentMethod; // COD, VNPAY, MOMO...

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems; // danh sách sản phẩm

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
