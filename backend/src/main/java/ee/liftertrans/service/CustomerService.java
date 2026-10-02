package ee.liftertrans.service;

import ee.liftertrans.dto.CustomerDto;
import ee.liftertrans.infrastructure.exception.BusinessException;
import ee.liftertrans.infrastructure.exception.ErrorCode;
import ee.liftertrans.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.liftertrans.mapper.CustomerMapper;
import ee.liftertrans.persistence.entity.Customer;
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

    // Seda kasutab JobService uue tellimuse loomisel, et customerId järgi päris Customer kätte saada
    public Customer getValidCustomerBy(Integer customerId) {

        // Otsime kliendi ID järgi, kui ei leia, siis 404 (PRIMARY_KEY_NOT_FOUND)
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("customerId", customerId));
    }

    @Transactional
    public CustomerDetailDto createCustomer(CustomerCreateRequestDto requestDto) {
        if (customerRepository.existsByCompanyRegistrationNumber(requestDto.getCompanyRegistrationNumber())) {
            throw new CustomerAlreadyExistsException("Sellise registrikoodiga klient on juba olemas");
        }

        Customer customer = Customer.builder()
                .name(requestDto.getName())
                .companyName(requestDto.getCompanyName())
                .companyRegistrationNumber(requestDto.getCompanyRegistrationNumber())
                .vatNumber(requestDto.getVatNumber())
                .email(requestDto.getEmail())
                .invoiceEmail(requestDto.getInvoiceEmail())
                .phone(requestDto.getPhone())
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        return CustomerDetailDto.builder()
                .id(savedCustomer.getId())
                .name(savedCustomer.getName())
                .companyName(savedCustomer.getCompanyName())
                .companyRegistrationNumber(savedCustomer.getCompanyRegistrationNumber())
                .vatNumber(savedCustomer.getVatNumber())
                .email(savedCustomer.getEmail())
                .invoiceEmail(savedCustomer.getInvoiceEmail())
                .phone(savedCustomer.getPhone())
                .build();
    }
}
}
