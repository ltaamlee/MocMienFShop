package mocmien.com.repository;

import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import mocmien.com.entity.CustomerAddress;
import mocmien.com.entity.Level;
import mocmien.com.entity.UserProfile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer> {

	// =====================================================
    // CRUD cơ bản
    // =====================================================
    void deleteByCustomer(UserProfile customer);

    // =====================================================
    // 🔍 TÌM KIẾM
    // =====================================================
    List<CustomerAddress> findByCustomer(UserProfile customer);
    Optional<CustomerAddress> findByCustomerAndIsDefaultTrue(UserProfile customer);

    // Tìm tất cả địa chỉ có tỉnh/thành phố hoặc huyện cụ thể
    List<CustomerAddress> findByProvinceContainingIgnoreCase(String province);
    List<CustomerAddress> findByDistrictContainingIgnoreCase(String district);
    List<CustomerAddress> findByWardContainingIgnoreCase(String ward);

    // =====================================================
    // KIỂM TRA TỒN TẠI
    // =====================================================
    boolean existsByCustomerAndIsDefaultTrue(UserProfile customer);
    boolean existsByLineAndWardAndDistrictAndProvince(String line, String ward, String district, String province);

    // =====================================================
    // THỐNG KÊ & ĐẾM
    // =====================================================
    long countByCustomer(UserProfile customer);

    // =====================================================
    // PHÂN TRANG / SẮP XẾP
    // =====================================================
    Page<CustomerAddress> findByCustomer(UserProfile customer, Pageable pageable);
    Page<CustomerAddress> findByProvinceContainingIgnoreCase(String province, Pageable pageable);

    // =====================================================
    // CUSTOM QUERIES KHÁC
    // =====================================================
    // Bỏ mặc định tất cả địa chỉ của khách hàng (dùng khi set địa chỉ mới là mặc định)
    void deleteByCustomerAndIsDefaultTrue(UserProfile customer);
}