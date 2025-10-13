package mocmien.com.enums;

import mocmien.com.enums.CustomerRank;

public enum CustomerRank {
	THUONG("Thường"), 
	BAC("Bạc"), 
	VANG("Vàng"), 
	KIM_CUONG("Kim Cương");

	public final String displayName;

	CustomerRank(String displayName) {
		this.displayName = displayName;
	}

	public static CustomerRank ofTotalOrders(long total) {
		if (total >= 30)
			return KIM_CUONG;
		if (total >= 15)
			return VANG;
		if (total >= 5)
			return BAC;
		return THUONG;
	}
}
