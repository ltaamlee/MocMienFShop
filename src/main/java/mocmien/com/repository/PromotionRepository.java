package mocmien.com.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mocmien.com.entity.Promotion;
import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.enums.PromotionType;

public interface PromotionRepository extends JpaRepository<Promotion, Integer>{
	// -----------------------
    // CRUD cơ bản
    // -----------------------
}
