package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class JobCreateResponseDto {
    private Integer jobId;
    private String status;
}
