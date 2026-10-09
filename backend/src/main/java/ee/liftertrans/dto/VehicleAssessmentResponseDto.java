package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleAssessmentResponseDto {

    private String status;
    private String answer;
    //see väli tähistab vastuses olevat sõidukite loendit
    private List<VehicleAssessmentVehicleDto> vehicles;
    private String craneAssessment;
    private List<String> missingInformation;
}
