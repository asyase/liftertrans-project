package ee.liftertrans.persistence.repository;


//Repository = koht, kust Service küsib andmebaasi andmeid.

import ee.liftertrans.persistence.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DriverRepository extends JpaRepository<Driver, Integer> {

    // Kontrollib, kas antud driverId on seotud mõne tööga
    @Query("SELECT COUNT(j) > 0 FROM Job j WHERE j.driver.id = :driverId")
    boolean existsJobsByDriverId(@Param("driverId") Integer driverId);

}
