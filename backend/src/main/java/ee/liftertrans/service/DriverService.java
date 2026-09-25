package ee.liftertrans.service;


import ee.liftertrans.dto.DriverDto;
import ee.liftertrans.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.liftertrans.mapper.DriverMapper;
import ee.liftertrans.persistence.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ee.liftertrans.persistence.entity.Driver;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    public List<DriverDto> getAllDrivers() {

    // Võtame kõik juhid andmebaasist

        List<Driver> drivers = driverRepository.findAll();

        // Muudame Entity objektid DTO objektideks
        List<DriverDto> driverDtos = driverMapper.driverDtoList(drivers);

        return driverDtos;
    }

    public Driver getValidDriverBy(Integer driverId) {

        // Otsime juhi ID järgi, kui ei leia, siis 404
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("driverId", driverId));
    }
}
