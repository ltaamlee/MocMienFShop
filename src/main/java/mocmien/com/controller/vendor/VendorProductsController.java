package mocmien.com.controller.vendor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendor/products")
@PreAuthorize("hasRole('VENDOR')")
public class VendorProductsController {

}
