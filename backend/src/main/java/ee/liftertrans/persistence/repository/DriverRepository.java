package ee.liftertrans.persistence.repository;


//Repository = koht, kust Service küsib andmebaasi andmeid.

import org.springframework.data.jpa.repository.JpaRepository;

import ee.liftertrans.persistence.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, Integer> {

}
