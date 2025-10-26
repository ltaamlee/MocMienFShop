package mocmien.com.repository;

import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import mocmien.com.entity.Customer;
import mocmien.com.entity.Level;
import mocmien.com.enums.Rank;

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
public interface LevelRepository extends JpaRepository<Level, Integer> {

	// =====================================================
    // CRUD cơ bản
    // =====================================================
    void deleteByName(Rank name);

    // =====================================================
    // TÌM KIẾM
    // =====================================================
    Optional<Level> findByName(Rank name);

    // Tìm cấp độ theo điểm tích lũy (rank phù hợp nhất)
    Optional<Level> findFirstByMinPointLessThanEqualOrderByMinPointDesc(Integer point);

    // Tìm tất cả cấp độ có điểm tối thiểu trong khoảng
    List<Level> findByMinPointBetween(Integer min, Integer max);

    // =====================================================
    // KIỂM TRA TỒN TẠI
    // =====================================================
    boolean existsByName(Rank name);
    boolean existsByMinPoint(Integer minPoint);

    // =====================================================
    // THỐNG KÊ & ĐẾM
    // =====================================================
    long countByMinPointLessThanEqual(Integer point);

    // =====================================================
    // PHÂN TRANG / SẮP XẾP
    // =====================================================
    Page<Level> findByName(Rank name, Pageable pageable);
    Page<Level> findByMinPointBetween(Integer min, Integer max, Pageable pageable);
}