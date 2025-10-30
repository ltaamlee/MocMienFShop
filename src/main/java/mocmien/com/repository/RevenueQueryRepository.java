package mocmien.com.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RevenueQueryRepository {

	// Interface “projection” dùng cho chart
	public static interface Point {
		Integer getOrderYear();

		Integer getOrderMonth();

		Integer getOrderDay();

		BigDecimal getTotal();
	}
}
