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
@Table(name = "driver", schema = "liftertrans_project")
public class Driver {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 150)
    @NotNull
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    @Size(max = 30)
    @NotNull
    @Column(name = "phone", nullable = false, length = 30)
    private String phone;
    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;
    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;


}
