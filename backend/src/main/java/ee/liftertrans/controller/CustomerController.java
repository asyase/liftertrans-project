package ee.liftertrans.controller;

import ee.liftertrans.dto.CustomerDto;
import ee.liftertrans.infrastructure.error.ApiError;
import ee.liftertrans.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@ApiResponses
public class CustomerController {
    private final CustomerService customerService;


    @GetMapping
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Töö on edukalt loodud"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sisendandmed on vigased",
                    content = @Content(schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "INVALID_SEARCH_PARAMETER",
                                    value = """
                                            {
                                              "message": "Otsingu parameeter on vigane",
                                              "errorCode": "INVALID_SEARCH_PARAMETER"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Kasutaja pole sisse logitud",
                    content = @Content(schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "UNAUTHORIZED",
                                    value = """
                                            {
                                              "message": "Kasutaja ei ole sisse logitud",
                                              "errorCode": "UNAUTHORIZED"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Kasutajat ei leitud",
                    content = @Content(schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "ACCESS_DENIED",
                                    value = """
                                            {
                                              "message": "Kasutajal puudub ligipääs",
                                              "errorCode": "ACCESS_DENIED"
                                            }
                                            """
                            )
                    )
            )
    })
    public List<CustomerDto> getCustomers(@RequestParam(required = false) String search) {
        return customerService.getCustomers(search);
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "Kliendi kustutamine (ainult kui temaga ei ole seotud töid)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Klient on kustutatud"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Kliendiga on seotud töid, teda ei saa kustutada",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "CUSTOMER_IN_USE",
                                    value = """
                                            {
                                              "message": "Klienti ei saa kustutada, kuna temaga on seotud töid",
                                              "errorCode": "CUSTOMER_IN_USE"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Klienti ei leitud",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "PRIMARY_KEY_NOT_FOUND",
                                    value = """
                                            {
                                              "message": "Ei leidnud primary keyd 'customerId' väärtusega: 99",
                                              "errorCode": "PRIMARY_KEY_NOT_FOUND"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok().build();
    }
}
