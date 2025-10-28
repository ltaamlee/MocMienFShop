package mocmien.com.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Orders;
import mocmien.com.entity.Shipper;
import mocmien.com.entity.Store;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, String> {

}