package ee.liftertrans.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "job")
@Data
@NoArgsConstructor
public class Job {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "subcontractor_id")
    private Subcontractor subcontractor;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "execution_type")
    private String executionType;

    @Column(name = "pickup_address")
    private String pickupAddress;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "service_address")
    private String serviceAddress;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(name = "planned_start_time")
    private LocalDateTime plannedStartTime;

    @Column(name = "planned_end_time")
    private LocalDateTime plannedEndTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_finish_time")
    private LocalDateTime actualFinishTime;

    @Column(name = "estimated_km")
    private BigDecimal estimatedKm;

    @Column(name = "actual_km")
    private BigDecimal actualKm;

    @Column(name = "estimated_hours")
    private BigDecimal estimatedHours;

    @Column(name = "actual_hours")
    private BigDecimal actualHours;

    private String status;
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
