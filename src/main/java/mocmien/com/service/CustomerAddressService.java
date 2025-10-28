package mocmien.com.service;

import java.util.List;

import mocmien.com.entity.CustomerAddress;
import mocmien.com.entity.UserProfile;

public interface CustomerAddressService {

    List<CustomerAddress> findByCustomer(UserProfile customer);

    CustomerAddress findById(Integer id);

    CustomerAddress save(CustomerAddress address);

    void delete(Integer id);

    CustomerAddress getDefaultAddress(UserProfile customer);

    void setDefault(Integer id, UserProfile customer);
}
