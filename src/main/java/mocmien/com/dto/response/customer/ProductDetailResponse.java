package mocmien.com.dto.response.customer;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
	private Integer maSP;
    private String tenSP;
    private BigDecimal gia;
    private String moTa;
    private String danhMuc;
    private List<String> hinhAnh;
    
	public Integer getMaSP() {
		return maSP;
	}
	public void setMaSP(Integer maSP) {
		this.maSP = maSP;
	}
	public String getTenSP() {
		return tenSP;
	}
	public void setTenSP(String tenSP) {
		this.tenSP = tenSP;
	}
	public BigDecimal getGia() {
		return gia;
	}
	public void setGia(BigDecimal gia) {
		this.gia = gia;
	}
	public String getMoTa() {
		return moTa;
	}
	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}
	public String getDanhMuc() {
		return danhMuc;
	}
	public void setDanhMuc(String danhMuc) {
		this.danhMuc = danhMuc;
	}
	public List<String> getHinhAnh() {
		return hinhAnh;
	}
	public void setHinhAnh(List<String> hinhAnh) {
		this.hinhAnh = hinhAnh;
	}
}

