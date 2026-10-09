package ee.liftertrans.ai;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;



public class VehicleAssessmentUnitConverterTest {
    @Test
    void teisendabTonnidKilogrammideks() {
        VehicleAssessmentUnitConverter converter = new VehicleAssessmentUnitConverter();

        BigDecimal kaal = new BigDecimal("2");
        BigDecimal tulemus = converter.convertWeightToKilograms(kaal, "t");
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("2000"), tulemus);

    }

    @Test
    void teisendabGrammidKilogrammideks() {
        VehicleAssessmentUnitConverter converter = new VehicleAssessmentUnitConverter();

        BigDecimal kaal = new BigDecimal("500");
        BigDecimal tulemus = converter.convertWeightToKilograms(kaal, "g");
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("0.5"), tulemus);


    }

    @Test
    void teisendabSentimeetridMeetriteks() {
        VehicleAssessmentUnitConverter converter = new VehicleAssessmentUnitConverter();

        BigDecimal pikkus = new BigDecimal("250");
        BigDecimal tulemus = converter.convertLengthToMeters(pikkus, "cm");
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("2.5"), tulemus);
    }

    @Test
    void jätabKilogrammidSamaks() {
        VehicleAssessmentUnitConverter converter = new VehicleAssessmentUnitConverter();
        BigDecimal kaal = new BigDecimal("3.5");
        BigDecimal tulemus = converter.convertWeightToKilograms(kaal, "kg");
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("3.5"), tulemus);

    }

    @Test
    void teisendabMillimeetridMeetriteks() {
        VehicleAssessmentUnitConverter converter = new VehicleAssessmentUnitConverter();
        BigDecimal pikkus = new BigDecimal("2500");
        BigDecimal tulemus = converter.convertLengthToMeters(pikkus, "mm");
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("2.5"), tulemus);
    }

    @Test
    void jatabMeetridSamaks() {
        VehicleAssessmentUnitConverter converter = new VehicleAssessmentUnitConverter();
        BigDecimal pikkus = new BigDecimal("2.5");
        BigDecimal tulemus = converter.convertLengthToMeters(pikkus, "m");
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("2.5"), tulemus);
    }

    @Test
    void tundmatuKaaluuhikuKorralVisatakseErind() {
        VehicleAssessmentUnitConverter converter = new VehicleAssessmentUnitConverter();
        BigDecimal kaal = new BigDecimal("5");

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertWeightToKilograms(kaal, "lb")
        );
    }

    @Test
    void tundmatuPikkuseuhikuKorralVisatakseErind() {
        VehicleAssessmentUnitConverter converter = new VehicleAssessmentUnitConverter();
        BigDecimal pikkus = new BigDecimal("5");

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertLengthToMeters(pikkus, "in")
        );
    }
}
