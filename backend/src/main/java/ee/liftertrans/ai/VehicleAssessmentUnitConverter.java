package ee.liftertrans.ai;

import java.math.BigDecimal;

public class VehicleAssessmentUnitConverter {
    //sisend on kaalu number ja ühik
    public BigDecimal convertWeightToKilograms(BigDecimal weight, String unit){
        //kui sisend ok kg, siis võrdleme kg ja tagastame kg
        if (unit.equals("kg")) {
            return weight;
        }

        //kui sisend on tonn, korrutame 1000-ga
        if (unit.equals("t")) {
            return weight.multiply(BigDecimal.valueOf(1000));

        }
        //kui sisend on g, siis jagame 1000-ga
        if (unit.equals("g")) {
            return weight.divide(BigDecimal.valueOf(1000));
        }

            //kui sisend on tundmatu, siis anname veateate
            throw new IllegalArgumentException("Ühik pole toetatud");


    }
    //sisend on pikkuse number ja ühik
    public BigDecimal convertLengthToMeters(BigDecimal length, String unit) {
        //kui sisend on meeter, siis võrdleme meetriga ja tagastame meeter
        if (unit.equals("m")) {
            return length;
        }
        //kui sisend on cm, jagame 100ga
        if (unit.equals("cm")) {
            return length.divide(BigDecimal.valueOf(100));
        }
        //kui sisend on mm, siis jagame 1000ga
        if (unit.equals("mm")) {
            return length.divide(BigDecimal.valueOf(1000));
        }
        //kui sisend on tundmatu, siis anname veateate
        throw new IllegalArgumentException("Ühik pole toetatud");

    }





}
