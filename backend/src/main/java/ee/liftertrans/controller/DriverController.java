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

    @DeleteMapping("/drivers/{driverId}")
    @Operation(summary = "Juhi kustutamine (ainult kui temaga ei ole seotud töid)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juht on kustutatud"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Juhiga on seotud töid, teda ei saa kustutada",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "RESOURCE_IN_USE",
                                    value = """
                                            {
                                              "message": "Juhti ei saa kustutada, kuna temaga on seotud töid",
                                              "errorCode": "RESOURCE_IN_USE"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Juhti ei leitud",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "PRIMARY_KEY_NOT_FOUND",
                                    value = """
                                            {
                                              "message": "Ei leidnud primary keyd 'driverId' väärtusega: 99",
                                              "errorCode": "PRIMARY_KEY_NOT_FOUND"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Void> deleteDriver(@PathVariable Integer driverId) {
        driverService.deleteDriver(driverId);
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
