package io.spine.helloworld;

import io.spine.core.BoundedContextName;
import io.spine.core.BoundedContextNames;
import io.spine.server.BoundedContext;
import io.spine.server.BoundedContextBuilder;
import io.spine.server.DefaultRepository;

/**
 * Provides {@link BoundedContextBuilder} for the Hello Context.
 */
final class HelloContext {

    /**
     * The name of the Context.
     *
     * @apiNote This constant is also used for annotating the package.
     * See {@code package-info.java}.
     */
    static final String NAME = "Hello";
    private static final BoundedContextName BC_NAME = BoundedContextNames.newName(NAME);

    /** Prevents instantiation of this utility class. */
    private HelloContext() {
    }

    /** Obtains the name of the Context. */
    static BoundedContextName name() {
        return BC_NAME;
    }

    /**
     * Creates new instance of the Hello Context builder.
     */
    static BoundedContextBuilder newBuilder() {
        return BoundedContext
                .newBuilder()
                .setName(NAME)
                .add(DefaultRepository.of(Console.class));
    }
}
