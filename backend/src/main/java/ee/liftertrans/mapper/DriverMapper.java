package ee.liftertrans.mapper;


import ee.liftertrans.dto.DriverDto;
import ee.liftertrans.persistence.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {
    @Mapping(source = "id", target = "driverId")

    DriverDto toDriverDto(Driver driver);

    List<DriverDto> driverDtoList(List<Driver> drivers);



}


