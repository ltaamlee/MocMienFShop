package mocmien.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Staff;
import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.enums.StaffPosition;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer>{
	// -----------------------
    // Tìm kiếm theo User
    // -----------------------
    Optional<Staff> findByUser(User user);
    Optional<Staff> findByUserId(Integer userId);

    // -----------------------
    // Tìm kiếm theo Store
    // -----------------------
    List<Staff> findByStore(Store store);
    List<Staff> findByStoreId(Integer storeId);

    // -----------------------
    // Tìm kiếm theo vị trí / chức vụ
    // -----------------------
    List<Staff> findByPosition(StaffPosition position);
    List<Staff> findByStoreAndPosition(Store store, StaffPosition position);

    // -----------------------
    // Trạng thái hoạt động
    // -----------------------
    List<Staff> findByIsActive(Boolean isActive);
    List<Staff> findByStoreAndIsActive(Store store, Boolean isActive);

    // -----------------------
    // Phân trang
    // -----------------------
    Page<Staff> findByIsActive(Boolean isActive, Pageable pageable);
    Page<Staff> findByStore(Store store, Pageable pageable);
    Page<Staff> findByStoreId(Integer storeId, Pageable pageable);
    
    // =======================
    // THỐNG KÊ / ĐẾM
    // =======================
    long countByIsActive(Boolean isActive); // đếm tổng nhân viên đang hoạt động
    long countByStore(Store store); // đếm số nhân viên trong cửa hàng
    long countByStoreAndIsActive(Store store, Boolean isActive); // đếm nhân viên hoạt động theo cửa hàng
    long countByPosition(StaffPosition position); // đếm theo chức vụ
    long countByStoreAndPosition(Store store, StaffPosition position); // đếm theo cửa hàng + chức vụ
    long countByStoreIdAndIsActive(Integer storeId, Boolean isActive); // đếm theo storeId và trạng thái
}
