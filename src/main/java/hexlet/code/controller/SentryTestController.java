package hexlet.code.controller;

import hexlet.code.exception.SentryTestException;
import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SentryTestController {

    @GetMapping("/sentry-test")
    public String triggerError() {
        try {
            throw new SentryTestException("This is a test exception for Sentry.");
        } catch (SentryTestException e) {
            Sentry.captureException(e);
            return "Exception caught and sent to Sentry! Check your Sentry dashboard.";
        }
    }
}
