package io.spine.helloworld.client;

import com.google.common.collect.ImmutableSet;
import io.spine.base.EventMessage;
import io.spine.client.Subscription;
import io.spine.helloworld.hello.command.Print;
import io.spine.helloworld.hello.event.Printed;
import org.checkerframework.checker.nullness.qual.Nullable;

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

    /** Subscriptions to the events produced in response to the {@link Print} command. */
    private @Nullable ImmutableSet<Subscription> printingSubscriptions;

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
        this.printingSubscriptions =
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
        if (printingSubscriptions != null) {
            printEvent(event);
            printingSubscriptions.forEach(client::cancel);
            printingSubscriptions = null;
        }
    }

    private void printEvent(EventMessage e) {
        String out = format(
                "The client received the event: %s%s",
                e.getClass().getName(),
                toCompactJson(e));
        System.out.println(out);
    }

    /**
     * Shuts down the client.
     */
    public void shutdown() {
        client.shutdown();
    }
}
