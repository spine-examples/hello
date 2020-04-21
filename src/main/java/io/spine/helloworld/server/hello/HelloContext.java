package io.spine.helloworld.server.hello;

import io.spine.server.BoundedContext;
import io.spine.server.BoundedContextBuilder;

/**
 * Provides {@link BoundedContextBuilder} for the Hello Context.
 */
public final class HelloContext {

    /**
     * The name of the Context.
     *
     * @apiNote This constant is also used for annotating the package.
     * See {@code package-info.java}.
     */
    static final String NAME = "Hello";

    /** Prevents instantiation of this utility class. */
    private HelloContext() {
    }

    /**
     * Creates new instance of the Hello Context builder.
     */
    public static BoundedContextBuilder newBuilder() {
        return BoundedContext
                .singleTenant(NAME)
                .add(Console.class);
    }
}
