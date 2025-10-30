package mocmien.com.repository;

import mocmien.com.entity.StoreAddress;
import mocmien.com.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StoreAddressRepository extends JpaRepository<StoreAddress, Integer> {
    Optional<StoreAddress> findByStore(Store store);
}
