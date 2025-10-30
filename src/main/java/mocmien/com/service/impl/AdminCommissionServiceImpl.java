package mocmien.com.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import mocmien.com.dto.request.commission.AdminCommissionCreateRequest;
import mocmien.com.entity.AppCommission;
import mocmien.com.entity.Store;
import mocmien.com.repository.AppCommissionRepository;
import mocmien.com.repository.StoreRepository;
import mocmien.com.service.AdminCommissionService;

@Service
public class AdminCommissionServiceImpl implements AdminCommissionService {

    @Autowired
    private AppCommissionRepository commissionRepo;

    @Autowired
    private StoreRepository storeRepo;

    @Override
    public AppCommission create(AdminCommissionCreateRequest request) {
        Assert.notNull(request.getRatePercent(), "Tỷ lệ chiết khấu không được để trống");

        AppCommission c = new AppCommission();

        if (request.getStoreId() != null) {
            Store s = storeRepo.findById(request.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cửa hàng"));
            c.setStore(s);
        }

        c.setRatePercent(request.getRatePercent());
        c.setIsActive(true);
        c.setNote(request.getNote());

        return commissionRepo.save(c);
    }

    @Override
    public Page<AppCommission> getPage(Pageable pageable) {
        return commissionRepo.findAll(pageable);
    }

    public AppCommission setActive(Integer id, boolean active) {
        AppCommission c = commissionRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cấu hình chiết khấu"));
        c.setIsActive(active);
        return commissionRepo.save(c);
    }
}


