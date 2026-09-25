package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobListDto {

    private Integer id;

    private LocalDateTime plannedStartTime;

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