package ee.liftertrans.controller;

import ee.liftertrans.dto.DriverDto;
import ee.liftertrans.dto.DriverRequestDto;
import ee.liftertrans.infrastructure.error.ApiError;
import ee.liftertrans.service.DriverService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DriverController {
    private final DriverService driverService;


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

    @DeleteMapping("/drivers/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Integer id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/drivers")
    @Operation(summary = "Uue juhi lisamine")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Juht on edukalt lisatud"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sisendandmed on vigased",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "INCORRECT_INPUT",
                                    value = """
                                            {
                                              "message": "name: must not be blank",
                                              "errorCode": "INCORRECT_INPUT"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Void> createDriver(@RequestBody @Valid DriverRequestDto driverRequestDto) {
        driverService.createDriver(driverRequestDto);
        return ResponseEntity.ok().build();
    }

}
