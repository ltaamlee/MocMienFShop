package mocmien.com.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ChiTietDonHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
	@EmbeddedId
    private OrderDetailId id;

    @ManyToOne
    @MapsId("maDH")
    @JoinColumn(name = "MaDH")
    private Order donHang;

    @ManyToOne
    @MapsId("maSP")
    @JoinColumn(name = "MaSP")
    private Product sanPham;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "GiaBan", precision = 18, scale = 2)
    private BigDecimal giaBan;

	public OrderDetailId getId() {
		return id;
	}

	public void setId(OrderDetailId id) {
		this.id = id;
	}

	public Order getDonHang() {
		return donHang;
	}

	public void setDonHang(Order donHang) {
		this.donHang = donHang;
	}

	public Product getSanPham() {
		return sanPham;
	}

	public void setSanPham(Product sanPham) {
		this.sanPham = sanPham;
	}

	public Integer getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(Integer soLuong) {
		this.soLuong = soLuong;
	}

	public BigDecimal getGiaBan() {
		return giaBan;
	}

	public void setGiaBan(BigDecimal giaBan) {
		this.giaBan = giaBan;
	}
    
    
}
