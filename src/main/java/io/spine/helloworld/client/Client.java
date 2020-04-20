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
                toCompactJson(e));
        System.out.println(out);
    }

    /**
     * Closes the client, performing all necessary cleanups.
     */
    public void close() {
        client.close();
    }
}
