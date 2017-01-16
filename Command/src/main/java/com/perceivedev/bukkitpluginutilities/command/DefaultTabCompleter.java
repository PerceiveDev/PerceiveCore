package com.perceivedev.bukkitpluginutilities.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * The default tab completer
 */
public class DefaultTabCompleter implements TabCompleter {

    private CommandTree tree;

    /**
     * @param tree The {@link CommandTree} to use
     */
    @SuppressWarnings("unused")
    public DefaultTabCompleter(CommandTree tree) {
        this.tree = tree;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        arguments.add(0, label);
        Optional<List<String>> tabComplete = tree.findAndTabComplete(commandSender, arguments);
        // it looks nicer this way :)
        //noinspection OptionalIsPresent
        if (!tabComplete.isPresent()) {
            return null;
        }
        return tabComplete.get().stream()
                .sorted()     // sort and filter it...
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
