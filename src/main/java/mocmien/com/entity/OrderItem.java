package mocmien.com.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "OrderItems")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OrderItemID")
	private Integer orderItemId;

	@ManyToOne
	@JoinColumn(name = "OrderID", nullable = false)
	private Order order; // Đơn hàng chứa item này

	@ManyToOne
	@JoinColumn(name = "ProductID", nullable = false)
	private Product product; // Sản phẩm trong đơn hàng

	@Column(name = "Quantity")
	private Integer quantity; // số lượng sản phẩm

	@Column(name = "Price")
	private Double price; // giá sản phẩm tại thời điểm đặt
}
