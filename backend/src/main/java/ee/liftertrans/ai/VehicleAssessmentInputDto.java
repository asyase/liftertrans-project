package ee.liftertrans.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleAssessmentInputDto {

    private BigDecimal cargoWeight;
    private String cargoWeightUnit;
    private BigDecimal cargoLength;
    private String cargoLengthUnit;
    private BigDecimal cargoWidth;
    private String cargoWidthUnit;
    private Boolean liftingRequired;
    private BigDecimal requestedLiftingHeight;
    private String requestedLiftingHeightUnit;
    private List<String> missingInformation;

}
