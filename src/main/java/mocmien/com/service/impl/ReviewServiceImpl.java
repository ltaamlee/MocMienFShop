package mocmien.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mocmien.com.entity.Product;
import mocmien.com.entity.Review;
import mocmien.com.entity.User;
import mocmien.com.repository.ProductRepository;
import mocmien.com.repository.ReviewRepository;
import mocmien.com.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void addReview(Product product, User user, int rating, String comment) {
        if (reviewRepository.existsByProductAndUser(product, user)) return;

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);

        reviewRepository.save(review);
    }
    
    @Override
    public List<Review> getReviewsByProduct(Product product) {
        return reviewRepository.findByProductOrderByCreateAtDesc(product);
    }
    
    @Override
    public List<Review> getReviewsByProductId(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        return reviewRepository.findByProductOrderByCreateAtDesc(product);
    }


}
