package ee.liftertrans.controller;

import ee.liftertrans.dto.DriverDto;
import ee.liftertrans.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DriverController {
    private final DriverService driverService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/drivers")
    @Operation(
            summary = "Juhtide nimekirja kuvamine"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description="Juhtide nimekiri tagastati edukalt"
            )
    })
    public List<DriverDto> getAllDrivers() {

        return driverService.getAllDrivers();
    }



}
