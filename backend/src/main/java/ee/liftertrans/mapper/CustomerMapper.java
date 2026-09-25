package ee.liftertrans.mapper;

import ee.liftertrans.dto.CustomerListDto;
import ee.liftertrans.persistence.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface CustomerMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "companyName", target = "companyName")
    @Mapping(source = "companyRegistrationNumber", target = "companyRegistrationNumber")
    @Mapping(source = "vatNumber", target = "vatNumber")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "invoiceEmail", target = "invoiceEmail")
    @Mapping(source = "phone", target = "phone")

    CustomerListDto toCustomerDto(Customer customer);

    List<CustomerListDto> toCustomerDtos(List<Customer> customers);
}