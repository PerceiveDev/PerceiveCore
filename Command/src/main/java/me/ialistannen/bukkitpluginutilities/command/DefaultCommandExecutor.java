package me.ialistannen.bukkitpluginutilities.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.ialistannen.bukkitpluginutilities.language.MessageProvider;

/**
 * The default command Executor
 * <p>
 * <br>
 * <b>Possible language keys:</b>
 * <ul>
 * <li><u>Not found</u>: {@value LANGUAGE_PREFIX}not.found</li>
 * <li><u>No permission</u>: {@value LANGUAGE_PREFIX}no.permission</li>
 * <li><u>Error</u>: {@value LANGUAGE_PREFIX}error.invoking</li>
 * <li><u>Send usage</u>: {@value LANGUAGE_PREFIX}usage</li>
 * </ul>
 */
public class DefaultCommandExecutor implements CommandExecutor {

    private static final String LANGUAGE_PREFIX = "command.executor.";

    private CommandTree tree;
    private MessageProvider language;

    /**
     * Creates a Default command executor. It has some language keys to modify
     * it's messages, look at the class javadoc for them.
     *
     * @param tree The {@link CommandTree} to use
     * @param language The {@link MessageProvider} to use
     */
    @SuppressWarnings("unused")
    public DefaultCommandExecutor(CommandTree tree, MessageProvider language) {
        this.tree = tree;
        this.language = language;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        String[] arguments = new String[args.length + 1];
        arguments[0] = label;
        if (args.length > 0) {
            System.arraycopy(args, 0, arguments, 1, args.length);
        }
        CommandFindResult findResult = tree.find(commandSender, arguments);

        if (!findResult.wasFound()) {
            commandSender.sendMessage(language.trOrDefault(LANGUAGE_PREFIX + "not.found", "&cCommand not found."));
            return true;
        }

        CommandResult result = findResult.getCommandNode()
                .execute(commandSender, findResult.getRestArgs().toArray(new String[0]));
        if (result == null) {
            result = CommandResult.ERROR;
        }
        switch (result) {
            case NO_PERMISSION: {
                commandSender.sendMessage(language.trOrDefault(LANGUAGE_PREFIX + "no.permission", "&cNo permission!"));
                return true;
            }
            case ERROR: {
                commandSender.sendMessage(language.trOrDefault(LANGUAGE_PREFIX + "error.invoking", "&cAn internal " +
                        "error occurred!"));
                return true;
            }
            case SEND_USAGE: {
                commandSender.sendMessage(language.trOrDefault(LANGUAGE_PREFIX + "usage", "&cUsage: {0}", (Object) 
                        findResult
                        .getCommandNode()
                        .getUsage()));
                return true;
            }
            case SUCCESSFULLY_INVOKED: {
                // fallthrough
            }
            default:
        }
        return true;
    }
}
