package mocmien.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mocmien.com.entity.OrderDetail;
import mocmien.com.entity.Orders;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

	List<OrderDetail> findByOrder(Orders order);
}