/*
 * Copyright 2020, TeamDev. All rights reserved.
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

    static {
        configureEnvironment();
    }

    private final io.spine.server.Server server;

    /**
     * Configures the server environment and creates in-process implementation
     * with the passed name.
     */
    public Server(String serverName) {
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
        ServerEnvironment.instance()
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
