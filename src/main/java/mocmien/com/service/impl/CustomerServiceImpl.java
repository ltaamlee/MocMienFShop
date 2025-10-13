package mocmien.com.service.impl;

import mocmien.com.entity.Customer;
import mocmien.com.enums.CustomerRank;
import mocmien.com.model.CustomerRowVM;
import mocmien.com.model.CustomerStatsVM;
import mocmien.com.repository.CustomerRepository;
import mocmien.com.repository.OrderRepository;
import mocmien.com.repository.UserRepository;
import mocmien.com.service.CustomerService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {
	
	private final CustomerRepository customerRepo;
	private final OrderRepository orderRepo;
	private final UserRepository userRepo;

	@Override
	public Optional<Customer> findByUser_Username(String username) {
		return customerRepo.findByUser_Username(username);
	}

	@Override
	public void updateAvatar(String username, String imageUrl) {
		userRepo.findByUsername(username).ifPresent(user -> {
			user.setImageUrl(imageUrl);
			userRepo.save(user);
		});
	}

	public CustomerServiceImpl(CustomerRepository customerRepo, OrderRepository orderRepo, UserRepository userRepo) {
		this.customerRepo = customerRepo;
		this.orderRepo = orderRepo;
		this.userRepo = userRepo;
	}

	@Override
	public List<CustomerRowVM> list(CustomerRank filterRank, Month filterMonth, Integer year) {
		List<Customer> customers = customerRepo.findAll();

		return customers.stream().map(c -> {
			String email = (c.getUser() != null) ? c.getUser().getEmail() : null;
			long total = orderRepo.countByKhachHang_MaKH(c.getMaKH());
			LocalDateTime firstDT = orderRepo.firstOrderDateTimeOfCustomer(c.getMaKH());
			LocalDate firstDate = (firstDT == null ? null : firstDT.toLocalDate());
			return CustomerRowVM.from(c, email, total, firstDate);
		}).filter(vm -> {
			if (filterMonth == null && year == null)
				return true;
			LocalDate d = vm.ngayTao(); // ngày đơn đầu tiên (đã map ở trên)
			if (d == null)
				return false;
			boolean ok = true;
			if (year != null)
				ok &= (d.getYear() == year);
			if (filterMonth != null)
				ok &= (d.getMonth() == filterMonth);
			return ok;
		}).toList();
	}

	@Override
	public List<CustomerRowVM> listWithSearch(String q, Month filterMonth, Integer year) {
		String kw = (q == null) ? "" : q.trim().toLowerCase();
		List<CustomerRowVM> base = list(null, filterMonth, year); // tái dùng hàm cũ (đã có lọc tháng/năm)
		if (kw.isEmpty())
			return base;

		return base.stream()
				.filter(r -> (r.hoTen() != null && r.hoTen().toLowerCase().contains(kw))
						|| (r.sdt() != null && r.sdt().toLowerCase().contains(kw))
						|| (r.email() != null && r.email().toLowerCase().contains(kw)))
				.toList();
	}

	@Override
	public CustomerRowVM oneRow(Integer id) {
		Customer c = customerRepo.findById(id).orElseThrow();

		String email = (c.getUser() != null) ? c.getUser().getEmail() : null;
		long total = orderRepo.countByKhachHang_MaKH(c.getMaKH());
		LocalDateTime firstDT = orderRepo.firstOrderDateTimeOfCustomer(c.getMaKH());

		return CustomerRowVM.from(c, email, total, (firstDT == null ? null : firstDT.toLocalDate()));
	}

	@Override
	public Optional<Customer> findById(Integer id) {
		return customerRepo.findById(id);
	}

	@Override
	public Customer save(Customer c) {
		return customerRepo.save(c);
	}

	@Override
	public CustomerStatsVM stats(Integer year, Month month) {
		long tongKhachHang = customerRepo.count();
		long tongTheoLoc = list(null, month, year).size();
		return new CustomerStatsVM(tongKhachHang, tongTheoLoc);
	}

}

