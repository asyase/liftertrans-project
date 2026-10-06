package ee.liftertrans.service;


import ee.liftertrans.dto.DriverDto;
import ee.liftertrans.dto.DriverRequestDto;
import ee.liftertrans.infrastructure.exception.BusinessException;
import ee.liftertrans.infrastructure.exception.ErrorCode;
import ee.liftertrans.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.liftertrans.mapper.DriverMapper;
import ee.liftertrans.persistence.repository.DriverRepository;
import ee.liftertrans.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ee.liftertrans.persistence.entity.Driver;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final UserRepository userRepository;

    public List<DriverDto> getAllDrivers() {

    // Võtame kõik juhid andmebaasist

        List<Driver> drivers = driverRepository.findAll();

        // Muudame Entity objektid DTO objektideks
        List<DriverDto> driverDtos = driverMapper.driverDtoList(drivers);

        return driverDtos;
    }


    public DriverDto getDriver(Integer driverId) {

        // leiame juhi andmebaasist, kui pole viskab 404 PRIMARY_KEY_NOT_FOUND
        Driver driver = getValidDriverBy(driverId);

        // Muudame andmebaasist saadud driver vastuse DTO-ks
        return driverMapper.toDriverDto(driver);


    }

    public Driver getValidDriverBy(Integer driverId) {


        // Otsime juhi ID järgi, kui ei leia, siis 404
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("driverId", driverId));


    }

    public void updateDriver(Integer driverId, DriverRequestDto driverRequestDto){

        Driver driver = getValidDriverBy(driverId);
        driverMapper.updateDriver(driverRequestDto, driver);

        driverRepository.save(driver);


    }


    // enne kustutamist veendume, et juhiga ei oleks soetud tellimusi. Otsime juhiId järgi


    @Transactional
    public void deleteDriver(Integer driverId) {

        // enne kustutamist veendume, et juhiga ei oleks soetud tellimusi. Otsime juhiId järgi
        Driver driver = getValidDriverBy(driverId);

        // Töödega juhti ei tohi kustutada
        if (driverRepository.existsJobsByDriverId(driverId)) {
            throw  new BusinessException(ErrorCode.RESOURCE_IN_USE);
        }

        // Juhi küljes võib olla kasutajakonto (user.driver_id), mis muidu blokeeriks kustutamise
        // andmebaasi tasemel — eemaldame selle enne juhi kustutamist
        userRepository.deleteByDriverId(driverId);
        driverRepository.delete(driver);
    }

    public void createDriver(DriverRequestDto driverRequestDto) {
        Driver driver = driverMapper.toDriver(driverRequestDto);
        driverRepository.save(driver);
    }
}
