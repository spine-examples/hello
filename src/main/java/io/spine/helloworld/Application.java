/*
 * Copyright 2018, TeamDev. All rights reserved.
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

package io.spine.helloworld;

import io.spine.client.ActorRequestFactory;
import io.spine.client.CommandFactory;
import io.spine.core.Command;
import io.spine.core.UserId;
import io.spine.grpc.StreamObservers;
import io.spine.helloworld.command.Print;
import io.spine.server.BoundedContext;
import io.spine.server.ServerEnvironment;
import io.spine.server.storage.memory.InMemoryStorageFactory;
import io.spine.server.transport.memory.InMemoryTransportFactory;

/**
 * This application creates a command (mimicking client-side) and posts it for handling.
 */
public final class Application {

    /**
     * The instance of the Hello Bounded Context.
     *
     * <p>This is a ‘server’ part of this example application.
     */
    private final BoundedContext context;

    /**
     * The factory for creating commands.
     *
     * <p>This is a ‘client’ part of this example application.
     */
    private final ActorRequestFactory requestFactory;

    private Application() {
        configureServerEnvironment();
        this.context = HelloContext
                .newBuilder()
                .build();
        this.requestFactory = createRequestFactory();
    }

    /**
     * Configures conditions and configuration under which the application operates.
     */
    private static void configureServerEnvironment() {
        ServerEnvironment se = ServerEnvironment.instance();
        se.configureStorage(InMemoryStorageFactory.newInstance());
        se.configureTransport(InMemoryTransportFactory.newInstance());
    }

    /**
     * Creates a new request factory with the ID of the current computer user.
     */
    private static ActorRequestFactory createRequestFactory() {
        @SuppressWarnings("AccessOfSystemProperties")
        UserId currentUser = UserId
                .newBuilder()
                .setValue(System.getProperty("user.name"))
                .vBuild();
        return ActorRequestFactory
                .newBuilder()
                .setActor(currentUser)
                .build();
    }

    /**
     * Creates the command, posts it for execution and then closes the application.
     */
    private void run() {
        Command command = createCommand();
        try {
            post(command);
        } finally {
            close();
        }
    }

    /**
     * Creates the command to post.
     */
    private Command createCommand() {
        // Create a command message.
        UserId currentUser = requestFactory.actor();
        Print commandMessage = Print
                .newBuilder()
                .setUsername(currentUser.getValue())
                .setText("Hello World!")
                .vBuild();

        // Create the Command instance using the command message.
        CommandFactory commandFactory = requestFactory.command();
        return commandFactory.create(commandMessage);
    }

    /**
     * Posts the command to the {@code CommandBus} of the Hello Context.
     */
    private void post(Command command) {
        context.commandBus().post(command, StreamObservers.noOpObserver());
    }

    /**
     * Closes the Bounded Context of the application.
     */
    @SuppressWarnings(
            {"CatchAndPrintStackTrace", "CallToPrintStackTrace"}
            /* A real app should use more sophisticated exception handling. */
    )
    private void close() {
        try {
            context.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates and runs the application.
     */
    public static void main(String[] args) {
        Application app = new Application();
        app.run();
    }
}
