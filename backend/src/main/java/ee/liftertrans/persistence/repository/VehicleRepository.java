package ee.liftertrans.persistence.repository;

import ee.liftertrans.persistence.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

}
