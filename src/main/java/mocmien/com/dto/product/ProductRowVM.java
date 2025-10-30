package mocmien.com.dto.product;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRowVM {
	private Integer maSP;
	private String tenSP;
	private BigDecimal giaGoc;
    private BigDecimal giaKhuyenMai; // null if no discount
	private String hinhAnh;
	private Integer trangThai;
    private Integer discountPercent; // rounded down
    private String ribbonText; // e.g: "-20%" or promotion name
}
