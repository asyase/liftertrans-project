package ee.liftertrans.controller;

import ee.liftertrans.dto.CustomerDto;
import ee.liftertrans.infrastructure.error.ApiError;
import ee.liftertrans.service.CustomerService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
}
