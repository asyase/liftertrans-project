package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Alltöövõtja rippmenüü jaoks (tellimuse vorm)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubcontractorDto {
    private Integer subcontractorId;
    private String companyName;
    private String contactName;
    private String phone;
    private String email;
    private Boolean active;
}
