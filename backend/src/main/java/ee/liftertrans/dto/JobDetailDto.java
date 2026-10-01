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
    private Integer customerId;
    private String customerName;
    private String customerCompanyName;
    private String customerPhone;
    private String customerEmail;

    // Auto, juht või alltöövõtja (SUBCONTRACTED puhul juhti pole)
    // id-d on vajalikud muutmise vormi jaoks (valikud eeltäidetakse id järgi)
    private Integer vehicleId;
    private String vehicleRegistrationNumber;
    private String vehicleName;
    private Integer driverId;
    private String driverName;
    private Integer subcontractorId;
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
