package ee.liftertrans.service;

import ee.liftertrans.dto.JobCreateRequestDto;
import ee.liftertrans.dto.JobCreateResponseDto;
import ee.liftertrans.dto.JobDetailDto;
import ee.liftertrans.dto.JobDto;
import ee.liftertrans.dto.JobRequest;
import ee.liftertrans.dto.JobUpdateRequestDto;
import ee.liftertrans.dto.SelectOptionDto;
import ee.liftertrans.infrastructure.exception.ForbiddenException;
import ee.liftertrans.infrastructure.exception.IncorrectInputException;
import ee.liftertrans.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.liftertrans.mapper.JobMapper;
import ee.liftertrans.persistence.entity.Job;
import ee.liftertrans.persistence.enums.ExecutionType;
import ee.liftertrans.persistence.enums.JobType;
import ee.liftertrans.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final DriverService driverService;
    private final VehicleService vehicleService;
    // Kliendi leidmiseks customerId järgi (sama moodi nagu juht ja sõiduk)
    private final CustomerService customerService;
    private final SubcontractorService subcontractorService;


    public List<JobDto> getJobs() {

        // Võtame kõik tööd andmebaasist
        List<Job> jobs = jobRepository.findAll();

        // Muudame Entity objektid DTO objektideks
        List<JobDto> jobDtos = jobMapper.toJobDtos(jobs);

        return jobDtos;
    }


    public JobDetailDto getJob(Integer jobId) {

        // Leiame töö andmebaasist (kui pole, siis 404)
        Job job = getValidJobBy(jobId);

        // Muudame Entity DTO-ks
        return jobMapper.toJobDetailDto(job);
    }

    public Job getValidJobBy(Integer jobId) {
        // Otsime töö ID järgi, kui ei leia, siis 404
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("jobId", jobId));
    }

    public List<JobDto> getDriverJobs(Integer driverId) {

        // Kontrollime, et juht on olemas (kui pole, siis 404)
        driverService.getValidDriverBy(driverId);

        // Juhi töölaual on ainult aktiivsed tööd: planeeritud ja töös olevad
        List<Job> jobs = jobRepository.findDriverJobsBy(driverId, List.of("PLANNED", "IN_PROGRESS"));

        return jobMapper.toJobDtos(jobs);
    }

    @Transactional
    public void startDriverJob(Integer driverId, Integer jobId) {

        Job job = getValidDriverJobBy(driverId, jobId);

        // Alustada saab ainult planeeritud tööd
        validateJobStatus(job, "PLANNED");

        job.setStatus("IN_PROGRESS");
        job.setActualStartTime(Instant.now());
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);
    }

    @Transactional
    public void finishDriverJob(Integer driverId, Integer jobId) {

        Job job = getValidDriverJobBy(driverId, jobId);

        // Lõpetada saab ainult töös olevat tööd
        validateJobStatus(job, "IN_PROGRESS");

        job.setStatus("COMPLETED");
        job.setActualFinishTime(Instant.now());
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);
    }

    private Job getValidDriverJobBy(Integer driverId, Integer jobId) {

        Job job = getValidJobBy(jobId);

        // Juht tohib muuta ainult talle määratud tööd
        if (job.getDriver() == null || !job.getDriver().getId().equals(driverId)) {
            throw new ForbiddenException("Töö ei ole sellele juhile määratud", "ACCESS_DENIED");
        }

        return job;
    }

    private void validateJobStatus(Job job, String expectedStatus) {
        if (!expectedStatus.equals(job.getStatus())) {
            throw new IncorrectInputException(
                    "status: töö staatus on " + job.getStatus() + ", oodati " + expectedStatus,
                    "INCORRECT_JOB_STATUS"
            );
        }
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


        // VIGA OLI: siin oli ainult TODO, klienti tööle ei pandud ja salvestamine kukkus 500-ga,
        // sest job.customer_id on andmebaasis NOT NULL.
        // Klient on kohustuslik — tühja customerId püüab kinni juba @NotNull DTO-s.
        job.setCustomer(customerService.getValidCustomerBy(request.getCustomerId()));

        // Sõiduk (ainult kui on valitud)
        if (request.getVehicleId() != null) {
            job.setVehicle(vehicleService.getValidVehicleBy(request.getVehicleId()));
        }

        // Juht (ainult kui on valitud)
        if (request.getDriverId() != null) {
            job.setDriver(driverService.getValidDriverBy(request.getDriverId()));
        }

        // Alltöövõtja (ainult SUBCONTRACTED tööl, validateExecutionType kontrollib seda)
        if (request.getSubcontractorId() != null) {
            job.setSubcontractor(subcontractorService.getValidSubcontractorBy(request.getSubcontractorId()));
        }


        // Salvestame töö andmebaasi
        Job savedJob = jobRepository.save(job);

        // Muudame salvestatud Job Entity Response DTO-ks
        return jobMapper.toJobCreateResponseDto(savedJob);
    }


    @Transactional
    public JobDetailDto updateJob(Integer jobId, JobUpdateRequestDto request) {

        // Leiame olemasoleva töö (kui pole, siis 404)
        Job job = getValidJobBy(jobId);

        // Samad kontrollid nagu loomisel (tüüp, teostamise viis, aadressid)
        validateJobType(request);
        validateExecutionType(request);
        validateAddresses(request);

        // Töö tüüp ja teostamise viis
        job.setJobType(request.getJobType());
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

        // Klient on kohustuslik (tühja customerId püüab @NotNull DTO-s)
        job.setCustomer(customerService.getValidCustomerBy(request.getCustomerId()));

        // Sõiduk, juht ja alltöövõtja — kui väärtus puudub, siis seos eemaldatakse
        job.setVehicle(request.getVehicleId() != null
                ? vehicleService.getValidVehicleBy(request.getVehicleId())
                : null);

        job.setDriver(request.getDriverId() != null
                ? driverService.getValidDriverBy(request.getDriverId())
                : null);

        job.setSubcontractor(request.getSubcontractorId() != null
                ? subcontractorService.getValidSubcontractorBy(request.getSubcontractorId())
                : null);

        // Tavaline muutmine ei muuda staatust (DRAFT -> DRAFT, PLANNED -> PLANNED)
        job.setUpdatedAt(Instant.now());

        Job savedJob = jobRepository.save(job);

        return jobMapper.toJobDetailDto(savedJob);
    }


    @Transactional
    public JobDetailDto confirmJob(Integer jobId) {

        Job job = getValidJobBy(jobId);

        // Kinnitada saab ainult mustandit (DRAFT -> PLANNED)
        validateJobStatus(job, "DRAFT");

        job.setStatus("PLANNED");
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);

        return jobMapper.toJobDetailDto(job);
    }


    private void validateJobType(JobRequest request) {

        String jobType = request.getJobType();

        // Lubatud töö tüübid (JobType enum)
        if (!JobType.isValid(jobType)) {

            throw new IncorrectInputException(
                    "jobType: tundmatu töö tüüp",
                    "INCORRECT_INPUT"
            );
        }
    }


    private void validateExecutionType(JobRequest request) {

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


    private void validateAddresses(JobRequest request) {

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


        // TRANSPORT ja TRANSPORT_AND_CRANE puhul
        // on vajalik pealevõtu aadress
        if (isTransportJob(jobType)
                && (request.getPickupAddress() == null
                || request.getPickupAddress().isBlank())) {

            throw new IncorrectInputException(
                    "pickupAddress: pealevõtu aadress on kohustuslik",
                    "INCORRECT_INPUT"
            );
        }


        // TRANSPORT ja TRANSPORT_AND_CRANE puhul
        // on vajalik kohaletoimetamise aadress
        if (isTransportJob(jobType)
                && (request.getDeliveryAddress() == null
                || request.getDeliveryAddress().isBlank())) {

            throw new IncorrectInputException(
                    "deliveryAddress: kohaletoimetamise aadress on kohustuslik",
                    "INCORRECT_INPUT"
            );
        }
    }

    // Transporditööl (TRANSPORT, TRANSPORT_AND_CRANE) on pealevõtu ja kohaletoimetamise aadress
    private boolean isTransportJob(String jobType) {
        return "TRANSPORT".equals(jobType) || "TRANSPORT_AND_CRANE".equals(jobType);
    }
}
