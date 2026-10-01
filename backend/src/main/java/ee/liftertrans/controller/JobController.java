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

    @GetMapping("/jobs/{jobId}")
    @Operation(summary = "Ühe töö andmed detailvaate jaoks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "404",
                    description = "'message': Ei leidnud primary keyd 'jobId' väärtusega: 'y', 'errorCode': PRIMARY_KEY_NOT_FOUND",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public JobDetailDto getJob(@PathVariable Integer jobId) {

        // jobId tuleb URL-ist, nt /api/jobs/2 → jobId = 2
        return jobService.getJob(jobId);
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

    @GetMapping("/drivers/{driverId}/jobs")
    @Operation(summary = "Juhi töölaud: juhile määratud tööd staatusega PLANNED ja IN_PROGRESS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "404",
                    description = "'message': Ei leidnud primary keyd 'driverId' väärtusega: 'y', 'errorCode': PRIMARY_KEY_NOT_FOUND",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public List<JobDto> getDriverJobs(@PathVariable Integer driverId) {

        return jobService.getDriverJobs(driverId);
    }

    @PatchMapping("/drivers/{driverId}/jobs/{jobId}/start")
    @Operation(summary = "Juht alustab tööd: PLANNED → IN_PROGRESS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "400",
                    description = "'message': status: töö staatus on ..., oodati PLANNED, 'errorCode': INCORRECT_JOB_STATUS",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "'message': Töö ei ole sellele juhile määratud, 'errorCode': ACCESS_DENIED",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "'message': Ei leidnud primary keyd 'jobId' väärtusega: 'y', 'errorCode': PRIMARY_KEY_NOT_FOUND",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public void startDriverJob(@PathVariable Integer driverId, @PathVariable Integer jobId) {

        jobService.startDriverJob(driverId, jobId);
    }

    @PatchMapping("/drivers/{driverId}/jobs/{jobId}/finish")
    @Operation(summary = "Juht lõpetab töö: IN_PROGRESS → COMPLETED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "400",
                    description = "'message': status: töö staatus on ..., oodati IN_PROGRESS, 'errorCode': INCORRECT_JOB_STATUS",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "'message': Töö ei ole sellele juhile määratud, 'errorCode': ACCESS_DENIED",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "'message': Ei leidnud primary keyd 'jobId' väärtusega: 'y', 'errorCode': PRIMARY_KEY_NOT_FOUND",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public void finishDriverJob(@PathVariable Integer driverId, @PathVariable Integer jobId) {

        jobService.finishDriverJob(driverId, jobId);
    }
}
