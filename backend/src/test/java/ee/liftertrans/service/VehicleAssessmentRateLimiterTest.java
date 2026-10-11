package ee.liftertrans.service;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleAssessmentRateLimiterTest {

    private static final long SECOND_NANOS = 1_000_000_000L;

    @Test
    void limitsEachAddressToFiveRequestsPerMinute() {
        AtomicLong clock = new AtomicLong();
        VehicleAssessmentRateLimiter limiter = new VehicleAssessmentRateLimiter(clock::get);

        for (int request = 0; request < 5; request++) {
            assertEquals(0, limiter.tryAcquire("192.0.2.1"));
        }
        assertEquals(60, limiter.tryAcquire("192.0.2.1"));

        clock.addAndGet(60 * SECOND_NANOS);
        assertEquals(0, limiter.tryAcquire("192.0.2.1"));
        assertEquals(0, limiter.tryAcquire("192.0.2.2"));
    }

    @Test
    void limitsEachAddressToThirtyRequestsPerHour() {
        AtomicLong clock = new AtomicLong();
        VehicleAssessmentRateLimiter limiter = new VehicleAssessmentRateLimiter(clock::get);

        for (int request = 0; request < 30; request++) {
            assertEquals(0, limiter.tryAcquire("192.0.2.1"));
            clock.addAndGet(61 * SECOND_NANOS);
        }

        assertTrue(limiter.tryAcquire("192.0.2.1") > 0);
    }
}
