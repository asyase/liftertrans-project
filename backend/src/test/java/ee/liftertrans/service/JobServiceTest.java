package ee.liftertrans.service;

import ee.liftertrans.dto.JobCreateRequestDto;
import ee.liftertrans.dto.JobCreateResponseDto;
import ee.liftertrans.dto.SelectOptionDto;
import ee.liftertrans.infrastructure.exception.IncorrectInputException;
import ee.liftertrans.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.liftertrans.mapper.JobMapper;
import ee.liftertrans.persistence.entity.Driver;
import ee.liftertrans.persistence.entity.Job;
import ee.liftertrans.persistence.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobMapper jobMapper;

    @Mock
    private DriverService driverService;

    @InjectMocks
    private JobService jobService;

    private JobCreateRequestDto request;

    @BeforeEach
    void setUp() {
        // Korrektne päring: oma ressursiga transport + kraana
        request = new JobCreateRequestDto();
        request.setCustomerId(1);
        request.setJobType("TRANSPORT_AND_CRANE");
        request.setExecutionType("INTERNAL");
        request.setPickupAddress("Tallinn, Pärnu mnt 1");
        request.setDeliveryAddress("Tartu, Riia 2");
        request.setPlannedStartTime(LocalDateTime.of(2026, 10, 1, 8, 0));
        request.setNotes("test");
    }

    // ===== Valikuväljade nimekirjad =====

    @Test
    void getJobTypes_returnsAllJobTypes() {
        List<SelectOptionDto> jobTypes = jobService.getJobTypes();

        assertEquals(List.of(
                new SelectOptionDto("TRANSPORT_AND_CRANE", "Transport ja kraana"),
                new SelectOptionDto("CRANE_ONLY", "Ainult kraana")
        ), jobTypes);
    }

    @Test
    void getExecutionTypes_returnsAllExecutionTypes() {
        List<SelectOptionDto> executionTypes = jobService.getExecutionTypes();

        assertEquals(List.of(
                new SelectOptionDto("INTERNAL", "Oma transport"),
                new SelectOptionDto("SUBCONTRACTED", "Alltöövõtja")
        ), executionTypes);
    }

    // ===== Edukas loomine =====

    @Test
    void createJob_validRequest_savesJobInDraftStatus() {
        JobCreateResponseDto expectedResponse = new JobCreateResponseDto(7, "DRAFT");
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jobMapper.toJobCreateResponseDto(any(Job.class))).thenReturn(expectedResponse);

        JobCreateResponseDto response = jobService.createJob(request);

        assertSame(expectedResponse, response);

        // Kontrollime, et andmebaasi salvestati õigete väärtustega Job
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepository).save(jobCaptor.capture());
        Job savedJob = jobCaptor.getValue();

        assertEquals("DRAFT", savedJob.getStatus());
        assertEquals("TRANSPORT_AND_CRANE", savedJob.getJobType());
        assertEquals("INTERNAL", savedJob.getExecutionType());
        assertEquals("Tallinn, Pärnu mnt 1", savedJob.getPickupAddress());
        assertEquals("Tartu, Riia 2", savedJob.getDeliveryAddress());
        assertEquals(LocalDateTime.of(2026, 10, 1, 8, 0), savedJob.getPlannedStartTime());
        assertEquals("test", savedJob.getNotes());
    }

    // ===== Juht =====

    @Test
    void createJob_withExistingDriver_setsDriverOnJob() {
        Driver driver = new Driver();
        driver.setId(3);
        request.setDriverId(3);
        when(driverService.getValidDriverBy(3)).thenReturn(driver);
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        jobService.createJob(request);

        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepository).save(jobCaptor.capture());
        assertSame(driver, jobCaptor.getValue().getDriver());
    }

    @Test
    void createJob_driverNotFound_throwsPrimaryKeyNotFound() {
        request.setDriverId(999);
        when(driverService.getValidDriverBy(999)).thenThrow(new PrimaryKeyNotFoundException("driverId", 999));

        PrimaryKeyNotFoundException exception = assertThrows(
                PrimaryKeyNotFoundException.class,
                () -> jobService.createJob(request)
        );

        assertEquals("PRIMARY_KEY_NOT_FOUND", exception.getErrorCode());
        verify(jobRepository, never()).save(any(Job.class));
    }

    // ===== Töö tüüp =====

    @Test
    void createJob_unknownJobType_throwsIncorrectInput() {
        request.setJobType("FLYING");

        assertIncorrectInput("jobType: tundmatu töö tüüp");
    }

    // ===== Teostamise viis =====

    @Test
    void createJob_unknownExecutionType_throwsIncorrectInput() {
        request.setExecutionType("SOMETHING");

        assertIncorrectInput("executionType: tundmatu teostamise viis");
    }

    @Test
    void createJob_subcontractedWithoutSubcontractor_throwsIncorrectInput() {
        request.setExecutionType("SUBCONTRACTED");
        request.setSubcontractorId(null);

        assertIncorrectInput("subcontractorId: alltöövõtja on kohustuslik");
    }

    @Test
    void createJob_subcontractedWithDriver_throwsIncorrectInput() {
        request.setExecutionType("SUBCONTRACTED");
        request.setSubcontractorId(1);
        request.setDriverId(1);

        assertIncorrectInput("driverId: alltöövõtja töö puhul ei tohi juht olla määratud");
    }

    @Test
    void createJob_internalWithSubcontractor_throwsIncorrectInput() {
        request.setSubcontractorId(1);

        assertIncorrectInput("subcontractorId: oma ressursiga töö puhul peab alltöövõtja puuduma");
    }

    // ===== Aadressid =====

    @Test
    void createJob_craneOnlyWithoutServiceAddress_throwsIncorrectInput() {
        request.setJobType("CRANE_ONLY");
        request.setServiceAddress(" ");

        assertIncorrectInput("serviceAddress: töö aadress on kohustuslik");
    }

    @Test
    void createJob_transportWithoutPickupAddress_throwsIncorrectInput() {
        request.setPickupAddress(null);

        assertIncorrectInput("pickupAddress: pealevõtu aadress on kohustuslik");
    }

    @Test
    void createJob_transportWithoutDeliveryAddress_throwsIncorrectInput() {
        request.setDeliveryAddress("");

        assertIncorrectInput("deliveryAddress: kohaletoimetamise aadress on kohustuslik");
    }

    // Kontrollib veateadet ja et vigast tööd andmebaasi ei salvestatud
    private void assertIncorrectInput(String expectedMessage) {
        IncorrectInputException exception = assertThrows(
                IncorrectInputException.class,
                () -> jobService.createJob(request)
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals("INCORRECT_INPUT", exception.getErrorCode());
        verify(jobRepository, never()).save(any(Job.class));
    }
}
