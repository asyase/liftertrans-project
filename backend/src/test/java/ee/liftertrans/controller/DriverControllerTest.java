package ee.liftertrans.controller;

import ee.liftertrans.infrastructure.RestExceptionHandler;
import ee.liftertrans.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DriverControllerTest {

    @Mock
    private DriverService driverService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new DriverController(driverService))
                .setControllerAdvice(new RestExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createDriverReturnsOkWithoutResponseBody() throws Exception {
        mockMvc.perform(post("/api/drivers")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Mart Tamm",
                                  "phone": "+3725551111",
                                  "email": "mart.tamm@liftertrans.ee",
                                  "active": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(driverService).createDriver(argThat(driverRequestDto ->
                "Mart Tamm".equals(driverRequestDto.getName())
                        && "+3725551111".equals(driverRequestDto.getPhone())
                        && "mart.tamm@liftertrans.ee".equals(driverRequestDto.getEmail())
                        && Boolean.TRUE.equals(driverRequestDto.getActive())));
    }

    @Test
    void createDriverRejectsBlankNameWithoutSaving() throws Exception {
        mockMvc.perform(post("/api/drivers")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "phone": "+3725551111",
                                  "active": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_INPUT"))
                .andExpect(jsonPath("$.message").value("name: must not be blank"));

        verifyNoInteractions(driverService);
    }

    @Test
    void createDriverRejectsBlankPhoneWithoutSaving() throws Exception {
        mockMvc.perform(post("/api/drivers")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Mart Tamm",
                                  "phone": "",
                                  "active": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_INPUT"))
                .andExpect(jsonPath("$.message").value("phone: must not be blank"));

        verifyNoInteractions(driverService);
    }

    @Test
    void createDriverAllowsMissingEmail() throws Exception {
        mockMvc.perform(post("/api/drivers")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Mart Tamm",
                                  "phone": "+3725551111",
                                  "active": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(driverService).createDriver(argThat(driverRequestDto ->
                driverRequestDto.getEmail() == null));
    }

    @Test
    void updateDriverReturnsOkWithoutResponseBody() throws Exception {
        mockMvc.perform(put("/api/drivers/1")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Mart Tamm",
                                  "phone": "+3725551111",
                                  "email": "mart.tamm@liftertrans.ee",
                                  "active": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(driverService).updateDriver(eq(1), argThat(driverRequestDto ->
                "Mart Tamm".equals(driverRequestDto.getName())
                        && "+3725551111".equals(driverRequestDto.getPhone())
                        && "mart.tamm@liftertrans.ee".equals(driverRequestDto.getEmail())
                        && Boolean.TRUE.equals(driverRequestDto.getActive())));
    }

    @Test
    void updateDriverRejectsBlankNameWithoutSaving() throws Exception {
        mockMvc.perform(put("/api/drivers/1")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "phone": "+3725551111",
                                  "active": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_INPUT"))
                .andExpect(jsonPath("$.message").value("name: must not be blank"));

        verifyNoInteractions(driverService);
    }
}
