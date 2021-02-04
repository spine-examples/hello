/*
 * Copyright 2021, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

import io.spine.base.Identifier;
import io.spine.helloworld.client.Client;
import io.spine.helloworld.hello.command.Print;
import io.spine.helloworld.hello.event.Printed;
import io.spine.helloworld.server.Server;

import java.io.IOException;
import java.time.Duration;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;

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
    @SuppressWarnings("UnstableApiUsage") // `sleepUninterruptibly()` is @Beta. OK for this example.
    public static void main(String[] args) {
        String serverName = Identifier.newUuid();
        Server server = new Server(serverName);
        Client client = null;
        try {
            server.start();
            client = new Client(serverName);
            client.sendCommand();
            while (!client.isDone()) {
                sleepUninterruptibly(Duration.ofMillis(100));
            }
        } catch (IOException e) {
            onError(e);
        } finally {
            if (client != null) {
                client.close();
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
