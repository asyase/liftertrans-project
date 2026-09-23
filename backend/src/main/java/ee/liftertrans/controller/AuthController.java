package ee.liftertrans.controller;

import ee.liftertrans.controller.dto.AuthResponseDto;
import ee.liftertrans.infrastructure.error.ApiError;
import ee.liftertrans.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @RequestBody
    @Valid
    @Operation(summary = "Sisse logimine.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ebaõnnestus",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Vale e-post/parool",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public AuthResponseDto login(@RequestBody @Valid AuthRequestDto authRequestDto){
        return authService.login(authRequestDto);
    }

}

