package ee.liftertrans.service;

import ee.liftertrans.dto.VehicleDto;
import ee.liftertrans.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.liftertrans.mapper.VehicleMapper;
import ee.liftertrans.persistence.entity.Vehicle;
import ee.liftertrans.persistence.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public List<VehicleDto> getAllVehicles() {

        // Võtame kõik sõidukid andmebaasist
        List<Vehicle> vehicles = vehicleRepository.findAll();

        // Muudame Entity objektid DTO objektideks
        return vehicleMapper.toVehicleDtos(vehicles);
    }

    public Vehicle getValidVehicleBy(Integer vehicleId) {

        // Otsime sõiduki ID järgi, kui ei leia, siis 404
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("vehicleId", vehicleId));
    }
}
