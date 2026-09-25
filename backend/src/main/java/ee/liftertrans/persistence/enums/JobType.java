package ee.liftertrans.persistence.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

// Töö tüübid. Uue tüübi lisamiseks lisa see siia ja andmebaasi CHECK constrainti (JOB_job_type_ck)
@Getter
@RequiredArgsConstructor
public enum JobType {

    TRANSPORT_AND_CRANE("Transport ja kraana"),
    CRANE_ONLY("Ainult kraana");

    private final String text;

    public static boolean isValid(String value) {
        return Arrays.stream(values()).anyMatch(jobType -> jobType.name().equals(value));
    }
}
