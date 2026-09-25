package ee.liftertrans.controller;

import ee.liftertrans.dto.*;
import ee.liftertrans.dto.JobDto;
import ee.liftertrans.service.JobService;
import ee.liftertrans.infrastructure.error.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")

public class JobController {

    private final JobService jobService;

    @GetMapping("/jobs")
    @Operation(
            summary = "Tööde nimekirja kuvamine. Tagastab kõik tööd"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"
            )
    })
    public List<JobDto> getJobs() {

        // Küsime service'ilt tööde nimekirja
        List<JobDto> jobs = jobService.getJobs();

        return jobs;
    }

    @GetMapping("/jobs/types")
    @Operation(
            summary = "Töö tüüpide nimekiri valikuvälja jaoks"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"
            )
    })
    public List<SelectOptionDto> getJobTypes() {
        return jobService.getJobTypes();
    }

    @GetMapping("/jobs/execution-types")
    @Operation(
            summary = "Teostamise viiside nimekiri valikuvälja jaoks"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"
            )
    })
    public List<SelectOptionDto> getExecutionTypes() {
        return jobService.getExecutionTypes();
    }

    @PostMapping("/jobs")
    @Operation(
            summary = "Uue töö loomine. Tagastab loodud töö jobId ja staatuse"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Töö on edukalt loodud"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sisendandmed on vigased",
                    content = @Content(schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "INCORRECT_INPUT",
                                    value = """
                                            {
                                              "message": "customerId: ei tohi olla tühi",
                                              "errorCode": "INCORRECT_INPUT"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Seotud objekti (nt customerId) ei leitud",
                    content = @Content(schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "PRIMARY_KEY_NOT_FOUND",
                                    value = """
                                            {
                                              "message": "Ei leidnud primary keyd 'customerId' väärtusega: 999",
                                              "errorCode": "PRIMARY_KEY_NOT_FOUND"
                                            }
                                            """
                            )
                    )
            )
    })

    //Controller sai request'i
    //        ↓
    //annab selle Service'ile
    //        ↓
    //Service loob töö
    //        ↓
    //Service tagastab JobCreateResponseDto
    //        ↓
    //Controller tagastab selle frontendile

    public JobCreateResponseDto createJob (@RequestBody @Valid JobCreateRequestDto jobCreateRequestDto ) {

        return jobService.createJob(jobCreateRequestDto);


    }
}
