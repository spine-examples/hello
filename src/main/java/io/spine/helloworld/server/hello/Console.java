/*
 * Copyright 2024, TeamDev. All rights reserved.
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

package io.spine.helloworld.server.hello;

import io.spine.helloworld.hello.command.Print;
import io.spine.helloworld.hello.event.Printed;
import io.spine.server.command.Assign;
import io.spine.server.procman.ProcessManager;

import static java.lang.String.format;

/**
 * This Process Manager handles the {@linkplain Print printing} commands.
 */
final class Console extends ProcessManager<String, Output, Output.Builder> {

    /**
     * Handles the printing command.
     *
     * <p>
     * <ol>
     *    <li>Updates the state of the process by adding the passed text.
     *    <li>Prints the text to the system output.
     *    <li>Emits the event on the fact.
     * </ol>
     */
    @Assign
    Printed handle(Print command) {
        String username = command.getUsername();
        String text = command.getText();
        builder().setUsername(username)
                 .addLines(text);
        println(username, text);
        return Printed.newBuilder()
                .setUsername(username)
                .setText(command.getText())
                .vBuild();
    }

    /**
     * Prints the passed text to console.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private void println(String userName, String text) {
        String output = format("[%s] %s", userName, text);
        System.out.println(output);
    }
}
