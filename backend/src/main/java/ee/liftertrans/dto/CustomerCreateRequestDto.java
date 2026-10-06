package ee.liftertrans.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerCreateRequestDto {

    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 150)
    private String companyName;

    @Size(max = 20)
    private String companyRegistrationNumber;

    @Size(max = 30)
    private String vatNumber;

    @Email
    @Size(max = 150)
    private String email;

    @Email
    @Size(max = 150)
    private String invoiceEmail;

    @NotBlank
    @Size(max = 30)
    private String phone;
}
