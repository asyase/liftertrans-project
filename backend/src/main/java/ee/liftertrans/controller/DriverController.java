package ee.liftertrans.controller;

import ee.liftertrans.dto.DriverDto;
import ee.liftertrans.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DriverController {
    private final DriverService driverService;

    @GetMapping("/drivers")
    public List<DriverDto> getAllDrivers() {

        return driverService.getAllDrivers();
    }



}
