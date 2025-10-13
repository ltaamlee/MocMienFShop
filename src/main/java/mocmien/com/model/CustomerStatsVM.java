package mocmien.com.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatsVM {
	private long tongKhachHang;
	private long tongTheoLoc;

	public CustomerStatsVM(long tongKhachHang, long tongTheoLoc) {
		this.tongKhachHang = tongKhachHang;
		this.tongTheoLoc = tongTheoLoc;
	}

	public long getTongKhachHang() {
		return tongKhachHang;
	}

	public void setTongKhachHang(long tongKhachHang) {
		this.tongKhachHang = tongKhachHang;
	}

	public long getTongTheoLoc() {
		return tongTheoLoc;
	}

	public void setTongTheoLoc(long tongTheoLoc) {
		this.tongTheoLoc = tongTheoLoc;
	}
}
