package ee.liftertrans.controller;

import ee.liftertrans.dto.VehicleAssessmentResponseDto;
import ee.liftertrans.infrastructure.RestExceptionHandler;
import ee.liftertrans.infrastructure.VehicleAssessmentRateLimitInterceptor;
import ee.liftertrans.service.VehicleAssessmentRateLimiter;
import ee.liftertrans.service.VehicleAssessmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VehicleAssessmentControllerTest {

    @Mock
    private VehicleAssessmentService vehicleAssessmentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Seome päringu kontrolli ja valideerimise päris HTTP-kihi kaudu.
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        VehicleAssessmentRateLimitInterceptor interceptor =
                new VehicleAssessmentRateLimitInterceptor(new VehicleAssessmentRateLimiter());
        mockMvc = MockMvcBuilders.standaloneSetup(new VehicleAssessmentController(vehicleAssessmentService))
                .addInterceptors(interceptor)
                .setControllerAdvice(new RestExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void validQuestionReturnsAssessment() throws Exception {
        // Kehtiv küsimus jõuab teenusesse ja tagastab selle hinnangu.
        when(vehicleAssessmentService.assessVehicle("küsimus"))
                .thenReturn(new VehicleAssessmentResponseDto(
                        "NEEDS_MANUAL_REVIEW", "Kontrollime päringu üle.", List.of(), null, List.of()));

        mockMvc.perform(post("/api/vehicle-assessment")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"küsimus\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NEEDS_MANUAL_REVIEW"));

        verify(vehicleAssessmentService).assessVehicle("küsimus");
    }

    @Test
    void blankQuestionReturnsBadRequestWithoutCallingService() throws Exception {
        // Tühi küsimus lükatakse tagasi enne teenuse väljakutset.
        mockMvc.perform(post("/api/vehicle-assessment")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_INPUT"));

        verifyNoInteractions(vehicleAssessmentService);
    }

    @Test
    void sixthRequestFromSameAddressReturnsTooManyRequests() throws Exception {
        // Kuues päring minutis saab 429 ning ütleb, mitu sekundit oodata.
        when(vehicleAssessmentService.assessVehicle("küsimus"))
                .thenReturn(new VehicleAssessmentResponseDto(
                        "POSSIBLE", "Eelhinnang valmis.", List.of(), "NOT_REQUIRED", List.of()));

        for (int request = 0; request < 5; request++) {
            mockMvc.perform(post("/api/vehicle-assessment")
                            .contentType(APPLICATION_JSON)
                            .content("{\"question\":\"küsimus\"}")
                            .with(requestBuilder -> {
                                requestBuilder.setRemoteAddr("192.0.2.5");
                                return requestBuilder;
                            }))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(post("/api/vehicle-assessment")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"küsimus\"}")
                        .with(requestBuilder -> {
                            requestBuilder.setRemoteAddr("192.0.2.5");
                            return requestBuilder;
                        }))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"))
                .andExpect(jsonPath("$.errorCode").value("TOO_MANY_REQUESTS"));

        verify(vehicleAssessmentService, org.mockito.Mockito.times(5)).assessVehicle("küsimus");
    }
}
