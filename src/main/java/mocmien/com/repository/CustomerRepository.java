package mocmien.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mocmien.com.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{

}
