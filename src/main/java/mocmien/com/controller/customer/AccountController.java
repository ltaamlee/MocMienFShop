package mocmien.com.controller.customer;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import mocmien.com.entity.Customer;
import mocmien.com.service.CustomerService;

@Controller
public class AccountController {

	@Autowired
	private CustomerService customerService;

	@GetMapping("/account")
	public String showAccountPage(Model model, Principal principal) {
		if (principal == null) {
			return "redirect:/login";
		}

		String username = principal.getName();
		Optional<Customer> customerOpt = customerService.findByUser_Username(username);

		if (customerOpt.isEmpty()) {
			return "redirect:/login";
		}

		Customer customer = customerOpt.get();
		model.addAttribute("customer", customer);
		model.addAttribute("user", customer.getUser());

		return "customer/account";
	}

	@GetMapping("/account/avatar")
	public String accountAvatar(Model model, Principal principal) {
		if (principal == null) {
			return "redirect:/login";
		}

		String username = principal.getName();
		Customer customer = customerService.findByUser_Username(username)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

		model.addAttribute("customer", customer);
		model.addAttribute("user", customer.getUser());
		model.addAttribute("title", "Ảnh đại diện");
		return "customer/avatar";
	}

	// Xử lý upload ảnh đại diện
	@PostMapping("/account/avatar/upload")
	public String uploadAvatar(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
		if (principal == null || file.isEmpty()) {
			return "redirect:/account";
		}

		String username = principal.getName();

		String uploadDir = "src/main/resources/static/uploads/avatar/";
		File directory = new File(uploadDir);
		if (!directory.exists())
			directory.mkdirs();

		// tên file duy nhất
		String fileName = username + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
		Path filePath = Paths.get(uploadDir, fileName);
		Files.write(filePath, file.getBytes());

		// cập nhật DB
		String imageUrl = "/uploads/avatar/" + fileName;
		customerService.updateAvatar(username, imageUrl);

		return "redirect:/account";
	}

	@PostMapping("/account/update")
	@ResponseBody
	public ResponseEntity<?> updateCustomerInfo(@ModelAttribute Customer formCustomer,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate ngaySinh,
			Principal principal) {

		try {
			if (principal == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
			}

			String username = principal.getName();
			Customer customer = customerService.findByUser_Username(username)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

			// Cập nhật dữ liệu
			customer.setHoTen(formCustomer.getHoTen());
			customer.setSdt(formCustomer.getSdt());
			customer.setDiaChi(formCustomer.getDiaChi());
			if (ngaySinh != null) {
				customer.setNgaySinh(ngaySinh);
			}

			// Lưu lại vào DB
			customerService.save(customer);

			// ✅ Trả JSON để JS cập nhật lại UI
			Map<String, Object> response = new HashMap<>();
			response.put("hoTen", customer.getHoTen());
			response.put("sdt", customer.getSdt());
			response.put("diaChi", customer.getDiaChi());
			response.put("ngaySinh", customer.getNgaySinh());
			response.put("email", customer.getUser().getEmail()); // 👈 thêm thẳng email ra ngoài

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Đã xảy ra lỗi khi lưu thông tin: " + e.getMessage());
		}
	}
}


