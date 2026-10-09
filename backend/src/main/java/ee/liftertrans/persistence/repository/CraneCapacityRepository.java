package ee.liftertrans.persistence.repository;

import ee.liftertrans.persistence.entity.CraneCapacity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Repository töötab CraneCapacity tabeliga
public interface CraneCapacityRepository extends JpaRepository<CraneCapacity, Integer> {
    //kraana mõõtepunktide leidmine sõiduki Id alusel
    List<CraneCapacity> findCraneCapacitiesByVehicle_Id(Integer id);


}
