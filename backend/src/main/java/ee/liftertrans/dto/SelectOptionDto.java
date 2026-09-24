package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Valikuvälja (select) üks rida: value läheb backendi, text näidatakse kasutajale
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectOptionDto {
    private String value;
    private String text;
}
