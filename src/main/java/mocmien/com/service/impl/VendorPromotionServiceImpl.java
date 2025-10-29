package mocmien.com.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mocmien.com.dto.request.promotion.VendorPromotionCreateRequest;
import mocmien.com.dto.request.promotion.VendorPromotionUpdateRequest;
import mocmien.com.dto.response.promotion.VendorPromotionDetailResponse;
import mocmien.com.dto.response.promotion.VendorPromotionListItemResponse;
import mocmien.com.dto.response.promotion.VendorPromotionStatsResponse;
import mocmien.com.entity.Product;
import mocmien.com.entity.Promotion;
import mocmien.com.entity.PromotionDetail;
import mocmien.com.entity.Store;
import mocmien.com.enums.PromotionStatus;
import mocmien.com.enums.PromotionType;
import mocmien.com.repository.ProductRepository;
import mocmien.com.repository.PromotionDetailRepository;
import mocmien.com.repository.PromotionRepository;
import mocmien.com.repository.PromotionRepository.IdName;
import mocmien.com.repository.StoreRepository;
import mocmien.com.service.VendorPromotionService;

@Service
@Transactional
public class VendorPromotionServiceImpl implements VendorPromotionService {

	private final StoreRepository storeRepo;
	private final PromotionRepository promoRepo;
	private final PromotionDetailRepository detailRepo;
	private final ProductRepository productRepo;

	@Autowired
	public VendorPromotionServiceImpl(StoreRepository storeRepo, PromotionRepository promoRepo,
			PromotionDetailRepository detailRepo, ProductRepository productRepo) {
		this.storeRepo = storeRepo;
		this.promoRepo = promoRepo;
		this.detailRepo = detailRepo;
		this.productRepo = productRepo;
	}

	private Store requireStoreOfVendor(Integer vendorUserId) {
		return storeRepo.findByVendorUserId(vendorUserId)
				.orElseThrow(() -> new IllegalStateException("Vendor chưa có Store"));
	}

	private void autoExpireIfNeeded(Promotion p) {
		if (p.isExpiredNow() && p.getStatus() != PromotionStatus.EXPIRED) {
			p.setStatus(PromotionStatus.EXPIRED);
		}
	}

	@Override
	public Page<VendorPromotionListItemResponse> list(Integer vendorUserId, String keyword, PromotionStatus status,
			PromotionType type, Pageable pageable) {
		Store store = requireStoreOfVendor(vendorUserId);
		if (keyword == null)
			keyword = "";

		Page<Promotion> page;
		if (status != null && type != null) {
			page = promoRepo.findByStoreAndStatusAndTypeAndNameContainingIgnoreCase(store, status, type, keyword,
					pageable);
		} else if (status != null) {
			page = promoRepo.findByStoreAndStatusAndNameContainingIgnoreCase(store, status, keyword, pageable);
		} else if (type != null) {
			page = promoRepo.findByStoreAndTypeAndNameContainingIgnoreCase(store, type, keyword, pageable);
		} else {
			page = promoRepo.findByStoreAndNameContainingIgnoreCase(store, keyword, pageable);
		}

		page.forEach(this::autoExpireIfNeeded);

		return page.map(p -> {
			VendorPromotionListItemResponse r = new VendorPromotionListItemResponse();
			r.setId(p.getId());
			r.setName(p.getName());
			r.setType(p.getType());
			r.setValue(p.getValue());
			r.setStartDate(p.getStartDate());
			r.setEndDate(p.getEndDate());
			r.setStatus(p.getStatus());
			return r;
		});
	}

	@Override
	public VendorPromotionStatsResponse stats(Integer vendorUserId) {
		Store store = requireStoreOfVendor(vendorUserId);
		long total = promoRepo.countByStore(store);
		long active = promoRepo.countByStoreAndStatus(store, PromotionStatus.ACTIVE);
		long inactive = promoRepo.countByStoreAndStatus(store, PromotionStatus.INACTIVE);
		long expired = promoRepo.countByStoreAndStatus(store, PromotionStatus.EXPIRED);
		long expSoon = promoRepo.countExpiringSoon(store.getId());

		VendorPromotionStatsResponse res = new VendorPromotionStatsResponse();
		res.setTotalPromotions(total);
		res.setInactivePromotions(inactive);
		res.setActivePromotions(active);
		res.setExpiringSoonPromotions(expSoon);
		res.setExpiredPromotions(expired);
		return res;
	}

	@Override
	public VendorPromotionDetailResponse detail(Integer vendorUserId, Integer id) {
		Store store = requireStoreOfVendor(vendorUserId);
		Promotion p = promoRepo.findByIdAndStore(id, store)
				.orElseThrow(() -> new NoSuchElementException("Promotion không tồn tại"));

		autoExpireIfNeeded(p);

		List<PromotionDetail> details = detailRepo.findByPromotion(p);
		List<Integer> productIds = new ArrayList<>();
		List<String> productNames = new ArrayList<>();
		for (PromotionDetail d : details) {
			if (d.getProduct() != null) {
				productIds.add(d.getProduct().getId());
				productNames.add(d.getProduct().getProductName());
			}
		}

		VendorPromotionDetailResponse res = new VendorPromotionDetailResponse();
		res.setId(p.getId());
		res.setName(p.getName());
		// Nếu có field description riêng thì đổi lại field phù hợp:
		res.setDescription(p.getBanner()); // tạm dùng banner
		res.setType(p.getType());
		res.setValue(p.getValue());
		res.setStartDate(p.getStartDate());
		res.setEndDate(p.getEndDate());
		res.setStatus(p.getStatus());
		res.setProductIds(productIds);
		res.setProductNames(productNames);
		return res;
	}

	@Override
	public Integer create(Integer vendorUserId, VendorPromotionCreateRequest req) {
		Store store = requireStoreOfVendor(vendorUserId);
		if (req.getEndDate().isBefore(req.getStartDate()))
			throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu");

		Promotion p = new Promotion();
		p.setStore(store);
		p.setName(req.getName());
		p.setType(req.getType());

		// ✅ chỉ lưu value cho PERCENT/AMOUNT
		if (req.getType() == PromotionType.PERCENT || req.getType() == PromotionType.AMOUNT) {
			p.setValue(req.getValue());
		} else {
			p.setValue(null);
		}

		p.setStartDate(req.getStartDate());
		p.setEndDate(req.getEndDate());
		p.setIsActive(true);
		p.setStatus(
				req.getStartDate().isAfter(LocalDateTime.now()) ? PromotionStatus.SCHEDULED : PromotionStatus.INACTIVE);

		Promotion saved = promoRepo.save(p);

		// details
		if (req.getProductIds() != null && !req.getProductIds().isEmpty()) {
		    List<Product> products =
		        productRepo.findByStore_IdAndIdIn(store.getId(), req.getProductIds());
		    for (Product prod : products) {
		        PromotionDetail d = new PromotionDetail();
		        d.setPromotion(saved);
		        d.setProduct(prod);
		        d.setType(p.getType());
		        d.setValue(p.getValue()); // null nếu FREESHIP/GIFT
		        d.setIsActive(true);
		        detailRepo.save(d);
		    }
		}
		return saved.getId();
	}

	@Override
	public VendorPromotionDetailResponse update(Integer vendorUserId, Integer id, VendorPromotionUpdateRequest req) {
		Store store = requireStoreOfVendor(vendorUserId);
		Promotion p = promoRepo.findByIdAndStore(id, store)
				.orElseThrow(() -> new NoSuchElementException("Promotion không tồn tại"));

		if (req.getEndDate().isBefore(req.getStartDate()))
			throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu");

		p.setName(req.getName());
		p.setType(req.getType());
		if (req.getType() == PromotionType.PERCENT || req.getType() == PromotionType.AMOUNT) {
			p.setValue(req.getValue());
		} else {
			p.setValue(null);
		}
		p.setStartDate(req.getStartDate());
		p.setEndDate(req.getEndDate());

		if (p.isExpiredNow())
			p.setStatus(PromotionStatus.EXPIRED);
		else if (p.getStartDate().isAfter(LocalDateTime.now()))
			p.setStatus(PromotionStatus.SCHEDULED);

		// làm lại details
		detailRepo.deleteByPromotion(p);
		if (req.getProductIds() != null && !req.getProductIds().isEmpty()) {
		    List<Product> products =
		        productRepo.findByStore_IdAndIdIn(store.getId(), req.getProductIds());
		    for (Product prod : products) {
		        PromotionDetail d = new PromotionDetail();
		        d.setPromotion(p);
		        d.setProduct(prod);
		        d.setType(p.getType());
		        d.setValue(p.getValue());
		        d.setIsActive(true);
		        detailRepo.save(d);
		    }
		}
		return detail(vendorUserId, p.getId());
	}

	@Override
	public void updateStatus(Integer vendorUserId, Integer id, PromotionStatus status) {
		Store store = requireStoreOfVendor(vendorUserId);
		Promotion p = promoRepo.findByIdAndStore(id, store)
				.orElseThrow(() -> new NoSuchElementException("Promotion không tồn tại"));

		if (p.isExpiredNow()) {
			p.setStatus(PromotionStatus.EXPIRED);
		} else {
			p.setStatus(status);
		}
		promoRepo.save(p);
	}

	@Override
	public void delete(Integer vendorUserId, Integer id) {
		Store store = requireStoreOfVendor(vendorUserId);
		Promotion p = promoRepo.findByIdAndStore(id, store)
				.orElseThrow(() -> new NoSuchElementException("Promotion không tồn tại"));
		detailRepo.deleteByPromotion(p);
		promoRepo.delete(p);
	}

	@Override
	public List<PromotionRepository.IdName> productOptions(Integer vendorUserId) {
		Store store = requireStoreOfVendor(vendorUserId);
		return promoRepo.findOptionsByStoreId(store.getId());
	}

}
