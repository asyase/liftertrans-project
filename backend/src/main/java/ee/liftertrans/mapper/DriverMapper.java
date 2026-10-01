package ee.liftertrans.mapper;


import ee.liftertrans.dto.DriverDto;
import ee.liftertrans.dto.DriverRequestDto;
import ee.liftertrans.persistence.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {

    @Mapping(source = "id", target = "driverId")
    DriverDto toDriverDto(Driver driver);

    @Mapping(target = "id", ignore = true)
    Driver toDriver(DriverRequestDto driverRequestDto);

    @Mapping(source = "driverId", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "active", target = "active")
    Driver toDriver(DriverDto driverDto);

    List<DriverDto> driverDtoList(List<Driver> drivers);

}

