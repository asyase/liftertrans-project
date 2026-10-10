package ee.liftertrans.service;


import ee.liftertrans.ai.VehicleAssessmentInputDto;
import ee.liftertrans.dto.VehicleAssessmentResponseDto;
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
            //puuduva info korral vastus: staatus, selgitus, tühi sõidukite loend, null kraanahinnang ja puuduva info nimekiri.
            return new VehicleAssessmentResponseDto("NEED_MORE_INFORMATION", "Palun täpsusta puuduvaid andmeid." , List.of(), null, vehicleAssessmentInputDto.getMissingInformation());

        }
        //küsime aktiivseid sõiduikeid andmebaasist
        List<Vehicle> activeVehicles = getActiveVehicles();

        //Teisendame veose kaalu kilogrammideks, et võrrelda seda sõidukite kandevõimega.
        BigDecimal cargoWeightKg = unitConverter.convertWeightToKilograms(
                vehicleAssessmentInputDto.getCargoWeight(),
                vehicleAssessmentInputDto.getCargoWeightUnit());

        List<Vehicle> suitableVehicles = new ArrayList<>();







    }
    private final VehicleAssessmentUnitConverter unitConverter = new VehicleAssessmentUnitConverter();





}
