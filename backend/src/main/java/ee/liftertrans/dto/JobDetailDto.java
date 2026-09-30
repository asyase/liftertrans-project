package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

// Ühe töö andmed detailvaate jaoks (koos kliendi, auto, juhi ja alltöövõtja nimedega)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDetailDto {
    // Töö
    private Integer jobId;
    private String status;
    private String jobType;
    private String executionType;

    // Klient
    private String customerName;
    private String customerCompanyName;
    private String customerPhone;
    private String customerEmail;

    // Auto, juht või alltöövõtja (SUBCONTRACTED puhul juhti pole)
    private Integer vehicleId;
    private String vehicleRegistrationNumber;
    private String vehicleName;
    private String driverName;
    private String subcontractorName;

    // Aadressid (CRANE_ONLY puhul ainult serviceAddress)
    private String pickupAddress;
    private String deliveryAddress;
    private String serviceAddress;
    private String receiverName;
    private String receiverPhone;

    // Planeeritud ja tegelik (actual* on täidetud alles lõpetatud tööl)
    private Instant plannedStartTime;
    private Instant plannedEndTime;
    private Instant actualStartTime;
    private Instant actualFinishTime;
    private BigDecimal estimatedKm;
    private BigDecimal actualKm;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;

    private String notes;
}
