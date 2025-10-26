package mocmien.com.entity;

import java.math.BigDecimal;

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
@Table(name = "CustomerAddress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "customerId", nullable = false)
	private Customer customer;
	
	@Column(name = "latitude", columnDefinition = "DECIMAL(10,7)")
	private BigDecimal latitude;

	@Column(name = "longitude", columnDefinition = "DECIMAL(10,7)")
	private BigDecimal longitude;


	@Column(name = "line", columnDefinition = "varchar(200)")
	private String line; // số nhà

	@Column(name = "ward", columnDefinition = "nvarchar(200)")
	private String ward; // phường/xã

	@Column(name = "district", columnDefinition = "nvarchar(200)")
	private String district; // huyện

	@Column(name = "province", columnDefinition = "nvarchar(200)")
	private String province; // tỉnh/thành phố

	@Column(name = "isDefault", nullable = false)
	private boolean isDefault = false;
}