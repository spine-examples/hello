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

import io.grpc.stub.StreamObserver;
import io.spine.client.ActorRequestFactory;
import io.spine.client.CommandFactory;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.core.UserId;
import io.spine.helloworld.command.Print;
import io.spine.server.BoundedContext;
import io.spine.server.storage.StorageFactory;
import io.spine.server.storage.memory.InMemoryStorageFactory;

import static com.google.protobuf.TextFormat.shortDebugString;
import static io.spine.core.Acks.toCommandId;
import static io.spine.util.Exceptions.newIllegalStateException;

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
        this.context = createContext();
        this.requestFactory = createRequestFactory();
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
     * Creates and configures a new instance of the Hello Context.
     */
    private static BoundedContext createContext() {
        // Use in-memory storage for this example app.
        // Real application would use factories for working with JDBC or Google Datastore.
        StorageFactory factory =
                InMemoryStorageFactory.newInstance(HelloContext.NAME, false);
        BoundedContext context = HelloContext
                .newBuilder()
                .setStorageFactorySupplier(() -> factory)
                .build();
        return context;
    }

    /**
     * Creates a new request factory with the ID of the current user and the system time zone.
     */
    private static ActorRequestFactory createRequestFactory() {
        @SuppressWarnings("AccessOfSystemProperties")
        UserId currentUser = UserId
                .vBuilder()
                .setValue(System.getProperty("user.name"))
                .build();
        return ActorRequestFactory
                .newBuilder()
                .setActor(currentUser)
                .build();
    }

    /**
     * Creates the command to post.
     */
    private Command createCommand() {
        // Create a command message.
        UserId currentUser = requestFactory.actor();
        Print commandMessage = Print
                .vBuilder()
                .setUsername(currentUser.getValue())
                .setText("Hello World!")
                .build();

        // Create the Command instance using the command message.
        CommandFactory commandFactory = requestFactory.command();
        return commandFactory.create(commandMessage);
    }

    /**
     * Posts the command to the {@code CommandBus} of the Hello Context.
     *
     * <p>Real applications would do this via {@link io.spine.server.CommandService}.
     */
    private void post(Command command) {
        context.commandBus().post(command, new StreamObserver<Ack>() {
            @Override
            public void onNext(Ack ack) {
                println("Command posted: " + shortDebugString(toCommandId(ack)));
            }

            @Override
            public void onError(Throwable t) {
                throw newIllegalStateException(t, "Unable to post the command.");
            }

            @Override
            public void onCompleted() {
                println("Successfully posted.");
            }
        });
    }

    /**
     * Closes the Bounded Context of the application.
     */
    @SuppressWarnings(
            "CatchAndPrintStackTrace"
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
     * Prints the passed text to console.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private static void println(String text) {
        System.out.println(text);
    }

    /**
     * Creates and runs the application.
     */
    public static void main(String[] args) {
        new Application().run();
    }
}
