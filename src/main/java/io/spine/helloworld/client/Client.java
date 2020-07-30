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

package io.spine.helloworld.client;

import io.spine.base.EventMessage;
import io.spine.helloworld.hello.command.Print;
import io.spine.helloworld.hello.event.Printed;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.client.Client.inProcess;
import static io.spine.json.Json.toCompactJson;
import static java.lang.String.format;

/**
 * A simple client that sends the {@link Print} command to the Hello server, subscribes to
 * the resulting events, and prints them as they arrive.
 */
public final class Client {

    private final io.spine.client.Client client;

    public Client(String serverName) {
        checkNotNull(serverName);
        this.client = inProcess(serverName)
                .shutdownTimout(2, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Sends the {@link Print} command to the server subscribing to resulting event.
     */
    public void sendCommand() {
        String userName = System.getProperty("user.name");
        Print commandMessage = Print
                .newBuilder()
                .setUsername(userName)
                .setText("Hello World!")
                .vBuild();

        client.asGuest()
              .command(commandMessage)
              .observe(Printed.class, this::onPrinted)
              .post();
    }

    /**
     * Prints the passed event and clears the subscriptions.
     *
     * @implNote Since we expect only one event produced in response to the {@link Print} command
     *  we clear the subscriptions as the event arrives.
     */
    private void onPrinted(Printed event) {
        printEvent(event);
        client.subscriptions()
              .cancelAll();
    }

    public boolean isDone() {
        return client.subscriptions()
                     .isEmpty();
    }

    private void printEvent(EventMessage e) {
        String out = format(
                "The client received the event: %s%s",
                e.getClass().getName(),
                toCompactJson(e)
        );
        System.out.println(out);
    }

    /**
     * Closes the client, performing all necessary cleanups.
     */
    public void close() {
        client.close();
    }
}
