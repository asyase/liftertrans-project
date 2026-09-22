package ee.liftertrans.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "driver")
@Data
@NoArgsConstructor
public class Driver {

    @Id
    private Integer id;

    private String name;
    private String phone;
    private String email;
    private Boolean active;
}
