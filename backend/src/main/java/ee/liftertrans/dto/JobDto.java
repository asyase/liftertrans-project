package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDto {

    private Integer jobId;

    private Instant plannedStartTime;

    private String customerName;

    private String jobType;

    private String pickupAddress;

    private String serviceAddress;

    private String deliveryAddress;

    private String vehicleRegistrationNumber;

    private String driverName;

    private String subcontractorName;

    private String status;
}