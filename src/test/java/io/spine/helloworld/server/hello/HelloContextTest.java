/*
 * Copyright 2020, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.helloworld.server.hello;

import io.spine.helloworld.hello.command.Print;
import io.spine.helloworld.hello.event.Printed;
import io.spine.server.BoundedContextBuilder;
import io.spine.testing.server.blackbox.ContextAwareTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.testing.TestValues.randomString;

@DisplayName("Hello Context should")
class HelloContextTest extends ContextAwareTest {

    @Override
    protected BoundedContextBuilder contextBuilder() {
        return HelloContext.newBuilder();
    }

    @Nested
    @DisplayName("handle the `Print` command")
    class PrintCommand {

        private Print command;

        @BeforeEach
        void sendCommand() {
            command = Print.newBuilder()
                    .setUsername(randomString())
                    .setText(randomString())
                    .vBuild();
            context().receivesCommand(command);
        }

        @Test @DisplayName("emitting the `Printed` event")
        void event() {
            Printed expected = Printed.newBuilder()
                    .setUsername(command.getUsername())
                    .setText(command.getText())
                    .build();
            context().assertEvent(expected);
        }

        @Test @DisplayName("updating the `Console` entity")
        void entity() {
            Output expected = Output.newBuilder()
                    .setUsername(command.getUsername())
                    .addLines(command.getText())
                    .vBuild();
            context().assertState(command.getUsername(), expected);
        }
    }
}
