package io.spine.helloworld.server;

import io.spine.helloworld.server.hello.HelloContext;
import io.spine.server.ServerEnvironment;
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
