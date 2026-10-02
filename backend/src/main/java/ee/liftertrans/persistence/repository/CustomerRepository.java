package ee.liftertrans.persistence.repository;

import ee.liftertrans.persistence.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT c FROM Customer c WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Customer> findBySearchTerm(@Param("search") String search);

    // Kontrollib, kas antud customerId on seotud mõne tööga
    @Query("SELECT COUNT(j) > 0 FROM Job j WHERE j.customer.id = :customerId")
    boolean existsJobsByCustomerId(@Param("customerId") Integer customerId);
}
