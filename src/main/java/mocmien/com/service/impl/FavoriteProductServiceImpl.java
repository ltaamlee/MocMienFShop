package mocmien.com.service.impl;

import lombok.RequiredArgsConstructor;
import mocmien.com.entity.FavoriteProduct;
import mocmien.com.entity.Product;
import mocmien.com.entity.User;
import mocmien.com.repository.FavoriteProductRepository;
import mocmien.com.service.FavoriteProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteProductServiceImpl implements FavoriteProductService {
    
	@Autowired private FavoriteProductRepository favoriteProductRepository;

    @Override
    public void addFavorite(User user, Product product) {
        if(!favoriteProductRepository.existsByUserAndProduct(user, product)) {
            favoriteProductRepository.save(new FavoriteProduct(user, product));
        }
    }

    @Override
    public void removeFavorite(User user, Product product) {
        favoriteProductRepository.deleteByUserAndProduct(user, product);
    }

    @Override
    public boolean isFavorite(User user, Product product) {
        return favoriteProductRepository.existsByUserAndProduct(user, product);
    }

    @Override
    public List<Product> getFavoriteProducts(User user) {
        return favoriteProductRepository.findAllByUser(user).stream().map(FavoriteProduct::getProduct).collect(Collectors.toList());
    }
}
