package mocmien.com.dto.response.product;

import java.math.BigDecimal;

public class ProductRowVM {
	private Integer maSP;
	private String tenSP;
	private String hinhAnh;
	private BigDecimal giaGoc;
	private Integer trangThai;
	private String trangThaiText;

	public ProductRowVM() {
	}

	public ProductRowVM(Integer maSP, String tenSP, String hinhAnh, BigDecimal giaGoc, Integer trangThai,
			String trangThaiText) {
		this.maSP = maSP;
		this.tenSP = tenSP;
		this.hinhAnh = hinhAnh;
		this.giaGoc = giaGoc;
		this.trangThai = trangThai;
		this.trangThaiText = trangThaiText;
	}

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

	public String getHinhAnh() {
		return hinhAnh;
	}

	public void setHinhAnh(String hinhAnh) {
		this.hinhAnh = hinhAnh;
	}

	public BigDecimal getGiaGoc() {
		return giaGoc;
	}

	public void setGiaGoc(BigDecimal giaGoc) {
		this.giaGoc = giaGoc;
	}

	public Integer getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(Integer trangThai) {
		this.trangThai = trangThai;
	}

	public String getTrangThaiText() {
		return trangThaiText;
	}

	public void setTrangThaiText(String trangThaiText) {
		this.trangThaiText = trangThaiText;
	}
}
