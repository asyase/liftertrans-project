package ee.liftertrans.persistence.repository;

import ee.liftertrans.persistence.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Integer> {
}
