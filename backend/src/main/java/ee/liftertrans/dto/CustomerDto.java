package ee.liftertrans.dto;

import lombok.Data;

@Data
public class CustomerDto {
    private Integer id;
    private String name;
    private String companyName;
    private String companyRegistrationNumber;
    private String vatNumber;
    private String email;
    private String invoiceEmail;
    private String phone;
}
