package lk.samarasinghe_super.asset.customer.dao;

import lk.samarasingher_super.asset.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerDao extends JpaRepository<Customer, Integer> {
    Customer findFirstByOrderByIdDesc();
}
