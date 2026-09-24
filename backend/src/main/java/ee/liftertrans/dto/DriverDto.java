package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Repository = võtab DB-st
//Mapper = convertib Entity DTO-ks.

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {

    private Integer driverId;
    private String name;
    private String phone;
    private String email;
    private Boolean active;

}
