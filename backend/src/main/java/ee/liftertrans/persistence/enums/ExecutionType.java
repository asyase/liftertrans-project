package ee.liftertrans.persistence.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

// Teostamise viisid. Uue viisi lisamiseks lisa see siia ja andmebaasi CHECK constrainti (JOB_execution_type_ck)
@Getter
@RequiredArgsConstructor
public enum ExecutionType {

    INTERNAL("Oma transport"),
    SUBCONTRACTED("Alltöövõtja");

    private final String text;

    public static boolean isValid(String value) {
        return Arrays.stream(values()).anyMatch(executionType -> executionType.name().equals(value));
    }
}
