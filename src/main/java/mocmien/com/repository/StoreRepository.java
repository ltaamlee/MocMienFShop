package mocmien.com.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.enums.UserStatus;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer>{
    // Thống kê số lượng cửa hàng
    // -----------------------
    long countByIsActive(Boolean isActive);
    long countByIsOpen(Boolean isOpen);
	Page<Store> findByStoreNameContainingIgnoreCase(String keyword, Pageable pageable);
	Page<Store> findByStoreNameContainingIgnoreCaseAndIsActive(String keyword, boolean isActive, Pageable pageable);
	Page<Store> findByIsActive(boolean isActive, Pageable pageable);


}
