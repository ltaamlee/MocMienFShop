package mocmien.com.service;

import java.util.List;

import mocmien.com.entity.Review;
import mocmien.com.entity.Product;
import mocmien.com.entity.User;

public interface ReviewService {
	void addReview(Product product, User user, int rating, String comment);

	List<Review> getReviewsByProduct(Product product);
	
	List<Review> getReviewsByProductId(Integer productId);

    Double getAverageRatingOfProduct(Product product);
    Double getAverageRatingOfShop(Integer storeId);
}
