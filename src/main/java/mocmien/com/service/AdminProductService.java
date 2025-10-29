package mocmien.com.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mocmien.com.dto.response.product.ProductListItemResponse;
import mocmien.com.enums.ProductStatus;

public interface AdminProductService {
	Page<ProductListItemResponse> listAdmin(String keyword, Integer categoryId, Integer storeId, ProductStatus status,
			Pageable pageable);
}
