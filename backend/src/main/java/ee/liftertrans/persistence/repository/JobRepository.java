package ee.liftertrans.persistence.repository;

import ee.liftertrans.persistence.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {

    // Juhile määratud tööd valitud staatustega, varaseim töö eespool
    @Query("select j from Job j where j.driver.id = :driverId and j.status in :statuses order by j.plannedStartTime")
    List<Job> findDriverJobsBy(Integer driverId, List<String> statuses);

    // Tööde nimekiri filtritega — null väärtusega filtrit ei arvestata
    @Query("""
            select j from Job j
            where (cast(:dayStart as Instant) is null or j.plannedStartTime >= :dayStart)
              and (cast(:dayEnd as Instant) is null or j.plannedStartTime < :dayEnd)
              and (:status is null or j.status = :status)
              and (:driverId is null or j.driver.id = :driverId)
              and (:vehicleId is null or j.vehicle.id = :vehicleId)
            order by j.id
            """)
    List<Job> findFilteredJobsBy(Instant dayStart, Instant dayEnd, String status, Integer driverId, Integer vehicleId);
}

