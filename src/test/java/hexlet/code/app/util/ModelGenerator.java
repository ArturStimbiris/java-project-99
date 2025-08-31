package hexlet.code.app.util;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

import hexlet.code.app.model.User;

@Component
public class ModelGenerator {
    public Model<User> getUserModel() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getEmail), () -> "test" + System.currentTimeMillis() + "@example.com")
                .supply(Select.field(User::getFirstName), () -> "Test")
                .supply(Select.field(User::getLastName), () -> "Testov")
                .toModel();
    }
}
