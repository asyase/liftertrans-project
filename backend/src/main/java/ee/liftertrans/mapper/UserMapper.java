package ee.liftertrans.mapper;

import ee.liftertrans.controller.dto.AuthResponseDto;
import ee.liftertrans.persistence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)

public interface UserMapper {

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "role.name", target = "roleName")
    @Mapping(source = "driver.id", target = "driverId")
    AuthResponseDto toAuthResponseDto(User user);
}
