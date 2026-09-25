package ee.liftertrans.service;

import ee.liftertrans.dto.CustomerDto;
import ee.liftertrans.infrastructure.exception.BusinessException;
import ee.liftertrans.infrastructure.exception.ErrorCode;
import ee.liftertrans.mapper.CustomerMapper;
import ee.liftertrans.persistence.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public List<CustomerDto> getCustomers(String search) {
        if (search != null && search.trim().length() > 100) {
            throw new BusinessException(ErrorCode.INVALID_SEARCH_PARAMETER);
        }
        var customers = customerRepository.findBySearchTerm(search);
        return customerMapper.toCustomerDtos(customers);

    }
}
