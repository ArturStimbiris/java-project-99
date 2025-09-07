package hexlet.code.app.controller;

import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SentryTestController {

    @GetMapping("/sentry-test")
    public String triggerError() {
        try {
            throw new Exception("This is a test exception for Sentry.");
        } catch (Exception e) {
            Sentry.captureException(e);
            return "Exception caught and sent to Sentry! Check your Sentry dashboard.";
        }
    }

    @GetMapping("/sentry-test-2")
    public String triggerAnotherError() {
        try {
            int[] numbers = new int[5];
            int value = numbers[10]; // ArrayIndexOutOfBoundsException
            return "This should not be reached";
        } catch (Exception e) {
            Sentry.captureException(e);
            return "Array index error sent to Sentry! Check your Sentry dashboard.";
        }
    }
}
