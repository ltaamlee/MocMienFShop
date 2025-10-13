package mocmien.com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocmien.com.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	Optional<Customer> findByUser_Username(String username);
    boolean existsBySdt(String sdt);
}