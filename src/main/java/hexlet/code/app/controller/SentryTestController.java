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
            return "Exception caught and sent to Sentry!";
        }
    }
}
