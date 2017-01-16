package com.perceivedev.bukkitpluginutilities.command;

import java.util.function.Predicate;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * The different CommandSender types
 */
public enum CommandSenderType {

    ALL(sender -> true),
    CONSOLE(sender -> sender instanceof ConsoleCommandSender),
    BLOCK(sender -> sender instanceof BlockCommandSender),
    PLAYER(sender -> sender instanceof Player),
    UNKNOWN(sender -> !CONSOLE.isThisType(sender)
            && !BLOCK.isThisType(sender)
            && !PLAYER.isThisType(sender));

    private Predicate<CommandSender> isYou;

    CommandSenderType(Predicate<CommandSender> isYou) {
        this.isYou = isYou;
    }

    /**
     * Checks the type of the CommandSender
     *
     * @param sender The {@link CommandSender} to check
     *
     * @return True if the commandSender is of this type
     */
    public boolean isThisType(CommandSender sender) {
        return isYou.test(sender);
    }

    /**
     * Returns the {@link CommandSenderType} of the sender
     *
     * @param sender The CommandSender to check
     *
     * @return The CommandSenderType of the sender
     */
    public static CommandSenderType getType(CommandSender sender) {
        for (CommandSenderType commandSenderType : values()) {
            // don't match all
            if (commandSenderType == ALL) {
                continue;
            }
            if (commandSenderType.isThisType(sender)) {
                return commandSenderType;
            }
        }
        return UNKNOWN;
    }
}
