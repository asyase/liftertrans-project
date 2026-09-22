package ee.liftertrans.service;


import ee.liftertrans.dto.JobListDto;
import ee.liftertrans.entity.Job;
import ee.liftertrans.mapper.JobMapper;
import ee.liftertrans.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    public List<JobListDto> getJobs() {

        // Võtame kõik tööd andmebaasist
        List<Job> jobs = jobRepository.findAll();

        // Muudame Entity objektid DTO objektideks
        List<JobListDto> jobDtos = jobMapper.toJobListDtos(jobs);

        return jobDtos;
    }
}