package ee.liftertrans.persistence.repository;

import ee.liftertrans.persistence.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    List<Vehicle> findVehiclesByStatus(String status);

}
