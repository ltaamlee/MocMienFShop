package mocmien.com.controller.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import mocmien.com.service.ProductService;
import mocmien.com.entity.Product;

@Controller
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@GetMapping("/{id}")
	public String productDetail(@PathVariable("id") Integer id, Model model) {
	    Product product = productService.getProductById(id)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + id));
//
//	    List<Product> related = productService.findRelatedProducts(product.getCategory(), id);

	    model.addAttribute("product", product);
//	    model.addAttribute("relatedProducts", related);
	    return "customer/product-detail";
	}


}
