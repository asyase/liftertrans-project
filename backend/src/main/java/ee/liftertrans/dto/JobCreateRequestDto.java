package ee.liftertrans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data

public class JobCreateRequestDto {

    @NotNull
    private Integer customerId;

    @NotBlank
    private String jobType;

    @NotBlank
    private String executionType;

    private Integer vehicleId;
    private Integer driverId;
    private Integer subcontractorId;

    private String pickupAddress;
    private String deliveryAddress;
    private String serviceAddress;

    private String receiverName;
    private String receiverPhone;

    @NotNull
    private LocalDateTime plannedStartTime;

    private LocalDateTime plannedEndTime;

    private BigDecimal estimatedKm;
    private BigDecimal estimatedHours;

    private String notes;
}