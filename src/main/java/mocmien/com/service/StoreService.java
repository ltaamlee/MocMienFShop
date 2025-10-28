package mocmien.com.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mocmien.com.dto.response.store.StoreResponse;
import mocmien.com.dto.response.store.StoreStats;
import mocmien.com.entity.Store;


public interface StoreService {
	
	//Thống kê
	StoreStats getStoreStatistics();

	public Page<StoreResponse> getStores(Pageable pageable, String keyword, Boolean active);

	void deleteStore(Integer id);

	void changeBlock(Integer storeId);

	Optional<Store> findById(Integer id);
}
