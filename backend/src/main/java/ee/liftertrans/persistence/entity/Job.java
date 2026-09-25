package ee.liftertrans.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "job", schema = "liftertrans_project")
public class Job {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcontractor_id")
    private Subcontractor subcontractor;
    @Size(max = 30)
    @NotNull
    @Column(name = "job_type", nullable = false, length = 30)
    private String jobType;
    @Size(max = 20)
    @NotNull
    @Column(name = "execution_type", nullable = false, length = 20)
    private String executionType;
    @Size(max = 255)
    @Column(name = "pickup_address")
    private String pickupAddress;
    @Size(max = 255)
    @Column(name = "delivery_address")
    private String deliveryAddress;
    @Size(max = 255)
    @Column(name = "service_address")
    private String serviceAddress;
    @Size(max = 150)
    @Column(name = "receiver_name", length = 150)
    private String receiverName;
    @Size(max = 30)
    @Column(name = "receiver_phone", length = 30)
    private String receiverPhone;
    @NotNull
    @Column(name = "planned_start_time", nullable = false)
    private Instant plannedStartTime;
    @Column(name = "planned_end_time")
    private Instant plannedEndTime;
    @Column(name = "actual_start_time")
    private Instant actualStartTime;
    @Column(name = "actual_finish_time")
    private Instant actualFinishTime;
    @Column(name = "estimated_km", precision = 10, scale = 2)
    private BigDecimal estimatedKm;
    @Column(name = "actual_km", precision = 10, scale = 2)
    private BigDecimal actualKm;
    @Column(name = "estimated_hours", precision = 6, scale = 2)
    private BigDecimal estimatedHours;
    @Column(name = "actual_hours", precision = 6, scale = 2)
    private BigDecimal actualHours;
    @Size(max = 20)
    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

}
