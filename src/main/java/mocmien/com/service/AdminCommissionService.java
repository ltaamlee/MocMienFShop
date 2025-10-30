package mocmien.com.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mocmien.com.dto.request.commission.AdminCommissionCreateRequest;
import mocmien.com.entity.AppCommission;

public interface AdminCommissionService {
    AppCommission create(AdminCommissionCreateRequest request);
    Page<AppCommission> getPage(Pageable pageable);
    AppCommission setActive(Integer id, boolean active);
}



