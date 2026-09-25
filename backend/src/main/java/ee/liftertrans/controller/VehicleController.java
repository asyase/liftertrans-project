package ee.liftertrans.controller;

import ee.liftertrans.dto.VehicleDto;
import ee.liftertrans.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/vehicles")
    public List<VehicleDto> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }
}
