package io.spine.helloworld;

import io.spine.helloworld.command.Output;
import io.spine.helloworld.command.Print;
import io.spine.helloworld.event.Printed;
import io.spine.server.command.Assign;
import io.spine.server.procman.ProcessManager;

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
        return Printed
                .newBuilder()
                .setUsername(username)
                .setText(command.getText())
                .vBuild();
    }

    /**
     * Prints the passed text to console.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private void println(String userName, String text) {
        String output = String.format("[%s] %s", userName, text);
        System.out.println(output);
    }
}
