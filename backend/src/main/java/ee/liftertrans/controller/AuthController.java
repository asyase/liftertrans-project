package ee.liftertrans.controller;


import ee.liftertrans.infrastructure.error.ApiError;
import ee.liftertrans.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
@RestController
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;
@PostMapping("/api/auth/login")
@Operation(
        summary="Sisse logimine.Tagastab userId, email, roleName ja driverId"
)

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description= "OK"
            ),
            @ApiResponse(
                    responseCode="400",
                    description= "Vigased siseandmed",
                    content = @Content( schema = @Schema(implementation = ApiError.class)
                    )

            ),

    @ApiResponse(
            responseCode="401",
            description= "Vale e-post või parool",
            content = @Content( schema = @Schema(implementation = ApiError.class)
            )
    )

            })
    public void login()  {

    }

}
