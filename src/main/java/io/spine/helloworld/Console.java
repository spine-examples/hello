package io.spine.helloworld;

import io.spine.helloworld.command.Output;
import io.spine.helloworld.command.OutputVBuilder;
import io.spine.helloworld.command.Print;
import io.spine.helloworld.event.Printed;
import io.spine.server.command.Assign;
import io.spine.server.procman.ProcessManager;

/**
 * This Process Manager handles the {@linkplain Print printing} commands.
 */
final class Console extends ProcessManager<String, Output, OutputVBuilder> {

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
        println(text);
        return Printed
                .vBuilder()
                .setUsername(username)
                .setText(command.getText())
                .build();
    }

    /**
     * Prints the passed text to console.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private static void println(String text) {
        System.out.println(text);
    }
}
