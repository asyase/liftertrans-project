package ee.liftertrans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiAskRequestDto {

    @NotBlank(message = "küsimus ei tohi olla tühi")
    @Size(max = 500, message = "küsimus võib olla kuni 500 tähemärki")
    private String question;
}
