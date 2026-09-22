package ee.liftertrans.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subcontractor")
@Data
@NoArgsConstructor
public class Subcontractor {

    @Id
    private Integer id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "contact_name")
    private String contactName;

    private String phone;
    private String email;
    private String notes;
    private Boolean active;

    @Column(name = "company_registration_number")
    private String companyRegistrationNumber;

    @Column(name = "vat_number")
    private String vatNumber;
}
