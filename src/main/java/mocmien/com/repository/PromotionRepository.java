package mocmien.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Product;
import mocmien.com.entity.Promotion;
import mocmien.com.entity.Store;
import mocmien.com.enums.PromotionStatus;
import mocmien.com.enums.PromotionType;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

	@EntityGraph(attributePaths = { "store", "user" })
	Page<Promotion> findByStoreAndNameContainingIgnoreCase(Store store, String keyword, Pageable pageable);

	@EntityGraph(attributePaths = { "store", "user" })
	Page<Promotion> findByStoreAndStatusAndNameContainingIgnoreCase(Store store, PromotionStatus status, String keyword,
			Pageable pageable);

	@EntityGraph(attributePaths = { "store", "user" })
	Page<Promotion> findByStoreAndTypeAndNameContainingIgnoreCase(Store store, PromotionType type, String keyword,
			Pageable pageable);

	@EntityGraph(attributePaths = { "store", "user" })
	Page<Promotion> findByStoreAndStatusAndTypeAndNameContainingIgnoreCase(Store store, PromotionStatus status,
			PromotionType type, String keyword, Pageable pageable);

	long countByStore(Store store);

	long countByStoreAndStatus(Store store, PromotionStatus status);

	// Đếm KM sắp hết hạn trong 3 ngày tới 
	@Query(value = """
			    SELECT COUNT(*)
			    FROM promotion p
			    WHERE p.store_id = :storeId
			      AND p.end_date IS NOT NULL
			      AND p.end_date BETWEEN CURRENT_TIMESTAMP
			                        AND DATEADD(DAY, 3, CURRENT_TIMESTAMP)
			""", nativeQuery = true)
	long countExpiringSoon(@Param("storeId") Integer storeId);

	@EntityGraph(attributePaths = { "store", "user" })
	Optional<Promotion> findByIdAndStore(Integer id, Store store);
	
	
	
	
	 interface IdName {
	        Integer getId();
	        String getProductName();
	    }


	    @Query("""
	        select p.id as id, p.productName as productName
	        from Product p
	        where p.store.id = :storeId
	          and (p.isActive = true or p.isActive is null)
	    """)
	    List<IdName> findOptionsByStoreId(@Param("storeId") Integer storeId);


	    List<Product> findByIdInAndStoreId(List<Integer> ids, Integer storeId);
}
