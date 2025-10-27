package mocmien.com.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Level;
import mocmien.com.entity.Product;
import mocmien.com.entity.Promotion;
import mocmien.com.entity.PromotionDetail;
import mocmien.com.enums.PromotionType;
import mocmien.com.enums.Rank;

@Repository
public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Integer> {

    // -----------------------
    // CRUD cơ bản
    // -----------------------

}