package mocmien.com.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mocmien.com.dto.request.delivery.DeliveryRequest;
import mocmien.com.dto.response.admin.DeliveryResponse;
import mocmien.com.dto.response.admin.DeliveryStats;
import mocmien.com.entity.Delivery;

public interface AdminDeliveryService {
    DeliveryStats getDeliveryStatistics();

    Page<DeliveryResponse> findAllDeliveries(Pageable pageable);

    Delivery findById(Integer id);

    Delivery save(Delivery delivery);

    Delivery update(Integer id, Delivery deliveryDetails);

    void delete(Integer id);
    
    DeliveryResponse createDelivery(DeliveryRequest request);
    
    DeliveryResponse findDeliveryResponseById(Integer id);

    DeliveryResponse updateDelivery(Integer id, DeliveryRequest request);

}
