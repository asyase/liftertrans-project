package ee.liftertrans.controller;

import ee.liftertrans.dto.CustomerDto;
import ee.liftertrans.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;


    @GetMapping
    public List<CustomerDto> getCustomers(@RequestParam(required = false) String search) {
        return customerService.getCustomers(search);
    }
}
