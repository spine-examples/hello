package io.spine.helloworld.server.hello;

import io.spine.helloworld.hello.command.Print;
import io.spine.helloworld.hello.event.Printed;
import io.spine.testing.server.EventSubject;
import io.spine.testing.server.blackbox.BlackBoxBoundedContext;
import io.spine.testing.server.entity.EntitySubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.testing.TestValues.randomString;

@DisplayName("Hello Context should")
class HelloContextTest {

    private BlackBoxBoundedContext<?> context;

    @BeforeEach
    void setUp() {
        context = BlackBoxBoundedContext.from(HelloContext.newBuilder());
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
            EntitySubject assertEntity = context.assertEntity(Console.class, command.getUsername());

            assertEntity.exists();

            Output expectedState = Output
                    .newBuilder()
                    .setUsername(command.getUsername())
                    .addLines(command.getText())
                    .vBuild();

            assertEntity.hasStateThat()
                        .comparingExpectedFieldsOnly()
                        .isEqualTo(expectedState);
        }

        @Test
        @DisplayName("emitting the `Printed` event")
        void event() {
            EventSubject assertEvents = context.assertEvents().withType(Printed.class);

            assertEvents.hasSize(1);

            Printed expectedEvent = Printed
                    .newBuilder()
                    .setUsername(command.getUsername())
                    .setText(command.getText())
                    .build();

            assertEvents.message(0)
                        .comparingExpectedFieldsOnly()
                        .isEqualTo(expectedEvent);
        }
    }
}
