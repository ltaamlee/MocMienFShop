package mocmien.com.dto.response.store;

import java.math.BigDecimal;

public record StoreResponse(
		 Integer id,
		    String storeName,
		    String vendorName,
		    Integer point,
		    BigDecimal eWallet,
		    BigDecimal rating,
		    boolean isActive,
		    boolean isOpen	
		
		) {

}
