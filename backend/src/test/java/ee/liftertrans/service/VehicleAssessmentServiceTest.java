package ee.liftertrans.service;

import ee.liftertrans.ai.VehicleAssessmentInputDto;
import ee.liftertrans.dto.VehicleAssessmentResponseDto;
import ee.liftertrans.dto.VehicleAssessmentVehicleDto;
import ee.liftertrans.mapper.VehicleMapper;
import ee.liftertrans.persistence.entity.CraneCapacity;
import ee.liftertrans.persistence.entity.Vehicle;
import ee.liftertrans.persistence.repository.CraneCapacityRepository;
import ee.liftertrans.persistence.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleAssessmentServiceTest {

    @Mock
    private CraneCapacityRepository craneCapacityRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleAssessmentInputService vehicleAssessmentInputService;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleAssessmentService vehicleAssessmentService;

    @Test
    void missingInformationNeedsManualReviewWithoutSearchingVehicles() {
        // Puuduv info suunab päringu käsitsi ülevaatamisele.
        VehicleAssessmentInputDto inputDto = new VehicleAssessmentInputDto();
        inputDto.setMissingInformation(List.of("cargoWeight"));
        when(vehicleAssessmentInputService.extractAssessmentInput("küsimus")).thenReturn(inputDto);

        VehicleAssessmentResponseDto response = vehicleAssessmentService.assessVehicle("küsimus");

        assertEquals("NEEDS_MANUAL_REVIEW", response.getStatus());
        assertEquals(List.of("cargoWeight"), response.getMissingInformation());
        verifyNoInteractions(vehicleRepository, vehicleMapper, craneCapacityRepository);
    }

    @Test
    void noSuitableActiveVehicleNeedsManualReview() {
        // Kui aktiivset sõidukit ei leita, ei lükata päringut automaatselt tagasi.
        VehicleAssessmentInputDto inputDto = completeInputWithoutLifting();
        when(vehicleAssessmentInputService.extractAssessmentInput("küsimus")).thenReturn(inputDto);
        when(vehicleRepository.findVehiclesByStatus("ACTIVE")).thenReturn(List.of());
        when(vehicleMapper.toVehicleAssessmentVehicleDtos(List.of())).thenReturn(List.of());

        VehicleAssessmentResponseDto response = vehicleAssessmentService.assessVehicle("küsimus");

        assertEquals("NEEDS_MANUAL_REVIEW", response.getStatus());
        assertEquals("NOT_REQUIRED", response.getCraneAssessment());
        assertEquals(List.of(), response.getVehicles());
        verify(vehicleRepository).findVehiclesByStatus("ACTIVE");
    }

    @Test
    void suitableActiveVehicleReturnsPossibleWithVehicleDto() {
        // Piisava kandevõime ja platvormiga sõiduk annab automaatse sobivuse.
        VehicleAssessmentInputDto inputDto = completeInputWithoutLifting();
        Vehicle vehicle = new Vehicle();
        vehicle.setMaxCargoWeightKg(new BigDecimal("1000"));
        vehicle.setPlatformLengthMm(new BigDecimal("3000"));
        vehicle.setPlatformWidthMm(new BigDecimal("2000"));

        VehicleAssessmentVehicleDto vehicleDto = new VehicleAssessmentVehicleDto(
                "Testauto", "123ABC");

        when(vehicleAssessmentInputService.extractAssessmentInput("küsimus")).thenReturn(inputDto);
        when(vehicleRepository.findVehiclesByStatus("ACTIVE")).thenReturn(List.of(vehicle));
        when(vehicleMapper.toVehicleAssessmentVehicleDtos(List.of(vehicle))).thenReturn(List.of(vehicleDto));

        VehicleAssessmentResponseDto response = vehicleAssessmentService.assessVehicle("küsimus");

        assertEquals("POSSIBLE", response.getStatus());
        assertEquals(List.of(vehicleDto), response.getVehicles());
        assertEquals("NOT_REQUIRED", response.getCraneAssessment());
    }

    @Test
    void exactCraneMeasurementWithinCapacityReturnsMeasuredCapacityStatus() {
        // Täpne kraanamõõtepunkt lubab veose tõsta.
        VehicleAssessmentInputDto inputDto = completeInputWithLifting();
        Vehicle vehicle = suitableVehicle();
        vehicle.setId(10);
        CraneCapacity craneCapacity = craneCapacity("10", "600");
        VehicleAssessmentVehicleDto vehicleDto = new VehicleAssessmentVehicleDto("Testauto", "123ABC");

        when(vehicleAssessmentInputService.extractAssessmentInput("küsimus")).thenReturn(inputDto);
        when(vehicleRepository.findVehiclesByStatus("ACTIVE")).thenReturn(List.of(vehicle));
        when(vehicleMapper.toVehicleAssessmentVehicleDtos(List.of(vehicle))).thenReturn(List.of(vehicleDto));
        when(craneCapacityRepository.findCraneCapacitiesByVehicle_Id(10)).thenReturn(List.of(craneCapacity));

        VehicleAssessmentResponseDto response = vehicleAssessmentService.assessVehicle("küsimus");

        assertEquals("POSSIBLE", response.getStatus());
        assertEquals("WITHIN_MEASURED_CAPACITY", response.getCraneAssessment());
    }

    @Test
    void missingExactCraneMeasurementNeedsManualReview() {
        // Vahepealse kõrguse kohta mõõtepunkti ei oletata.
        VehicleAssessmentInputDto inputDto = completeInputWithLifting();
        inputDto.setRequestedLiftingHeight(new BigDecimal("10.5"));
        Vehicle vehicle = suitableVehicle();
        vehicle.setId(10);
        VehicleAssessmentVehicleDto vehicleDto = new VehicleAssessmentVehicleDto("Testauto", "123ABC");

        when(vehicleAssessmentInputService.extractAssessmentInput("küsimus")).thenReturn(inputDto);
        when(vehicleRepository.findVehiclesByStatus("ACTIVE")).thenReturn(List.of(vehicle));
        when(vehicleMapper.toVehicleAssessmentVehicleDtos(List.of(vehicle))).thenReturn(List.of(vehicleDto));
        when(craneCapacityRepository.findCraneCapacitiesByVehicle_Id(10))
                .thenReturn(List.of(craneCapacity("10", "600")));

        VehicleAssessmentResponseDto response = vehicleAssessmentService.assessVehicle("küsimus");

        assertEquals("NEEDS_MANUAL_REVIEW", response.getStatus());
        assertEquals("MANUAL_CONFIRMATION", response.getCraneAssessment());
    }

    private Vehicle suitableVehicle() {
        // Loob veose kaalu ja mõõtmeid mahutava aktiivse sõiduki.
        Vehicle vehicle = new Vehicle();
        vehicle.setMaxCargoWeightKg(new BigDecimal("1000"));
        vehicle.setPlatformLengthMm(new BigDecimal("3000"));
        vehicle.setPlatformWidthMm(new BigDecimal("2000"));
        return vehicle;
    }

    private CraneCapacity craneCapacity(String reachMeters, String maxWeightKg) {
        // Loob testis kasutatava kraana mõõtepunkti.
        CraneCapacity craneCapacity = new CraneCapacity();
        craneCapacity.setReachM(new BigDecimal(reachMeters));
        craneCapacity.setMaxWeightKg(new BigDecimal(maxWeightKg));
        return craneCapacity;
    }

    private VehicleAssessmentInputDto completeInputWithLifting() {
        // Annab testile veose andmed ja täpselt kümne meetri tõstekõrguse.
        VehicleAssessmentInputDto inputDto = completeInputWithoutLifting();
        inputDto.setLiftingRequired(true);
        inputDto.setRequestedLiftingHeight(new BigDecimal("10"));
        inputDto.setRequestedLiftingHeightUnit("m");
        return inputDto;
    }

    private VehicleAssessmentInputDto completeInputWithoutLifting() {
        // Annab teenusele vajalikud veose andmed olukorras, kus tõstmist pole vaja.
        VehicleAssessmentInputDto inputDto = new VehicleAssessmentInputDto();
        inputDto.setCargoWeight(new BigDecimal("500"));
        inputDto.setCargoWeightUnit("kg");
        inputDto.setCargoLength(new BigDecimal("2"));
        inputDto.setCargoLengthUnit("m");
        inputDto.setCargoWidth(new BigDecimal("1"));
        inputDto.setCargoWidthUnit("m");
        inputDto.setLiftingRequired(false);
        inputDto.setMissingInformation(List.of());
        return inputDto;
    }
}
