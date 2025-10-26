package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.service.StoreService;

@Service
public class StoreServiceImpl implements StoreService{

	@Override
	public List<Store> findByVendor(User vendor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Store> findByIdAndVendor(Integer id, User vendor) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<Store> findByStoreNameContainingIgnoreCase(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Store> findByStoreName(String storeName) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<Store> findByIsActive(Boolean isActive) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Store> findByIsOpen(Boolean isOpen) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Store> findByIsActive(Boolean isActive, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Store> findByStoreNameContainingIgnoreCase(String keyword, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal totalEWallet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer totalPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal averageRating() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Store> findTopStoresByRating(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Store> findTopStoresByPoints(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int addToEWallet(Integer storeId, BigDecimal amount) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addRating(Integer storeId, BigDecimal rating) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addPoints(Integer storeId, Integer points) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setOpenStatus(Integer storeId, boolean isOpen) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setActiveStatus(Integer storeId, boolean isActive) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long countByIsActive(Boolean isActive) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long countByIsOpen(Boolean isOpen) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Store> findByPointGreaterThanEqual(Integer point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Store> findByRatingGreaterThanEqual(BigDecimal rating) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateAfterOrder(Integer storeId, BigDecimal revenue, BigDecimal rating, Integer points) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
