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
    // Võtame seotud Subcontractor entity id; oma auto puhul on subcontractor null → subcontractorId null
    @Mapping(source = "subcontractor.id", target = "subcontractorId")
    VehicleDto toVehicleDto(Vehicle vehicle);

    List<VehicleDto> toVehicleDtos(List<Vehicle> vehicles);

}
