package ee.liftertrans.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "customer", schema = "liftertrans_project")
public class Customer {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 150)
    @NotNull
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    @Size(max = 150)
    @Column(name = "company_name", length = 150)
    private String companyName;
    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;
    @Size(max = 30)
    @NotNull
    @Column(name = "phone", nullable = false, length = 30)
    private String phone;
    @Column(name = "created_at")
    private Instant createdAt;
    @Size(max = 20)
    @Column(name = "company_registration_number", length = 20)
    private String companyRegistrationNumber;
    @Size(max = 30)
    @Column(name = "vat_number", length = 30)
    private String vatNumber;
    @Size(max = 150)
    @Column(name = "invoice_email", length = 150)
    private String invoiceEmail;


}
