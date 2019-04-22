/*
 * Copyright 2018, TeamDev. All rights reserved.
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

import io.spine.helloworld.command.GreetWorld;
import io.spine.helloworld.event.WorldGreeted;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;

/**
 * A simple aggregate that handles a greeting command.
 */
final class GreetingAggregate extends Aggregate<String, Greeting, GreetingVBuilder> {

    GreetingAggregate(String id) {
        super(id);
    }

    /**
     * Handles the passed command.
     *
     * <p>The returned event message says that the user greeted the world.
     * This becomes the fact in the application data.
     */
    @Assign
    WorldGreeted handle(GreetWorld command) {
        return WorldGreeted
                .vBuilder()
                .setUsername(command.getUsername())
                .build();
    }

    /** Applies the passed event to the aggregate. */
    @Apply
    private void event(WorldGreeted e) {
        builder().setUsername(e.getUsername());
    }
}
