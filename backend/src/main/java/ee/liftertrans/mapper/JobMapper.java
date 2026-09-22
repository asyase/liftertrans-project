package ee.liftertrans.mapper;
import org.mapstruct.Mapping;
import ee.liftertrans.dto.JobListDto;
import ee.liftertrans.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface JobMapper {


    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "vehicle.registrationNumber", target = "vehicleRegistrationNumber")
    @Mapping(source = "driver.name", target = "driverName")
    @Mapping(source = "subcontractor.companyName", target = "subcontractorName")
    JobListDto toJobListDto(Job job);

    List<JobListDto> toJobListDtos(List<Job> jobs);
}