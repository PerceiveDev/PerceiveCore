package com.perceivedev.perceivecore.command;

import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

/**
 * A command
 */
interface CommandNode {

    /**
     * Returns the children of this command
     */
    List<CommandNode> getChildren();

    /**
     * Executes the command
     *
     * @param sender The sender to execute it as
     * @param args The arguments for this command
     *
     * @return The Result of this command
     */
    CommandResult execute(CommandSender sender, String... args);

    /**
     * Tab completes this command
     *
     * @param sender The The sender who tab completes
     * @param wholeChat The whole chat of him
     * @param relativeIndex The relative index to this command.
     *
     * @return ALl possible tab completions. Null for players.
     */
    List<String> tabComplete(CommandSender sender, List<String> wholeChat, int relativeIndex);

    /**
     * Searches for a command
     *
     * @param sender The sender to search as
     * @param commandQuery The commandQuery to search for
     *
     * @return The {@link CommandFindResult}
     */
    CommandFindResult find(CommandSender sender, String commandQuery);

    /**
     * Checks if a permissible has a permission
     *
     * @param permissible The permissible to check
     *
     * @return True if the permissible has the given permission
     */
    boolean hasPermission(Permissible permissible);

    /**
     * Returns the accepted {@link CommandSenderType}s
     *
     * @return The accepted {@link CommandSenderType}s
     */
    Collection<CommandSenderType> getAcceptedCommandSenders();

    /**
     * Checks if the given {@link CommandSender} is accepted
     *
     * @param commandSender The {@link CommandSender} to check
     *
     * @return True if it accepts the {@link CommandSender}
     */
    boolean acceptsCommandSender(CommandSender commandSender);

    /**
     * Checks if it is the keyword of the command
     *
     * @param string The String to check
     *
     * @return True if this is the keyword of this command
     */
    boolean isYourKeyword(String string);
    
    /* **********************************************************************************
     *
     *                                      Cosmetic
     *
     ************************************************************************************/

    /**
     * Returns the command usage
     *
     * @return The Usage of the command
     */
    String getUsage();

    /**
     * Returns the command description
     *
     * @return The Description of the command
     */
    String getDescription();

    /**
     * Returns the keyword of the command. To use in e.g. tab completion
     *
     * @return The keyword
     */
    String getKeyword();
}
