package ee.liftertrans.controller;

import ee.liftertrans.dto.VehicleAssessmentRequestDto;
import ee.liftertrans.dto.VehicleAssessmentResponseDto;
import ee.liftertrans.service.VehicleAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class VehicleAssessmentController {

    private final VehicleAssessmentService vehicleAssessmentService;

    @PostMapping("/vehicle-assessment")
    @Operation(summary = "Hindab veose transpordi ja tõstmise sobivust")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eelhinnang tagastati"),
            @ApiResponse(responseCode = "400", description = "Küsimus on puudu, tühi või liiga pikk"),
            @ApiResponse(responseCode = "429", description = "Päringute limiit on täis")
    })
    public VehicleAssessmentResponseDto assessVehicle(
            @Valid @RequestBody VehicleAssessmentRequestDto requestDto) {
        return vehicleAssessmentService.assessVehicle(requestDto.getQuestion());
    }
}
