package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Repository = võtab DB-st
//Mapper = convertib Entity DTO-ks.

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AuthResponseDto {
private Integer userId;
private String email;
private String roleName;
private Integer driverId;
}
