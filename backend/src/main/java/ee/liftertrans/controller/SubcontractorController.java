package ee.liftertrans.controller;

import ee.liftertrans.dto.SubcontractorDto;
import ee.liftertrans.service.SubcontractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SubcontractorController {

    private final SubcontractorService subcontractorService;

    @GetMapping("/subcontractors")
    @Operation(summary = "Aktiivsete alltöövõtjate nimekiri (tellimuse vormi rippmenüü jaoks)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    public List<SubcontractorDto> getActiveSubcontractors() {
        return subcontractorService.getActiveSubcontractors();
    }
}
