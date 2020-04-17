package io.spine.helloworld.server;

import io.spine.core.Event;
import io.spine.core.TenantId;
import io.spine.helloworld.server.hello.HelloContext;
import io.spine.server.ServerEnvironment;
import io.spine.server.delivery.Delivery;
import io.spine.server.delivery.InboxMessage;
import io.spine.server.delivery.UniformAcrossAllShards;
import io.spine.server.storage.memory.InMemoryStorageFactory;
import io.spine.server.tenant.TenantAwareRunner;
import io.spine.server.transport.memory.InMemoryTransportFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.spine.server.Server.inProcess;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Backend implementation of the Hello Context.
 */
public final class Server {

    private static final ExecutorService deliveryPool = newSingleThreadExecutor();
    private final io.spine.server.Server server;

    /**
     * Configures the server environment and creates in-process implementation
     * with the passed name.
     */
    public Server(String serverName) {
        configureServerEnvironment();
        this.server = inProcess(serverName)
                .add(HelloContext.newBuilder())
                .build();
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
        se.configureDelivery(asyncDelivery());
    }

    @SuppressWarnings("HandleMethodResult")
    private static Delivery asyncDelivery() {
        Delivery delivery = Delivery.newBuilder()
                                    .setStrategy(UniformAcrossAllShards.singleShard())
                                    .build();
        delivery.subscribe(
                (message) -> new Thread(
                        () -> {
                            TenantId tenantId = tenantId(message);
                            TenantAwareRunner.with(tenantId)
                                             .run(() -> delivery.deliverMessagesFrom(message.shardIndex()));
                        }
                ).start()
        );
        return delivery;
    }

    private static TenantId tenantId(InboxMessage message) {
        TenantId tenantId;
        if (message.hasCommand()) {
            tenantId = message.getCommand()
                              .getContext()
                              .getActorContext()
                              .getTenantId();
        } else {
            Event event = message.getEvent();

            tenantId = event.getContext()
                            .getPastMessage()
                            .getActorContext()
                            .getTenantId();
        }
        return tenantId;
    }

    /** Starts the server. */
    public void start() throws IOException {
        server.start();
    }

    /** Shut downs the server. */
    public void shutdown() {
        deliveryPool.shutdownNow();
        server.shutdown();
    }
}
