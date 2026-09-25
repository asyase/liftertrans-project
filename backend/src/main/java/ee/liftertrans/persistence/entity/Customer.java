package ee.liftertrans.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
public class Customer {

    @Id
    private Integer id;

    private String name;

    @Column(name = "company_name")
    private String companyName;

    private String email;
    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "company_registration_number")
    private String companyRegistrationNumber;

    @Column(name = "vat_number")
    private String vatNumber;

    @Column(name = "invoice_email")
    private String invoiceEmail;
}
