package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import mocmien.com.dto.response.store.StoreResponse;
import mocmien.com.dto.response.store.StoreStats;
import mocmien.com.dto.response.users.UserStats;
import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.enums.UserStatus;
import mocmien.com.repository.StoreRepository;
import mocmien.com.service.StoreService;

@Service
public class StoreServiceImpl implements StoreService {

	@Autowired
	private StoreRepository storeRepository;
	
	@Override
	public StoreStats getStoreStatistics() {
		long total = storeRepository.count(); // tổng số user
		long active = storeRepository.countByIsOpen(true); // online
		long inactive = storeRepository.countByIsOpen(true); // offline
		long blocked = storeRepository.countByIsActive(false);

		return new StoreStats(total, active, inactive, blocked);
	}

	public Page<StoreResponse> getStores(Pageable pageable, String keyword, Boolean active) {
	    Page<Store> stores;

	    if (keyword != null && !keyword.isEmpty() && active != null) {
	        stores = storeRepository.findByStoreNameContainingIgnoreCaseAndIsActive(keyword, active, pageable);
	    } else if (keyword != null && !keyword.isEmpty()) {
	        stores = storeRepository.findByStoreNameContainingIgnoreCase(keyword, pageable);
	    } else if (active != null) {
	        stores = storeRepository.findByIsActive(active, pageable);
	    } else {
	        stores = storeRepository.findAll(pageable);
	    }

	    return stores.map(store -> new StoreResponse(
	            store.getId(),
	            store.getStoreName(),
	            store.getVendor().getUsername(),
	            store.getPoint(),
	            store.geteWallet(),
	            store.getRating(),
	            store.isActive(),
	            store.isOpen()
	    ));
	}


	@Override
	public void deleteStore(Integer storeId) {
		storeRepository.deleteById(storeId);
	}

	@Override
	public void changeBlock(Integer storeId) {
		storeRepository.findById(storeId).ifPresent(store -> {

			// 1. Lấy trạng thái ngược lại (Chuyển đổi trạng thái)
			boolean newStatus = !store.isActive();

			// 2. Cập nhật trạng thái
			store.setActive(newStatus);
			storeRepository.save(store);

			// 3. Log hành động (để tiện kiểm tra)
			String action = newStatus ? "UNBLOCKED (Active=true)" : "BLOCKED (Active=false)";
			System.out.println("[USER STATUS TOGGLED] " + store.getStoreName() + " -> " + action);
		});
		
	}



}
