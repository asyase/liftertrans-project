package ee.liftertrans.service;

import ee.liftertrans.dto.CustomerListDto;
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

    public List<CustomerListDto> getCustomers(String search) {
        var customers = customerRepository.findBySearchTerm(search);
        return customerMapper.toCustomerDtos(customers);
    }
}
