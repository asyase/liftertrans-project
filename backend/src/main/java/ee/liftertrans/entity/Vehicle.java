package ee.liftertrans.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicle")
@Data
@NoArgsConstructor
public class Vehicle {

    @Id
    private Integer id;

    @Column(name = "registration_number")
    private String registrationNumber;

    private String name;

    @Column(name = "max_cargo_weight_kg")
    private BigDecimal maxCargoWeightKg;

    @Column(name = "crane_capacity_kg")
    private BigDecimal craneCapacityKg;

    @Column(name = "crane_reach_m")
    private BigDecimal craneReachM;

    @Column(name = "platform_length_m")
    private BigDecimal platformLengthM;

    @Column(name = "platform_width_m")
    private BigDecimal platformWidthM;

    @ManyToOne
    @JoinColumn(name = "subcontractor_id")
    private Subcontractor subcontractor;

    private String status;

    @Column(name = "vehicle_length_m")
    private BigDecimal vehicleLengthM;

    @Column(name = "vehicle_width_m")
    private BigDecimal vehicleWidthM;

    @Column(name = "vehicle_height_m")
    private BigDecimal vehicleHeightM;
}
