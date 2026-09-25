package ee.liftertrans.controller;

import ee.liftertrans.dto.CustomerListDto;
import ee.liftertrans.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")

public class CustomerController {
    private final CustomerService customerService;


    @GetMapping
    public List<CustomerListDto> getCustomers(@RequestParam(required = false)String search){
        return customerService.getCustomers(search);
    }
}
