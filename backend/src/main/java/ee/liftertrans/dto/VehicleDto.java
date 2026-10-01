package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {

    private Integer vehicleId;
    private String registrationNumber;
    private String name;
    // ACTIVE / IN_SERVICE / ... — tellimuse vormis näidatakse ainult ACTIVE autosid
    private String status;
    // null = LIFTERTRANS-i oma auto, number = alltöövõtja auto
    private Integer subcontractorId;

}
