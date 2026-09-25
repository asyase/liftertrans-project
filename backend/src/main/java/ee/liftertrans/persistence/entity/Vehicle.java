package ee.liftertrans.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "vehicle", schema = "liftertrans_project")
public class Vehicle {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 20)
    @NotNull
    @Column(name = "registration_number", nullable = false, length = 20)
    private String registrationNumber;
    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;
    @Column(name = "max_cargo_weight_kg", precision = 10, scale = 2)
    private BigDecimal maxCargoWeightKg;
    @Column(name = "crane_capacity_kg", precision = 10, scale = 2)
    private BigDecimal craneCapacityKg;
    @Column(name = "crane_reach_m", precision = 8, scale = 2)
    private BigDecimal craneReachM;
    @Column(name = "platform_length_m", precision = 8, scale = 2)
    private BigDecimal platformLengthM;
    @Column(name = "platform_width_m", precision = 8, scale = 2)
    private BigDecimal platformWidthM;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcontractor_id")
    private Subcontractor subcontractor;
    @Size(max = 20)
    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    @Column(name = "vehicle_length_m", precision = 8, scale = 2)
    private BigDecimal vehicleLengthM;
    @Column(name = "vehicle_width_m", precision = 8, scale = 2)
    private BigDecimal vehicleWidthM;
    @Column(name = "vehicle_height_m", precision = 8, scale = 2)
    private BigDecimal vehicleHeightM;

}
