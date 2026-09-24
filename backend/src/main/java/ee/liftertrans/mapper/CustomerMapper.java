package ee.liftertrans.mapper;


import ee.liftertrans.dto.CustomerListDto;
import ee.liftertrans.persistence.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface CustomerMapper {

    CustomerListDto toCustomerDto(Customer customer);

    List<CustomerListDto> toCustomerDtos(List<Customer> customers);
}
