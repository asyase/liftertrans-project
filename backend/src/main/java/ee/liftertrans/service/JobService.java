package ee.liftertrans.service;


import ee.liftertrans.dto.JobDto;
import ee.liftertrans.persistence.entity.Job;
import ee.liftertrans.mapper.JobMapper;
import ee.liftertrans.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    public List<JobDto> getJobs() {

        // Võtame kõik tööd andmebaasist
        List<Job> jobs = jobRepository.findAll();

        // Muudame Entity objektid DTO objektideks
        List<JobDto> jobDtos = jobMapper.toJobDtos(jobs);

        return jobDtos;
    }
}