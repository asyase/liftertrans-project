package ee.liftertrans.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subcontractor", schema = "liftertrans_project")
public class Subcontractor {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 150)
    @NotNull
    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;
    @Size(max = 150)
    @Column(name = "contact_name", length = 150)
    private String contactName;
    @Size(max = 30)
    @Column(name = "phone", length = 30)
    private String phone;
    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;
    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;
    @Column(name = "active")
    private Boolean active;
    @Size(max = 20)
    @Column(name = "company_registration_number", length = 20)
    private String companyRegistrationNumber;
    @Size(max = 30)
    @Column(name = "vat_number", length = 30)
    private String vatNumber;

}
