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
	private UserProfile customer;
	
	@Column(name = "fullName", columnDefinition = "nvarchar(200)")
	private String fullName;

	@Column(name = "phone", columnDefinition = "varchar(20)")
	private String phone;

	
	@Column(name = "latitude", columnDefinition = "DECIMAL(10,7)")
	private BigDecimal latitude;

	@Column(name = "longitude", columnDefinition = "DECIMAL(10,7)")
	private BigDecimal longitude;


	@Column(name = "line", columnDefinition = "nvarchar(200)")
	private String line; // số nhà

	@Column(name = "ward", columnDefinition = "nvarchar(200)")
	private String ward; // phường/xã

	@Column(name = "district", columnDefinition = "nvarchar(200)")
	private String district; // huyện

	@Column(name = "province", columnDefinition = "nvarchar(200)")
	private String province; // tỉnh/thành phố

	@Column(name = "isDefault", nullable = false)
	private boolean isDefault = false;
	
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public String getFullName() { return fullName; }
	public void setFullName(String fullName) { this.fullName = fullName; }

	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }

	public String getLine() { return line; }
	public void setLine(String line) { this.line = line; }

	public String getWard() { return ward; }
	public void setWard(String ward) { this.ward = ward; }

	public String getDistrict() { return district; }
	public void setDistrict(String district) { this.district = district; }

	public String getProvince() { return province; }
	public void setProvince(String province) { this.province = province; }

	public boolean getIsDefault() { return isDefault; }
	public void setIsDefault(boolean isDefault) { this.isDefault = isDefault; }

	public UserProfile getCustomer() { return customer; }
	public void setCustomer(UserProfile customer) { this.customer = customer; }




}