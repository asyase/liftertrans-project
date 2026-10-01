package ee.liftertrans.dto;

// Ühised väljad, mida töö loomise ja muutmise valideerimine vajab.
// Nii saavad JobCreateRequestDto ja JobUpdateRequestDto kasutada samu valideerimismeetodeid.
public interface JobRequest {

    String getJobType();

    String getExecutionType();

    Integer getDriverId();

    Integer getSubcontractorId();

    String getPickupAddress();

    String getDeliveryAddress();

    String getServiceAddress();
}
