package io.spine.helloworld;

import io.spine.helloworld.client.Client;
import io.spine.helloworld.hello.command.Print;
import io.spine.helloworld.hello.event.Printed;
import io.spine.helloworld.server.Server;

import java.util.UUID;

/**
 * This example application demonstrates sending a command to a server, observing the results
 * of the handling of the command.
 *
 * <p>This app consists of the two parts (that in a real world scenario would be implemented by
 * separate applications):
 * <ol>
 *     <li>In-process server configured to serve the Hello Context.
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

    /**
     * Runs the example.
     *
     * <p>The method performs the following steps:
     * <ol>
     *     <li>Generates the name for the in-process server.
     *     <li>Creates the server with the generated name and starts it.
     *     <li>Creates a client which connects to the server.
     *     <li>The client sends the command.
     * </ol>
     *
     * <p>After that the method performs the clean-up by shutting down the client and then
     * the server.
     *
     * @see Server
     * @see Client
     */
    public static void main(String[] args) {
        String serverName = UUID.randomUUID().toString();
        Server server = new Server(serverName);
        Client client = null;
        try {
            server.start();
            client = new Client(serverName);
            client.sendCommand();
        } catch (Exception e) {
            onError(e);
        }
        finally {
            if (client != null) {
                client.shutdown();
            }
            server.shutdown();
        }
    }

    /**
     * Prints a stack trace of the passed exception.
     *
     * <p>A real app should use more sophisticated exception handling.
     */
    @SuppressWarnings({"CatchAndPrintStackTrace", "CallToPrintStackTrace"})
    private static void onError(Exception e) {
        e.printStackTrace();
    }
}
