package mocmien.com.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Promotion;
import mocmien.com.enums.PromotionStatus;

@Repository
public interface AdminPromotionRepository extends JpaRepository<Promotion, Integer>, JpaSpecificationExecutor<Promotion> {

	@Query("SELECT COUNT(p) FROM Promotion p WHERE p.startDate <= :now AND p.endDate >= :now")
    long countActive(LocalDateTime now);

    @Query("SELECT COUNT(p) FROM Promotion p WHERE p.startDate > :now")
    long countUpcoming(LocalDateTime now);

    @Query("SELECT COUNT(p) FROM Promotion p WHERE p.endDate < :now")
    long countExpired(LocalDateTime now);

    @Query("SELECT COUNT(p) FROM Promotion p WHERE p.endDate BETWEEN :now AND :soon")
    long countExpiring(LocalDateTime now, LocalDateTime soon);

	boolean existsByName(String name);

	long countByStatus(PromotionStatus active);
	
}
