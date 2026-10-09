package ee.liftertrans.controller;

import ee.liftertrans.ai.AiAskRequestDto;
import ee.liftertrans.ai.AiAskResponseDto;
import ee.liftertrans.service.NlToSqlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AiController {

    private final NlToSqlService nlToSqlService;

    @PostMapping("/ask")
    @Operation(
            summary = "Küsimus loomulikus keeles. AI teeb sellest SQL päringu, käivitab selle ja võtab tulemuse kokku"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vastus tagastati edukalt"),
            @ApiResponse(responseCode = "400", description = "Küsimus on vigane või sellele ei saa andmebaasi põhjal vastata")
    })
    public AiAskResponseDto ask(@Valid @RequestBody AiAskRequestDto aiAskRequestDto) {
        return nlToSqlService.ask(aiAskRequestDto.getQuestion());
    }
}
