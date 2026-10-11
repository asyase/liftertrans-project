package ee.liftertrans.service;


import ee.liftertrans.ai.VehicleAssessmentInputDto;
import ee.liftertrans.dto.VehicleAssessmentResponseDto;
import ee.liftertrans.dto.VehicleAssessmentVehicleDto;
import ee.liftertrans.mapper.VehicleMapper;
import ee.liftertrans.persistence.entity.CraneCapacity;
import ee.liftertrans.persistence.entity.Vehicle;
import ee.liftertrans.persistence.repository.CraneCapacityRepository;
import ee.liftertrans.persistence.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ee.liftertrans.ai.VehicleAssessmentUnitConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class VehicleAssessmentService {

    private final CraneCapacityRepository craneCapacityRepository;
    private final VehicleRepository vehicleRepository;
    //ühendame 2 teenust - 1 võtab küsimusest kaalu ja teine küsib sõidukite andmed
    private final VehicleAssessmentInputService vehicleAssessmentInputService;
    private final VehicleMapper vehicleMapper;


    //Service tagastab sõiduki Id järgi leitud kraana mõõtepunktid
    public List<CraneCapacity> getCraneCapacityBy (Integer vehicleId) {
        return craneCapacityRepository.findCraneCapacitiesByVehicle_Id(vehicleId);
    }

    //küsime sõidukid, mis on staatusega aktiivne
    public List<Vehicle> getActiveVehicles() {
        return vehicleRepository.findVehiclesByStatus("ACTIVE");
    }

    //Hindab veose sobivust aktiivsetele sõidukitele kliendi küsimuse põhjal.
    public VehicleAssessmentResponseDto assessVehicle(String question) {
        //kutsub välja küsimuse ja salvestab selle muutujasse
        VehicleAssessmentInputDto vehicleAssessmentInputDto = vehicleAssessmentInputService.extractAssessmentInput(question);
        //kontrollime, kas AI leidis puuduvaid andmeid
        if (!vehicleAssessmentInputDto.getMissingInformation().isEmpty()) {
            // Puuduvate sisendandmete korral suuname päringu käsitsi ülevaatamisele.
            return new VehicleAssessmentResponseDto(
                    "NEEDS_MANUAL_REVIEW",
                    "Selle päringu hindamiseks vajalik info vajab käsitsi ülevaatamist.",
                    List.of(),
                    null,
                    vehicleAssessmentInputDto.getMissingInformation());

        }
        //küsime aktiivseid sõiduikeid andmebaasist
        List<Vehicle> activeVehicles = getActiveVehicles();

        //Teisendame veose kaalu kilogrammideks, et võrrelda seda sõidukite kandevõimega.
        BigDecimal cargoWeightKg = unitConverter.convertWeightToKilograms(
                vehicleAssessmentInputDto.getCargoWeight(),
                vehicleAssessmentInputDto.getCargoWeightUnit());

        //Teisendame veose pikkuse meetriteks
        BigDecimal cargoLengthM = unitConverter.convertLengthToMeters(
                vehicleAssessmentInputDto.getCargoLength(),
                vehicleAssessmentInputDto.getCargoLengthUnit());

        //teisendame veose laiuse meetriteks
        BigDecimal cargoWidthM = unitConverter.convertLengthToMeters(
                vehicleAssessmentInputDto.getCargoWidth(),
                vehicleAssessmentInputDto.getCargoWidthUnit());

        BigDecimal requestedLiftingHeightM = null;
        if (Boolean.TRUE.equals(vehicleAssessmentInputDto.getLiftingRequired())) {
            // Kraana mõõtepunktidega võrdlemiseks teisendame tõstekõrguse meetriteks.
            requestedLiftingHeightM = unitConverter.convertLengthToMeters(
                    vehicleAssessmentInputDto.getRequestedLiftingHeight(),
                    vehicleAssessmentInputDto.getRequestedLiftingHeightUnit());
        }

        //enne võrdlemist, teisendame veose pikkuse meetrid millimeetriteks
        BigDecimal cargoLengthMm = cargoLengthM.multiply(BigDecimal.valueOf(1000));

        //enne võrdlemist, teisendame veose laiuse meetrid millimeetriteks
        BigDecimal cargoWidthMm = cargoWidthM.multiply(BigDecimal.valueOf(1000));

        //loome sobivate sõidukite listi
        List<Vehicle> vehiclesWithinWeightCapacity = new ArrayList<>();

        //loome sõidukite listi, mis vajab käsitsi kontrolli
        List<Vehicle>vehiclesNeedingManualReview = new ArrayList<>();

        //kontrollime sobivaid sõiduikeid ükshaaval
        for (Vehicle vehicle: activeVehicles) {

            //võrdleme max kandevõimet ja veose kaalu
            //kui sõiduki kandevõime on veose kaaluga võrdne või suurem
            if (vehicle.getMaxCargoWeightKg().compareTo(cargoWeightKg) >= 0) {

                //Puuduva platvormi mõõdu korral ei saa sobivust kindlalt hinnata.
                if (vehicle.getPlatformLengthMm() == null
                        //Pikkuse ületamisel tuleb platvorm käsitsi üle kontrollida.
                        || cargoLengthMm.compareTo(vehicle.getPlatformLengthMm()) > 0
                        //Puuduva platvormi laiuse korral ei saa sobivust kindlalt hinnata.
                        || vehicle.getPlatformWidthMm() == null
                        //Laiuse ületamisel tuleb platvorm käsitsi üle kontrollida.
                        || cargoWidthMm.compareTo(vehicle.getPlatformWidthMm()) > 0) {
                    //Mõõtude tõttu käsitsi kontrolli vajava sõiduki lisame eraldi nimekirja.
                    vehiclesNeedingManualReview.add(vehicle);
                } else {
                    //Kandevõime ja mõlemad platvormi mõõdud sobivad.
                    vehiclesWithinWeightCapacity.add(vehicle);
                }
            }

        }

        // Teisendame sobivad sõidukid API vastuses kasutatavateks DTO-deks.
        List<VehicleAssessmentVehicleDto> suitableVehicleDtos =
                vehicleMapper.toVehicleAssessmentVehicleDtos(vehiclesWithinWeightCapacity);

        String status;
        if (!vehiclesNeedingManualReview.isEmpty() || suitableVehicleDtos.isEmpty()) {
            // Ebaselge mõõdu või automaatselt sobiva sõiduki puudumise korral kontrollib inimene päringu üle.
            status = "NEEDS_MANUAL_REVIEW";
        } else {
            // Vähemalt üks sõiduk sobib automaatse kontrolli alusel.
            status = "POSSIBLE";
        }

        // Kui tõstmine on vajalik, jääb hinnang käsitsi kinnitatavaks kuni mõõtepunkti leidmiseni.
        String craneAssessment = Boolean.FALSE.equals(vehicleAssessmentInputDto.getLiftingRequired())
                ? "NOT_REQUIRED"
                : "MANUAL_CONFIRMATION";

        if (Boolean.TRUE.equals(vehicleAssessmentInputDto.getLiftingRequired())) {
            for (Vehicle vehicle : vehiclesWithinWeightCapacity) {
                // Leiame sõiduki kraana mõõtepunktid enne tõstevõime hindamist.
                List<CraneCapacity> craneCapacities = getCraneCapacityBy(vehicle.getId());

                for (CraneCapacity craneCapacity : craneCapacities) {
                    // Arvestame ainult andmebaasis täpselt mõõdetud tõstekõrgusega.
                    if (craneCapacity.getReachM().compareTo(requestedLiftingHeightM) == 0) {
                        // Võrdleme veose kaalu täpse mõõtepunkti lubatud tõstevõimega.
                        if (craneCapacity.getMaxWeightKg().compareTo(cargoWeightKg) >= 0) {
                            craneAssessment = "WITHIN_MEASURED_CAPACITY";
                        } else {
                            craneAssessment = "EXCEEDS_MEASURED_CAPACITY";
                        }
                    }
                }
            }
        }

        if ("MANUAL_CONFIRMATION".equals(craneAssessment)) {
            // Täpse kraana mõõtepunkti puudumisel vajab kogu juhtum käsitsi kontrolli.
            status = "NEEDS_MANUAL_REVIEW";
        }

        // Koostame kliendile staatusele vastava eestikeelse selgituse.
        String answer;
        if ("NEEDS_MANUAL_REVIEW".equals(status)) {
            answer = "Veose sobivus vajab käsitsi üle kontrollimist.";
        } else if ("NOT_POSSIBLE".equals(status)) {
            answer = "Sobivat sõidukit ei leitud.";
        } else {
            answer = "Leidus automaatselt sobiv sõiduk; tulemus on esialgne eelhinnang.";
        }

        // Tagastame transpordi ja kraana eelhinnangu koos sobivate sõidukitega.
        return new VehicleAssessmentResponseDto(
                status,
                answer,
                suitableVehicleDtos,
                craneAssessment,
                List.of());

    }
    private final VehicleAssessmentUnitConverter unitConverter = new VehicleAssessmentUnitConverter();





}
