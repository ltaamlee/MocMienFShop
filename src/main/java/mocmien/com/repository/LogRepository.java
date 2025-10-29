package mocmien.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mocmien.com.entity.Log;

public interface LogRepository extends JpaRepository<Log, Long> {

    // Lấy tất cả thông báo chưa xóa của 1 user
    List<Log> findByReceiverIdAndIsDeletedFalseOrderByTimestampDesc(Integer receiverId);
}
