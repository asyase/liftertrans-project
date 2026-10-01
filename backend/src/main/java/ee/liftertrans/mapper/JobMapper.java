package ee.liftertrans.mapper;
import ee.liftertrans.dto.JobCreateResponseDto;
import ee.liftertrans.dto.JobDetailDto;
import org.mapstruct.Mapping;
import ee.liftertrans.dto.JobDto;
import ee.liftertrans.persistence.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface JobMapper {

    // Job -> JobListDto

    // VIGA OLI: see rida puudus ja nimekirjas oli jobId alati null (ID veerg tühi).
    // Entity-s on väli id, DTO-s jobId — nimed on erinevad, seega MapStruct ise neid ei seo.
    // Hoiatust ka ei tulnud, sest unmappedTargetPolicy = IGNORE.
    @Mapping(source = "id", target = "jobId")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "vehicle.registrationNumber", target = "vehicleRegistrationNumber")
    @Mapping(source = "driver.name", target = "driverName")
    @Mapping(source = "subcontractor.companyName", target = "subcontractorName")
    @Mapping(source = "plannedStartTime", target = "plannedStartTime")
    JobDto toJobListDto(Job job);

    List<JobDto> toJobDtos(List<Job> jobs);

    @Mapping(source = "id", target = "jobId")
    JobCreateResponseDto toJobCreateResponseDto(Job job);

    List<JobDto> toJobListDtos(List<Job> jobs);

    // Job -> JobDetailDto (üks töö detailvaate jaoks)
    // Samanimelised väljad (status, pickupAddress jne) seob MapStruct ise,
    // siia kirjutan ainult need, mis tulevad kliendi, auto, juhi või alltöövõtja küljest
    @Mapping(source = "id", target = "jobId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.companyName", target = "customerCompanyName")
    @Mapping(source = "customer.phone", target = "customerPhone")
    @Mapping(source = "customer.email", target = "customerEmail")
    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "vehicle.registrationNumber", target = "vehicleRegistrationNumber")
    @Mapping(source = "vehicle.name", target = "vehicleName")
    @Mapping(source = "driver.id", target = "driverId")
    @Mapping(source = "driver.name", target = "driverName")
    @Mapping(source = "subcontractor.id", target = "subcontractorId")
    @Mapping(source = "subcontractor.companyName", target = "subcontractorName")
    JobDetailDto toJobDetailDto(Job job);

}