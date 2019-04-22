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
import io.spine.core.BoundedContextName;
import io.spine.core.Command;
import io.spine.core.UserId;
import io.spine.helloworld.command.GreetWorld;
import io.spine.server.BoundedContext;
import io.spine.server.DefaultRepository;
import io.spine.server.commandbus.CommandBus;
import io.spine.server.event.EventBus;
import io.spine.server.storage.StorageFactory;
import io.spine.server.storage.memory.InMemoryStorageFactory;

import static com.google.protobuf.TextFormat.shortDebugString;
import static io.spine.core.Acks.toCommandId;
import static io.spine.util.Exceptions.newIllegalStateException;

/**
 * A simple Hello World application which sends a command to an aggregate
 * and responds to the produced event.
 */
public class HelloWorldApp {

    /**
     * The factory for creating a greeting command.
     */
    private final ActorRequestFactory requestFactory;

    /**
     * The {@code CommandBus} into which we will post the command.
     *
     * <p>Real applications would do this via {@link io.spine.server.CommandService}.
     */
    private final CommandBus commandBus;

    private HelloWorldApp() {
        // Create an instance of the Bounded Context.
        BoundedContext context = createContext();

        // Obtain a reference to Command Bus to which post the greeting command.
        // This is a ‘server’ part of the application.
        this.commandBus = context.commandBus();

        // Create a request factory to create a command.
        // This is a ‘client’ part of the application.
        this.requestFactory = createRequestFactory();
    }

    private void run() {
        Command command = createCommand();

        // Post the command to Command Bus.
        commandBus.post(command, new StreamObserver<Ack>() {
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

    private Command createCommand() {
        // Create a command message.
        UserId currentUser = requestFactory.actor();
        GreetWorld commandMessage = GreetWorld
                .vBuilder()
                .setUsername(currentUser.getValue())
                .build();

        // Create Command instance.
        CommandFactory commandFactory = requestFactory.command();
        return commandFactory.create(commandMessage);
    }

    /**
     * Prints the passed text to console.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private static void println(String text) {
        System.out.println(text);
    }

    /**
     * Creates and configures a new instance of the {@linkplain BoundedContext}.
     */
    private static BoundedContext createContext() {
        String className = HelloWorldApp.class.getSimpleName();
        BoundedContextName name = BoundedContextName
                .newBuilder()
                .setValue(className)
                .build();
        // Use in-memory storage for this example app.
        StorageFactory storageFactory = InMemoryStorageFactory.newInstance(name, false);
        BoundedContext context = BoundedContext
                .newBuilder()
                .setName(name.getValue())
                .setStorageFactorySupplier(() -> storageFactory)
                .add(DefaultRepository.of(GreetingAggregate.class))
                .build();

        // Register the event subscriber which prints out a greeting.
        EventBus eventBus = context.eventBus();
        eventBus.register(new Speaker());
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

    public static void main(String[] args) {
        new HelloWorldApp().run();
    }
}
