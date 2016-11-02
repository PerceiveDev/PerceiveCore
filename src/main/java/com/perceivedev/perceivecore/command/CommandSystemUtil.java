package com.perceivedev.perceivecore.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.ExecutablePredicate;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.MemberPredicate;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

/** Some utility functions for the command system */
public class CommandSystemUtil {

    // <editor-fold desc="Register">
    // -------------------- Register -------------------- //

    /**
     * Registers a command
     *
     * @param commandName The name of the command to register
     * @param owner The owning plugin
     * @param aliases The aliases for the command
     */
    static void registerCommand(String commandName, Plugin owner, List<String> aliases, CommandExecutor executor) {
        PluginCommand command = instantiateCommand(commandName, owner);
        if (command == null) {
            return;
        }
        command.setAliases(aliases);
        command.setExecutor(executor);

        registerCommand(command, owner);
    }

    /**
     * Registers a command
     *
     * @param commandName The name of the command to register
     * @param owner The owning plugin
     * @param aliases The aliases for the command
     */
    public static void registerCommand(String commandName, Plugin owner, List<String> aliases, CommandExecutor executor, TabCompleter tabCompleter) {
        PluginCommand command = instantiateCommand(commandName, owner);
        if (command == null) {
            return;
        }
        // lower case, as the commands will be converted to lower case, when
        // being looked up
        command.setAliases(aliases.stream().map(String::toLowerCase).collect(Collectors.toList()));
        command.setExecutor(executor);
        command.setTabCompleter(tabCompleter);

        registerCommand(command, owner);
    }

    /**
     * Registers a command
     *
     * @param owner The owning plugin
     */
    private static void registerCommand(Command command, Plugin owner) {
        CommandMap map = getCommandMap();
        if (map == null) {
            return;
        }

        Command conflict = map.getCommand(command.getName());
        if (conflict != null) {
            throw new IllegalArgumentException("Command already registered: " + command.getName());
        }

        // use the plugins name as fallback, as that is the default behaviour
        map.register(command.getName(), owner.getName(), command);
    }
    // </editor-fold>

    // <editor-fold desc="Unregister">
    // -------------------- Unregister -------------------- //

    /**
     * Unregisters a command
     *
     * @param commandName The command to unregister
     */
    public static void unregisterCommand(String commandName) {
        CommandMap commandMap = getCommandMap();
        if (commandMap == null) {
            return;
        }

        Command command = commandMap.getCommand(commandName);
        // not registered
        if (command == null) {
            return;
        }

        command.unregister(commandMap);

        Map<String, Command> knownCommands = getMapFromCommandMap(commandMap);
        if (knownCommands == null) {
            return;
        }
        {
            Iterator<Entry<String, Command>> iterator = knownCommands.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Command> commandEntry = iterator.next();
                if (commandEntry.getValue().equals(command)) {
                    iterator.remove();
                }
            }
        }
    }
    // </editor-fold>

    // <editor-fold desc="Utility Methods">
    // -------------------- Utility Methods -------------------- //

    /**
     * Instantiates a {@link PluginCommand} (has Protected access)
     *
     * @param commandName The name of the command
     * @param owner The owning plugin
     *
     * @return The instantiated plugin command
     */
    private static PluginCommand instantiateCommand(String commandName, Plugin owner) {
        ReflectResponse<?> response = ReflectionUtil
                .instantiate(PluginCommand.class,
                        new ExecutablePredicate<Constructor<?>>()
                                .withParameters(String.class, Plugin.class),
                        commandName, owner);

        if (printError(response, "Couldn't instantiate PluginCommand (%s)")) {
            return null;
        }
        return (PluginCommand) response.getValue();
    }

    /**
     * Returns the servers command map
     *
     * @return The server's command map
     */
    private static CommandMap getCommandMap() {
        Server server = Bukkit.getServer();
        ReflectResponse<Object> response = ReflectionUtil
                .getFieldValue(server.getClass(), server, field -> field.getType() == SimpleCommandMap.class);

        if (printError(response, "Couldn't find the CommandMap (%s)")) {
            return null;
        }

        return (CommandMap) response.getValue();
    }

    /**
     * Returns all known commands in the {@link CommandMap}
     *
     * @param commandMap The commandMap to get it from
     *
     * @return The knownCommands map from the {@link CommandMap}
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Command> getMapFromCommandMap(CommandMap commandMap) {
        ReflectResponse<Object> response = ReflectionUtil.getFieldValue(commandMap.getClass(),
                commandMap,
                new MemberPredicate<Field>().withName("knownCommands"));
        if (printError(response, "Couldn't obtain knownCommands map. (%s)")) {
            return null;
        }
        return (Map<String, Command>) response.getValue();
    }

    /**
     * Prints the error, if any
     *
     * @param response The {@link ReflectResponse}
     * @param message The message to send. Formatted using String.format. %s is
     *            the result type
     *
     * @return True if an error occurred
     */
    private static boolean printError(ReflectResponse<?> response, String message) {
        if (!response.isValuePresent()) {
            if (response.getException() != null) {
                PerceiveCore.getInstance().getLogger().log(Level.WARNING,
                        String.format(message, response.getResultType().toString()),
                        response.getException());
            } else {
                PerceiveCore.getInstance().getLogger().log(Level.WARNING,
                        String.format(message, response.getResultType().toString()));
            }

            return true;
        }
        return false;
    }
    // </editor-fold>
}
