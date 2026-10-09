package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor

//sõiduki info, mida kasutatakse veose sobivushinnagu vastuses
public class VehicleAssessmentVehicleDto {
    private String name;
    private String registrationNumber;


}
