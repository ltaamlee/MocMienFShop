package mocmien.com.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import mocmien.com.enums.EmployeePosition;
import jakarta.persistence.Column;
/*import jakarta.persistence.Convert;*/
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NhanVien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MaNV")
	private Integer maNV;

	@Column(name = "HoTen", nullable = false, columnDefinition = "nvarchar(100)")
	private String hoTen;

	@Enumerated(EnumType.STRING)
    @Column(name = "ChucVu", columnDefinition = "nvarchar(50)", nullable=false)
    private EmployeePosition chucVu;

	@Column(name = "Email", length = 100, unique = true)
	private String email;

	@Column(name = "SDT", length = 15)
	private String sdt;

	@OneToOne
	@JoinColumn(name = "UserID", referencedColumnName = "UserID")
	@JsonBackReference
	private User user;

	public Integer getMaNV() {
		return maNV;
	}

	public void setMaNV(Integer maNV) {
		this.maNV = maNV;
	}

	public String getHoTen() {
		return hoTen;
	}

	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}

	public EmployeePosition getChucVu() {
		return chucVu;
	}

	public void setChucVu(EmployeePosition chucVu) {
		this.chucVu = chucVu;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSdt() {
		return sdt;
	}

	public void setSdt(String sdt) {
		this.sdt = sdt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
