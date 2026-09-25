package ee.liftertrans.controller;

import ee.liftertrans.dto.JobDto;
import ee.liftertrans.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")

public class JobController {

    private final JobService jobService;

    @GetMapping("/jobs")
    public List<JobDto> getJobs() {

        // Küsime service'ilt tööde nimekirja
        List<JobDto> jobs = jobService.getJobs();

        return jobs;
    }
}