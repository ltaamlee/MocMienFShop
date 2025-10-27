package mocmien.com.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.repository.StoreRepository;
import mocmien.com.service.StoreService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
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

    // Stats
    @Override
    public BigDecimal totalEWallet() {
        return storeRepository.totalEWallet();
    }

    @Override
    public Integer totalPoints() {
        return storeRepository.totalPoints();
    }

    @Override
    public BigDecimal averageRating() {
        return storeRepository.averageRating();
    }

    @Override
    public List<Store> findTopStoresByRating(Pageable pageable) {
        return storeRepository.findTopStoresByRating(pageable);
    }

    @Override
    public List<Store> findTopStoresByPoints(Pageable pageable) {
        return storeRepository.findTopStoresByPoints(pageable);
    }

    // Mutations
    @Override
    public int addToEWallet(Integer storeId, BigDecimal amount) {
        return storeRepository.addToEWallet(storeId, amount);
    }

    @Override
    public int addRating(Integer storeId, BigDecimal rating) {
        return storeRepository.addRating(storeId, rating);
    }

    @Override
    public int addPoints(Integer storeId, Integer points) {
        return storeRepository.addPoints(storeId, points);
    }

    @Override
    public int setOpenStatus(Integer storeId, boolean isOpen) {
        return storeRepository.setOpenStatus(storeId, isOpen);
    }

    @Override
    public int setActiveStatus(Integer storeId, boolean isActive) {
        return storeRepository.setActiveStatus(storeId, isActive);
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
}