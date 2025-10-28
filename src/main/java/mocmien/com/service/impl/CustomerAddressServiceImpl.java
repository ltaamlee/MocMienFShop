package mocmien.com.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mocmien.com.entity.CustomerAddress;
import mocmien.com.entity.UserProfile;
import mocmien.com.repository.CustomerAddressRepository;
import mocmien.com.service.CustomerAddressService;

@Service
public class CustomerAddressServiceImpl implements CustomerAddressService {

    private final CustomerAddressRepository addressRepository;

    public CustomerAddressServiceImpl(CustomerAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<CustomerAddress> findByCustomer(UserProfile customer) {
        return addressRepository.findByCustomer(customer);
    }

    @Override
    public CustomerAddress findById(Integer id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ có id = " + id));
    }

    @Override
    public CustomerAddress save(CustomerAddress address) {
        return addressRepository.save(address);
    }

    @Override
    public void delete(Integer id) {
        addressRepository.deleteById(id);
    }

    @Override
    public CustomerAddress getDefaultAddress(UserProfile customer) {
        Optional<CustomerAddress> defaultAddr = addressRepository.findByCustomerAndIsDefaultTrue(customer);
        return defaultAddr.orElse(null);
    }

    @Override
    @Transactional
    public void setDefault(Integer id, UserProfile customer) {
        // 🔹 Bỏ mặc định các địa chỉ cũ
        List<CustomerAddress> addresses = addressRepository.findByCustomer(customer);
        for (CustomerAddress addr : addresses) {
            addr.setIsDefault(false);
        }
        addressRepository.saveAll(addresses);

        // 🔹 Đặt địa chỉ mới làm mặc định
        CustomerAddress newDefault = findById(id);
        newDefault.setIsDefault(true);
        addressRepository.save(newDefault);
    }
}
