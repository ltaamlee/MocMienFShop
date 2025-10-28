package mocmien.com.dto.request.store;

import lombok.Data;

@Data
public class ToggleOpenRequest {
    private Boolean open; // true = mở, false = tắt

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}
    
}
