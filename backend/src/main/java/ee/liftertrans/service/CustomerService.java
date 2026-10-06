package ee.liftertrans.service;

import ee.liftertrans.dto.CustomerCreateRequestDto;
import ee.liftertrans.dto.CustomerDetailDto;
import ee.liftertrans.dto.CustomerDto;
import ee.liftertrans.infrastructure.exception.BusinessException;
import ee.liftertrans.infrastructure.exception.ErrorCode;
import ee.liftertrans.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.liftertrans.mapper.CustomerMapper;
import ee.liftertrans.persistence.entity.Customer;
import ee.liftertrans.persistence.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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

    @Transactional
    public CustomerDetailDto addCustomer(CustomerCreateRequestDto customerCreateRequestDto) {
        validateCompanyRegistrationNumberIsAvailable(customerCreateRequestDto.getCompanyRegistrationNumber());

        Customer customer = customerMapper.toCustomer(customerCreateRequestDto);
        customer.setCreatedAt(Instant.now());
        Customer savedCustomer = customerRepository.save(customer);

        return customerMapper.toCustomerDetailDto(savedCustomer);
    }

    // Registrikood on valikuline, seega kontrollime duplikaati ainult siis, kui see on sisestatud
    private void validateCompanyRegistrationNumberIsAvailable(String companyRegistrationNumber) {
        if (companyRegistrationNumber != null && !companyRegistrationNumber.isBlank()
                && customerRepository.existsCustomerByCompanyRegistrationNumber(companyRegistrationNumber)) {
            throw new BusinessException(ErrorCode.CUSTOMER_ALREADY_EXISTS);
        }
    }

    // Seda kasutab JobService uue tellimuse loomisel, et customerId järgi päris Customer kätte saada
    public Customer getValidCustomerBy(Integer customerId) {

        // Otsime kliendi ID järgi, kui ei leia, siis 404 (PRIMARY_KEY_NOT_FOUND)
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("customerId", customerId));
    }
}
