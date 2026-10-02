package ee.liftertrans.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class CustomerDetailDto {

    @Schema
    private Long id;

    @Schema
    private String name;

    @Schema
    private String companyName;

    @Schema
    private String companyRegistrationNumber;

    @Schema
    private String vatNumber;

    @Schema
    private String email;

    @Schema
    private String invoiceEmail;

    @Schema
    private String phone;
}