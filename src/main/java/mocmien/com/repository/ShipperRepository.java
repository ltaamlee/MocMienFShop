package mocmien.com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Shipper;

@Repository
public interface ShipperRepository extends JpaRepository<Shipper, Integer> {
    Optional<Shipper> findByUser_UserId(Integer userId);
}



