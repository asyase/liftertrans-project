package ee.liftertrans.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema
public class CustomerCreateRequestDto {

    @NotBlank
    @Schema
    private String name;

    @NotBlank
    private String companyName;

    @NotBlank
    private String companyRegistrationNumber;

    private String vatNumber;

    @NotBlank
    @Email
    private String email;

    @Email
    private String invoiceEmail;

    private String phone;
}