package mocmien.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mocmien.com.entity.AppCommission;

public interface AppCommissionRepository extends JpaRepository<AppCommission, Integer> {

    @Query("SELECT c FROM AppCommission c WHERE c.isActive = true AND c.store.id = :storeId ORDER BY c.createdAt DESC")
    List<AppCommission> findActiveByStore(@Param("storeId") Integer storeId);

    @Query("SELECT c FROM AppCommission c WHERE c.isActive = true AND c.store IS NULL ORDER BY c.createdAt DESC")
    List<AppCommission> findActiveGlobal();
}



