package mocmien.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Promotion;
import mocmien.com.entity.PromotionDetail;

@Repository
public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Integer> {
	List<PromotionDetail> findByPromotion(Promotion promotion);

	void deleteByPromotion(Promotion promotion);
}
