package ee.liftertrans.service;


import ee.liftertrans.persistence.entity.CraneCapacity;
import ee.liftertrans.persistence.repository.CraneCapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
@RequiredArgsConstructor
public class VehicleAssessmentService {

    private final CraneCapacityRepository craneCapacityRepository;

    //Service tagastab sõiduki Id järgi leitud kraana mõõtepunktid
    public List<CraneCapacity> getCraneCapacityBy (Integer vehicleId) {
        return craneCapacityRepository.findCraneCapacitiesByVehicle_Id(vehicleId);


    }
}
