package com.perceivedev.perceivecore.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The result of searching a command. Contains basic data and the arguments you will still need to pass it.
 */
public class CommandFindResult {
    private CommandNode  commandNode;
    private List<String> restArgs;

    /**
     * @param commandNode The {@link CommandNode}. May be null, indicating none found.
     * @param restArgs The remaining arguments
     */
    public CommandFindResult(CommandNode commandNode, Collection<String> restArgs) {
        this.commandNode = commandNode;
        this.restArgs = new ArrayList<>(restArgs);
    }

    /**
     * Returns the command node
     *
     * @return The CommandNode. Null if not found.
     */
    public CommandNode getCommandNode() {
        return commandNode;
    }

    /**
     * Checks if the command was found
     *
     * @return True if the command was found.
     */
    public boolean wasFound() {
        return commandNode != null;
    }

    /**
     * Returns the remaining arguments
     *
     * @return The remaining arguments to pass. A clone.
     */
    public List<String> getRestArgs() {
        return new ArrayList<>(restArgs);
    }

    @Override
    public String toString() {
        return "CommandFindResult{" +
                  "commandNode=" + commandNode +
                  ", restArgs=" + restArgs +
                  ", wasFound=" + wasFound() +
                  '}';
    }
}
