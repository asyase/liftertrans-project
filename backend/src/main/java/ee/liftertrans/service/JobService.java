package ee.liftertrans.service;

import ee.liftertrans.dto.JobCreateRequestDto;
import ee.liftertrans.dto.JobCreateResponseDto;
import ee.liftertrans.dto.JobDto;
import ee.liftertrans.dto.SelectOptionDto;
import ee.liftertrans.infrastructure.exception.IncorrectInputException;
import ee.liftertrans.mapper.JobMapper;
import ee.liftertrans.persistence.entity.Job;
import ee.liftertrans.persistence.enums.ExecutionType;
import ee.liftertrans.persistence.enums.JobType;
import ee.liftertrans.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final DriverService driverService;
    private final VehicleService vehicleService;


    public List<JobDto> getJobs() {

        // Võtame kõik tööd andmebaasist
        List<Job> jobs = jobRepository.findAll();

        // Muudame Entity objektid DTO objektideks
        List<JobDto> jobDtos = jobMapper.toJobDtos(jobs);

        return jobDtos;
    }


    public List<SelectOptionDto> getJobTypes() {

        // Teeme igast töö tüübist valikuvälja rea
        return Arrays.stream(JobType.values())
                .map(jobType -> new SelectOptionDto(jobType.name(), jobType.getText()))
                .toList();
    }

    public List<SelectOptionDto> getExecutionTypes() {

        // Teeme igast teostamise viisist valikuvälja rea
        return Arrays.stream(ExecutionType.values())
                .map(executionType -> new SelectOptionDto(executionType.name(), executionType.getText()))
                .toList();
    }

    @Transactional
    public JobCreateResponseDto createJob(JobCreateRequestDto request) {

        // Kontrollime töö tüüpi
        validateJobType(request);

        // Kontrollime teostamise viisi
        validateExecutionType(request);

        // Kontrollime aadresse
        validateAddresses(request);


        // Loome uue Job entity
        Job job = new Job();

        // Töö tüüp
        job.setJobType(request.getJobType());

        // Teostamise viis
        job.setExecutionType(request.getExecutionType());

        // Aadressid
        job.setPickupAddress(request.getPickupAddress());
        job.setDeliveryAddress(request.getDeliveryAddress());
        job.setServiceAddress(request.getServiceAddress());

        // Vastuvõtja
        job.setReceiverName(request.getReceiverName());
        job.setReceiverPhone(request.getReceiverPhone());

        // Planeeritud ajad
        job.setPlannedStartTime(request.getPlannedStartTime());
        job.setPlannedEndTime(request.getPlannedEndTime());

        // Hinnangulised km ja tunnid
        job.setEstimatedKm(request.getEstimatedKm());
        job.setEstimatedHours(request.getEstimatedHours());

        // Märkused
        job.setNotes(request.getNotes());

        // Uus töö luuakse DRAFT staatuses
        job.setStatus("DRAFT");


        // TODO: leia Customer customerId järgi
        // job.setCustomer(customer);

        // Sõiduk (ainult kui on valitud)
        if (request.getVehicleId() != null) {
            job.setVehicle(vehicleService.getValidVehicleBy(request.getVehicleId()));
        }

        // Juht (ainult kui on valitud)
        if (request.getDriverId() != null) {
            job.setDriver(driverService.getValidDriverBy(request.getDriverId()));
        }

        // TODO: kui subcontractorId != null, leia Subcontractor
        // job.setSubcontractor(subcontractor);


        // Salvestame töö andmebaasi
        Job savedJob = jobRepository.save(job);

        // Muudame salvestatud Job Entity Response DTO-ks
        return jobMapper.toJobCreateResponseDto(savedJob);
    }


    private void validateJobType(JobCreateRequestDto request) {

        String jobType = request.getJobType();

        // Lubatud töö tüübid (JobType enum)
        if (!JobType.isValid(jobType)) {

            throw new IncorrectInputException(
                    "jobType: tundmatu töö tüüp",
                    "INCORRECT_INPUT"
            );
        }
    }


    private void validateExecutionType(JobCreateRequestDto request) {

        String executionType = request.getExecutionType();

        // Lubatud teostamise viisid (ExecutionType enum)
        if (!ExecutionType.isValid(executionType)) {

            throw new IncorrectInputException(
                    "executionType: tundmatu teostamise viis",
                    "INCORRECT_INPUT"
            );
        }


        // SUBCONTRACTED puhul peab alltöövõtja olema valitud
        if ("SUBCONTRACTED".equals(executionType)
                && request.getSubcontractorId() == null) {

            throw new IncorrectInputException(
                    "subcontractorId: alltöövõtja on kohustuslik",
                    "INCORRECT_INPUT"
            );
        }


        // SUBCONTRACTED puhul juhti ei valita
        if ("SUBCONTRACTED".equals(executionType)
                && request.getDriverId() != null) {

            throw new IncorrectInputException(
                    "driverId: alltöövõtja töö puhul ei tohi juht olla määratud",
                    "INCORRECT_INPUT"
            );
        }


        // INTERNAL puhul alltöövõtjat ei valita
        if ("INTERNAL".equals(executionType)
                && request.getSubcontractorId() != null) {

            throw new IncorrectInputException(
                    "subcontractorId: oma ressursiga töö puhul peab alltöövõtja puuduma",
                    "INCORRECT_INPUT"
            );
        }
    }


    private void validateAddresses(JobCreateRequestDto request) {

        String jobType = request.getJobType();


        // CRANE_ONLY puhul on vajalik töö aadress
        if ("CRANE_ONLY".equals(jobType)
                && (request.getServiceAddress() == null
                || request.getServiceAddress().isBlank())) {

            throw new IncorrectInputException(
                    "serviceAddress: töö aadress on kohustuslik",
                    "INCORRECT_INPUT"
            );
        }


        // TRANSPORT_AND_CRANE puhul
        // on vajalik pealevõtu aadress
        if ("TRANSPORT_AND_CRANE".equals(jobType)
                && (request.getPickupAddress() == null
                || request.getPickupAddress().isBlank())) {

            throw new IncorrectInputException(
                    "pickupAddress: pealevõtu aadress on kohustuslik",
                    "INCORRECT_INPUT"
            );
        }


        // TRANSPORT_AND_CRANE puhul
        // on vajalik kohaletoimetamise aadress
        if ("TRANSPORT_AND_CRANE".equals(jobType)
                && (request.getDeliveryAddress() == null
                || request.getDeliveryAddress().isBlank())) {

            throw new IncorrectInputException(
                    "deliveryAddress: kohaletoimetamise aadress on kohustuslik",
                    "INCORRECT_INPUT"
            );
        }
    }
}