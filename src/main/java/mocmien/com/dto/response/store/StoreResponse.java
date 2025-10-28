package mocmien.com.dto.response.store;

import java.math.BigDecimal;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreResponse {
    private Integer id;

    private String storeName;
    private String address;

    private String avatar;
    private String cover;
    private List<String> featureImages;

    private BigDecimal eWallet;
    private BigDecimal rating;
    private Integer point;

    private boolean isActive;
    private boolean isOpen;

    private String levelName;          // Rank enum name
    private String levelDisplayName;   // Rank tiếng Việt
    private BigDecimal levelDiscount;

    private String ownerName;          // vendor.username hoặc fullName
    private String ownerPhone;
    private String ownerEmail;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public BigDecimal geteWallet() {
		return eWallet;
	}
	public void seteWallet(BigDecimal eWallet) {
		this.eWallet = eWallet;
	}
	public BigDecimal getRating() {
		return rating;
	}
	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	public String getLevelDisplayName() {
		return levelDisplayName;
	}
	public void setLevelDisplayName(String levelDisplayName) {
		this.levelDisplayName = levelDisplayName;
	}
	public BigDecimal getLevelDiscount() {
		return levelDiscount;
	}
	public void setLevelDiscount(BigDecimal levelDiscount) {
		this.levelDiscount = levelDiscount;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerPhone() {
		return ownerPhone;
	}
	public void setOwnerPhone(String ownerPhone) {
		this.ownerPhone = ownerPhone;
	}
	public String getOwnerEmail() {
		return ownerEmail;
	}
	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	public StoreResponse(Integer id, String storeName, String address, String avatar, String cover,
			List<String> featureImages, BigDecimal eWallet, BigDecimal rating, Integer point, boolean isActive,
			boolean isOpen, String levelName, String levelDisplayName, BigDecimal levelDiscount, String ownerName,
			String ownerPhone, String ownerEmail) {
		super();
		this.id = id;
		this.storeName = storeName;
		this.address = address;
		this.avatar = avatar;
		this.cover = cover;
		this.featureImages = featureImages;
		this.eWallet = eWallet;
		this.rating = rating;
		this.point = point;
		this.isActive = isActive;
		this.isOpen = isOpen;
		this.levelName = levelName;
		this.levelDisplayName = levelDisplayName;
		this.levelDiscount = levelDiscount;
		this.ownerName = ownerName;
		this.ownerPhone = ownerPhone;
		this.ownerEmail = ownerEmail;
	}
	public StoreResponse() {
		super();
	}
	
	
	
}
