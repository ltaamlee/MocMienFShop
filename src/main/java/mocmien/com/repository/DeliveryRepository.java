package mocmien.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    List<Delivery> findByIsActiveTrue();

    long countByIsActive(boolean isActive);
    
    /**
     * Tìm delivery đầu tiên phù hợp với khoảng cách (active và maxDistance >= distanceKm)
     * Sắp xếp theo basePrice tăng dần (ưu tiên delivery rẻ nhất)
     */
    @Query("SELECT d FROM Delivery d WHERE d.isActive = true AND d.maxDistance >= :distanceKm ORDER BY d.basePrice ASC")
    Optional<Delivery> findFirstAvailableForDistance(@Param("distanceKm") Integer distanceKm);
    
    /**
     * Lấy tất cả delivery active phù hợp với khoảng cách
     */
    @Query("SELECT d FROM Delivery d WHERE d.isActive = true AND d.maxDistance >= :distanceKm ORDER BY d.basePrice ASC")
    List<Delivery> findAllAvailableForDistance(@Param("distanceKm") Integer distanceKm);
}
