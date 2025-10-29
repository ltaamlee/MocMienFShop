package mocmien.com.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mocmien.com.dto.response.store.AdminStoreResponse;
import mocmien.com.dto.response.store.StoreStats;
import mocmien.com.entity.Store;
import mocmien.com.entity.User;


public interface StoreService {
	
	//Thống kê
	StoreStats getStoreStatistics();
	
	Page<AdminStoreResponse> findAll(String keyword, Boolean isActive, Pageable pageable);
	

	// CRUD cơ bản
    Store save(Store store);
	
	// -----------------------
    // Tìm kiếm theo chủ cửa hàng (vendor)
    // -----------------------
    List<Store> findByVendor(User vendor);
    Optional<Store> findByIdAndVendor(Integer id, User vendor);

	void deleteStore(Integer id);

	void changeBlock(Integer storeId);

	Optional<Store> findById(Integer id);

	Page<Store> findByIsActive(Boolean isActive, Pageable pageable);

	Page<Store> findByStoreNameContainingIgnoreCase(String keyword, Pageable pageable);

	List<Store> findByIsOpen(Boolean isOpen);
	List<Store> findByIsActive(Boolean isActive);

	Optional<Store> findByStoreName(String storeName);

	List<Store> findByStoreNameContainingIgnoreCase(String keyword);

	List<Store> findByRatingGreaterThanEqual(BigDecimal rating);

	int updateAfterOrder(Integer storeId, BigDecimal revenue, BigDecimal rating, Integer points);

	List<Store> findByPointGreaterThanEqual(Integer point);

	long countByIsOpen(Boolean isOpen);

	long countByIsActive(Boolean isActive);

}
