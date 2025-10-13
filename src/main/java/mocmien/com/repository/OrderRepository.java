package mocmien.com.repository;

import mocmien.com.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

	
	// Tổng số đơn của một khách hàng
    long countByKhachHang_MaKH(Integer maKH);

    // Thời điểm đơn HÀNG ĐẦU TIÊN của KH (coi là "ngày tạo" KH)
    @Query("select min(o.ngayDat) from Order o where o.khachHang.maKH = :maKH")
    LocalDateTime firstOrderDateTimeOfCustomer(@Param("maKH") Integer maKH);

    // (Phục vụ thống kê) — số KH mới theo từng tháng trong một năm
    // KH mới = KH có đơn đầu tiên nằm trong tháng đó
    @Query("""
        select month(min(o.ngayDat)) as m, count(distinct o.khachHang.maKH) as cnt
        from Order o
        where year(o.ngayDat) = :year
        group by o.khachHang.maKH
    """)
    List<Object[]> firstOrderMonthPerCustomer(@Param("year") int year);
}
