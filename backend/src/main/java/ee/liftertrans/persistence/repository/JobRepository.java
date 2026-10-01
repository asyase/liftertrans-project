package ee.liftertrans.persistence.repository;

import ee.liftertrans.persistence.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {

    // Juhile määratud tööd valitud staatustega, varaseim töö eespool
    @Query("select j from Job j where j.driver.id = :driverId and j.status in :statuses order by j.plannedStartTime")
    List<Job> findDriverJobsBy(Integer driverId, List<String> statuses);
}

