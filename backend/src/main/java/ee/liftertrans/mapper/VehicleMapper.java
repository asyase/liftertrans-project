package ee.liftertrans.mapper;

import ee.liftertrans.dto.VehicleDto;
import ee.liftertrans.persistence.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleMapper {

    @Mapping(source = "id", target = "vehicleId")
    VehicleDto toVehicleDto(Vehicle vehicle);

    List<VehicleDto> toVehicleDtos(List<Vehicle> vehicles);

}
