package io.spine.helloworld.server;

import io.spine.base.Production;
import io.spine.helloworld.server.hello.HelloContext;
import io.spine.server.ServerEnvironment;
import io.spine.server.delivery.Delivery;
import io.spine.server.storage.memory.InMemoryStorageFactory;
import io.spine.server.transport.memory.InMemoryTransportFactory;

import java.io.IOException;

import static io.spine.server.Server.inProcess;

/**
 * Backend implementation of the Hello Context.
 */
public final class Server {

    private final io.spine.server.Server server;

    /**
     * Configures the server environment and creates in-process implementation
     * with the passed name.
     */
    public Server(String serverName) {
        configureEnvironment();
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
    private static void configureEnvironment() {
        Class<Production> prod = Production.class;
        ServerEnvironment
                .instance()
                .use(InMemoryStorageFactory.newInstance(), prod)
                .use(Delivery.localAsync(), prod)
                .use(InMemoryTransportFactory.newInstance(), prod);
    }

    /** Starts the server. */
    public void start() throws IOException {
        server.start();
    }

    /** Shut downs the server. */
    public void shutdown() {
        server.shutdown();
    }
}
