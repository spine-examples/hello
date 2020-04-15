package io.spine.helloworld;

import io.spine.base.EventMessage;
import io.spine.client.Client;
import io.spine.helloworld.command.Print;
import io.spine.helloworld.event.Printed;
import io.spine.server.Server;
import io.spine.server.ServerEnvironment;
import io.spine.server.storage.memory.InMemoryStorageFactory;
import io.spine.server.transport.memory.InMemoryTransportFactory;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.json.Json.toCompactJson;
import static java.lang.String.format;

/**
 * This example application demonstrates sending a command to a server, observing the results
 * of the handling of the command.
 *
 * <p>This app consists of the two parts (that in a real world scenario would be implemented by
 * separate applications):
 * <ol>
 *     <li>In-process server configured to serve the {@link HelloContext}.
 *     <li>A client connected to the in-process server.
 * </ol>
 *
 * <p>The client:
 * <ol>
 *    <li>generates a {@link Print} command;
 *    <li>subscribes to {@link Printed} events that would be generated in response to the command;
 *    <li>posts the command to the server.
 * </ol>
 *
 * <p>After the command is posted and handled, the application terminates.
 */
public final class Example {

    /** The generated name for the in-process server which both the server and the client use. */
    private @Nullable String serverName;

    /** The server-side of this example application. */
    private @Nullable Server server;

    /** The client-side of this example.*/
    private @Nullable Client client;

    /**
     * Runs the example.
     */
    public static void main(String[] args) {
        Example app = new Example();
        app.run();
    }

    /** Generates a new server name. */
    private Example() {
        this.serverName = UUID.randomUUID().toString();
    }

    /**
     * Executes the steps of the example.
     */
    private void run() {
        configureServerEnvironment();
        try {
            createAndStartServer();
            createClient();
            sendCommand();
        } catch (Exception e) {
            onError(e);
        }
        finally {
            close();
        }
    }

    /**
     * Configures the production server-side environment.
     *
     * <p>We use in-memory implementations (that are typically used in tests) to simplify this
     * example application. Real applications would use implementations that correspond
     * to their environments.
     */
    private static void configureServerEnvironment() {
        ServerEnvironment se = ServerEnvironment.instance();
        se.configureStorage(InMemoryStorageFactory.newInstance());
        se.configureTransport(InMemoryTransportFactory.newInstance());
    }

    /**
     * Creates and starts the server with the {@link HelloContext}.
     */
    private void createAndStartServer() {
        checkNotNull(serverName);
        server = Server.inProcess(serverName)
                       .add(HelloContext.newBuilder())
                       .build();
        try {
            server.start();
        } catch (IOException e) {
            onError(e);
        }
    }

    /**
     * Prints a stack trace of the passed exception.
     *
     * <p>A real app should use more sophisticated exception handling.
     */
    @SuppressWarnings({"CatchAndPrintStackTrace", "CallToPrintStackTrace"})
    private void onError(Exception e) {
        e.printStackTrace();
    }

    /**
     * Obtains the server instance ensuring it's not null.
     *
     * @see #close()
     */
    private Server server() {
        return checkNotNull(server);
    }

    /**
     * Creates a client connected to the in-process server.
     */
    private void createClient() {
        checkNotNull(serverName);
        client = Client.inProcess(serverName)
                       .shutdownTimout(2, TimeUnit.SECONDS)
                       .build();
    }

    /**
     * Obtains the client instance ensuring it's not null.
     *
     * @see #close()
     */
    private Client client() {
        return checkNotNull(client);
    }

    @SuppressWarnings("CheckReturnValue")
        // Ignore subscriptions returned by the `post()` method for the brevity of the example.
    private void sendCommand() {
        String userName = System.getProperty("user.name");
        Print commandMessage = Print
                .newBuilder()
                .setUsername(userName)
                .setText("Hello World!")
                .vBuild();
        client().asGuest()
                .command(commandMessage)
                .observe(Printed.class, this::printEvent)
                .post();
    }

    private void printEvent(EventMessage e) {
        String out = format(
                "The client received the event: %s%s",
                e.getClass().getName(),
                toCompactJson(e));
        System.out.println(out);
    }

    private void close() {
        client().shutdown();
        this.client = null;
        server().shutdown();
        this.server = null;
        this.serverName = null;
    }
}
