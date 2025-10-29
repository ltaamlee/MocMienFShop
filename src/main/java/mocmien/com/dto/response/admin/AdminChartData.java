package mocmien.com.dto.response.admin;

import java.math.BigDecimal;
import java.util.List;

public record AdminChartData(
		List<String> labels,
	    List<BigDecimal> data 
		
		) {

}
