package ee.liftertrans.persistence.repository;

import ee.liftertrans.persistence.entity.Subcontractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubcontractorRepository extends JpaRepository<Subcontractor, Integer> {

    // Tellimusele saab valida ainult aktiivse alltöövõtja
    @Query("select s from Subcontractor s where s.active = true order by s.companyName")
    List<Subcontractor> findActiveSubcontractors();
}
