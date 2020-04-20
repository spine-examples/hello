package io.spine.helloworld.server.hello;

import io.spine.helloworld.hello.command.Print;
import io.spine.helloworld.hello.event.Printed;
import io.spine.testing.server.blackbox.BlackBoxContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.testing.TestValues.randomString;

@DisplayName("Hello Context should")
class HelloContextTest {

    private BlackBoxContext context;

    @BeforeEach
    void setUp() {
        context = BlackBoxContext.from(HelloContext.newBuilder());
    }

    @Nested
    @DisplayName("handle the `Print` command")
    class PrintCommand {

        private Print command;

        @BeforeEach
        void sendCommand() {
            command = Print
                    .newBuilder()
                    .setUsername(randomString())
                    .setText(randomString())
                    .vBuild();
            context.receivesCommand(command);
        }

        @Test
        @DisplayName("updating the `Console` entity")
        void entity() {
            Output expected = Output
                    .newBuilder()
                    .setUsername(command.getUsername())
                    .addLines(command.getText())
                    .vBuild();
            context.assertState(command.getUsername(), expected);
        }

        @Test
        @DisplayName("emitting the `Printed` event")
        void event() {
            Printed expected = Printed
                    .newBuilder()
                    .setUsername(command.getUsername())
                    .setText(command.getText())
                    .build();
            context.assertEvent(expected);
        }
    }
}
