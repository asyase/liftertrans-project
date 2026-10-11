package ee.liftertrans.infrastructure;

import ee.liftertrans.infrastructure.exception.TooManyRequestsException;
import ee.liftertrans.service.VehicleAssessmentRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class VehicleAssessmentRateLimitInterceptor implements HandlerInterceptor {

    private final VehicleAssessmentRateLimiter rateLimiter;

    public VehicleAssessmentRateLimitInterceptor(VehicleAssessmentRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        // Kasutame ühenduse aadressi ega usalda kliendi saadetud IP-päiseid.
        long retryAfterSeconds = rateLimiter.tryAcquire(request.getRemoteAddr());
        if (retryAfterSeconds > 0) {
            throw new TooManyRequestsException(retryAfterSeconds);
        }
        return true;
    }
}
