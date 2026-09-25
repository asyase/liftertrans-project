package ee.liftertrans.mapper;
import ee.liftertrans.dto.JobCreateResponseDto;
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

}