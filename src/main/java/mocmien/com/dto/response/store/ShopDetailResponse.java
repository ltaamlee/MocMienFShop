package mocmien.com.dto.response.store;

import java.math.BigDecimal;
import java.util.List;

public class ShopDetailResponse {
    
    // ==========================
    // 🔹 Shop info
    // ==========================
    private Integer storeId;
    private String storeName;
    private String avatar;
    private String cover;
    private String address;
    private BigDecimal rating;
    private Integer totalProducts;
    private String phone;
    private Boolean isOpen;

    // ==========================
    // 🔹 Vendor info
    // ==========================
    private Integer vendorId;
    private String vendorName;

    // ==========================
    // 🔹 Feature images (banner)
    // ==========================
    private List<String> featureImages;

    // ==========================
    // 🔹 Shop products
    // ==========================
    private List<ShopProductDTO> products;

    // ==========================
    // 🔹 Constructors
    // ==========================
    public ShopDetailResponse() {}

    public ShopDetailResponse(Integer storeId, String storeName, String avatar, String cover, String address,
            BigDecimal rating, Integer totalProducts, String phone, Boolean isOpen,
            Integer vendorId, String vendorName, List<String> featureImages, List<ShopProductDTO> products) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.avatar = avatar;
        this.cover = cover;
        this.address = address;
        this.rating = rating;
        this.totalProducts = totalProducts;
        this.phone = phone;
        this.isOpen = isOpen;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.featureImages = featureImages;
        this.products = products;
    }

    // ==========================
    // 🔹 Getters & Setters
    // ==========================
    public Integer getStoreId() { return storeId; }
    public void setStoreId(Integer storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Integer getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getIsOpen() { return isOpen; }
    public void setIsOpen(Boolean isOpen) { this.isOpen = isOpen; }

    public Integer getVendorId() { return vendorId; }
    public void setVendorId(Integer vendorId) { this.vendorId = vendorId; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public List<String> getFeatureImages() { return featureImages; }
    public void setFeatureImages(List<String> featureImages) { this.featureImages = featureImages; }

    public List<ShopProductDTO> getProducts() { return products; }
    public void setProducts(List<ShopProductDTO> products) { this.products = products; }

    // ==========================
    // 🔹 Inner class: ShopProductDTO
    // ==========================
    public static class ShopProductDTO {
        private Integer id;
        private String productName;
        private String slug;
        private BigDecimal price;
        private BigDecimal promotionalPrice;
        private String mainImage;
        private BigDecimal rating;
        private Integer soldCount;
        private Boolean isAvailable;
        private Boolean isSelling;

        public ShopProductDTO() {}

        public ShopProductDTO(Integer id, String productName, String slug, BigDecimal price, BigDecimal promotionalPrice,
                String mainImage, BigDecimal rating, Integer soldCount, Boolean isAvailable, Boolean isSelling) {
            this.id = id;
            this.productName = productName;
            this.slug = slug;
            this.price = price;
            this.promotionalPrice = promotionalPrice;
            this.mainImage = mainImage;
            this.rating = rating;
            this.soldCount = soldCount;
            this.isAvailable = isAvailable;
            this.isSelling = isSelling;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public BigDecimal getPromotionalPrice() { return promotionalPrice; }
        public void setPromotionalPrice(BigDecimal promotionalPrice) { this.promotionalPrice = promotionalPrice; }

        public String getMainImage() { return mainImage; }
        public void setMainImage(String mainImage) { this.mainImage = mainImage; }

        public BigDecimal getRating() { return rating; }
        public void setRating(BigDecimal rating) { this.rating = rating; }

        public Integer getSoldCount() { return soldCount; }
        public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }

        public Boolean getIsAvailable() { return isAvailable; }
        public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

        public Boolean getIsSelling() { return isSelling; }
        public void setIsSelling(Boolean isSelling) { this.isSelling = isSelling; }
    }
}
