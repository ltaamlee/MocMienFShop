package mocmien.com.dto.request.store;
import java.util.List;

import lombok.Data;

@Data
public class StoreRegisterRequest {
    private Integer vendorId;

    private String storeName;
    private String address;

    private String avatar;        // url đã upload Cloudinary
    private String cover;         // url đã upload Cloudinary
    private List<String> featureImages; // list url ảnh nổi bật

    private Boolean isOpen;       // vendor muốn bật mở cửa ngay không (default false)

	public Integer getVendorId() {
		return vendorId;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public List<String> getFeatureImages() {
		return featureImages;
	}

	public void setFeatureImages(List<String> featureImages) {
		this.featureImages = featureImages;
	}

	public Boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}
    
    
}
