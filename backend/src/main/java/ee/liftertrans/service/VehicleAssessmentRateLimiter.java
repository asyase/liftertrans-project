package ee.liftertrans.service;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongSupplier;

@Component
public class VehicleAssessmentRateLimiter {

    private static final long MINUTE_NANOS = 60_000_000_000L;
    private static final long HOUR_NANOS = 3_600_000_000_000L;
    private static final int MINUTE_LIMIT = 5;
    private static final int HOUR_LIMIT = 30;
    private static final int CLEANUP_INTERVAL = 256;

    private final LongSupplier nanoTime;
    private final Map<String, RequestHistory> requestHistoryByAddress = new HashMap<>();
    private int requestsSinceCleanup;

    public VehicleAssessmentRateLimiter() {
        this(System::nanoTime);
    }

    VehicleAssessmentRateLimiter(LongSupplier nanoTime) {
        this.nanoTime = nanoTime;
    }

    // Lubatud päringu korral tagastab 0, piirangu korral ooteaja sekundites.
    public synchronized long tryAcquire(String clientAddress) {
        long now = nanoTime.getAsLong();
        RequestHistory history = requestHistoryByAddress.computeIfAbsent(
                clientAddress, ignored -> new RequestHistory());
        history.removeExpiredRequests(now);
        history.lastAccessNanos = now;

        long retryAfterNanos = 0;
        if (history.minuteRequests.size() >= MINUTE_LIMIT) {
            retryAfterNanos = Math.max(retryAfterNanos,
                    MINUTE_NANOS - (now - history.minuteRequests.peekFirst()));
        }
        if (history.hourRequests.size() >= HOUR_LIMIT) {
            retryAfterNanos = Math.max(retryAfterNanos,
                    HOUR_NANOS - (now - history.hourRequests.peekFirst()));
        }

        if (retryAfterNanos <= 0) {
            history.minuteRequests.addLast(now);
            history.hourRequests.addLast(now);
        }

        if (++requestsSinceCleanup >= CLEANUP_INTERVAL) {
            removeInactiveAddresses(now);
            requestsSinceCleanup = 0;
        }

        return retryAfterNanos <= 0 ? 0 : Math.max(1,
                (retryAfterNanos + 999_999_999L) / 1_000_000_000L);
    }

    private void removeInactiveAddresses(long now) {
        requestHistoryByAddress.entrySet().removeIf(entry ->
                now - entry.getValue().lastAccessNanos >= HOUR_NANOS);
    }

    private static class RequestHistory {
        private final ArrayDeque<Long> minuteRequests = new ArrayDeque<>();
        private final ArrayDeque<Long> hourRequests = new ArrayDeque<>();
        private long lastAccessNanos;

        private void removeExpiredRequests(long now) {
            removeExpiredRequests(minuteRequests, now, MINUTE_NANOS);
            removeExpiredRequests(hourRequests, now, HOUR_NANOS);
        }

        private void removeExpiredRequests(ArrayDeque<Long> requests, long now, long windowNanos) {
            while (!requests.isEmpty() && now - requests.peekFirst() >= windowNanos) {
                requests.removeFirst();
            }
        }
    }
}
