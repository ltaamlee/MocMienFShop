package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import mocmien.com.dto.response.store.AdminStoreResponse;
import mocmien.com.dto.response.store.StoreStats;
import mocmien.com.dto.response.users.UserStats;
import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.enums.UserStatus;
import mocmien.com.repository.StoreRepository;
import mocmien.com.service.StoreService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class StoreServiceImpl implements StoreService {

	private final StoreRepository storeRepository;

	public StoreServiceImpl(StoreRepository storeRepository) {
		this.storeRepository = storeRepository;
	}
	
	@Override
	public StoreStats getStoreStatistics() {
		long total = storeRepository.count(); // tổng số user
		long active = storeRepository.countByIsOpen(true); // online
		long inactive = storeRepository.countByIsOpen(true); // offline
		long blocked = storeRepository.countByIsActive(false);

		return new StoreStats(total, active, inactive, blocked);
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

	@Override
	public Optional<Store> findById(Integer id) {
		return storeRepository.findById(id);
	}
	
	// CRUD
	@Override
	public Store save(Store store) {
		return storeRepository.save(store);
	}

	// Vendor
	@Override
	public List<Store> findByVendor(User vendor) {
		return storeRepository.findByVendor(vendor);
	}

	@Override
	public Optional<Store> findByIdAndVendor(Integer id, User vendor) {
		return storeRepository.findByIdAndVendor(id, vendor);
	}

	// Name
	@Override
	public List<Store> findByStoreNameContainingIgnoreCase(String keyword) {
		return storeRepository.findByStoreNameContainingIgnoreCase(keyword);
	}

	@Override
	public Optional<Store> findByStoreName(String storeName) {
		return storeRepository.findByStoreName(storeName);
	}

	// Status
	@Override
	public List<Store> findByIsActive(Boolean isActive) {
		return storeRepository.findByIsActive(isActive);
	}

	@Override
	public List<Store> findByIsOpen(Boolean isOpen) {
		return storeRepository.findByIsOpen(isOpen);
	}

	// Paging
	@Override
	public Page<Store> findByIsActive(Boolean isActive, Pageable pageable) {
		return storeRepository.findByIsActive(isActive, pageable);
	}

	@Override
	public Page<Store> findByStoreNameContainingIgnoreCase(String keyword, Pageable pageable) {
		return storeRepository.findByStoreNameContainingIgnoreCase(keyword, pageable);
	}


	@Override
	public long countByIsActive(Boolean isActive) {
		return storeRepository.countByIsActive(isActive);
	}

	@Override
	public long countByIsOpen(Boolean isOpen) {
		return storeRepository.countByIsOpen(isOpen);
	}

	@Override
	public List<Store> findByPointGreaterThanEqual(Integer point) {
		return storeRepository.findByPointGreaterThanEqual(point);
	}

	@Override
	public List<Store> findByRatingGreaterThanEqual(BigDecimal rating) {
		return storeRepository.findByRatingGreaterThanEqual(rating);
	}

	@Override
	public int updateAfterOrder(Integer storeId, BigDecimal revenue, BigDecimal rating, Integer points) {
		return storeRepository.updateAfterOrder(storeId, revenue, rating, points);
	}

	
	@Override
	@Transactional(readOnly = true)
	public Page<AdminStoreResponse> findAll(String keyword, Boolean isActive, Pageable pageable) {
		
		Specification<Store> spec = (root, query, cb) -> null;
		
		if (StringUtils.hasText(keyword)) {
		    spec = spec.and((root, query, cb) -> 
		        cb.or(
		            cb.like(cb.lower(root.get("storeName")), "%" + keyword.trim().toLowerCase() + "%"),
		            cb.like(cb.lower(root.join("vendor").get("email")), "%" + keyword.trim().toLowerCase() + "%")
		        )
		    );
		}


	    if (isActive != null) {
	        spec = spec.and((root, query, cb) ->
	            cb.equal(root.get("isActive"), isActive)
	        );
	    }

	    // fetch bằng EntityGraph -> vendor và userProfile sẽ được load luôn
	    Page<Store> stores = storeRepository.findAll(spec, pageable);

	    // map sang DTO
	    return stores.map(store -> {
	        AdminStoreResponse res = new AdminStoreResponse();
	        res.setId(store.getId());
	        res.setStoreName(store.getStoreName());

	        if (store.getVendor() != null && store.getVendor().getUserProfile() != null) {
	            res.setVendorId(store.getVendor().getUserId());
	            res.setVendorName(store.getVendor().getUserProfile().getFullName());
	            res.setVendorEmail(store.getVendor().getEmail());
	            res.setVendorPhone(store.getVendor().getPhone());
	        }

	        res.setLevelName(store.getLevel() != null ? store.getLevel().getName().getDisplayName() : null);
	        res.setPhone(store.getPhone());
	        res.setAddress(store.getAddress());
	        res.setAvatar(store.getAvatar());
	        res.setCover(store.getCover());
	        res.setPoint(store.getPoint());
	        res.setRating(store.getRating());
	        res.seteWallet(store.geteWallet());
	        res.setActive(store.isActive());
	        res.setOpen(store.isOpen());
	        res.setCreateAt(store.getCreateAt());
	        res.setUpdateAt(store.getUpdateAt());

	        return res;
	    });
	}

	@Override
	public List<Store> getAll() {
		return storeRepository.findAll();
	}


    
}
