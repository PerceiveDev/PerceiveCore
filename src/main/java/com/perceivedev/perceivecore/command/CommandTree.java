package com.perceivedev.perceivecore.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.language.MessageProvider;
import com.perceivedev.perceivecore.util.DisableManager;
import com.perceivedev.perceivecore.util.types.DisableListener;

/** A Command tree */
public class CommandTree {

    private TreeRoot root;

    /**
     * Creates a new Command Tree
     * 
     * Uses {@link PerceiveCore}s DisableManager
     * 
     * @see #CommandTree(DisableManager)
     */
    public CommandTree() {
        this(PerceiveCore.getInstance().getDisableManager());
    }

    /**
     * Creates a new Command Tree
     *
     * Creates a new {@link DisableManager} for the given plugin
     * 
     * @param plugin The Plugin this is for
     * 
     * @see #CommandTree(DisableManager)
     */
    public CommandTree(Plugin plugin) {
        this(new DisableManager(plugin));
    }

    /**
     * Creates a new CommandTree, which cleans up at reloads
     * 
     * @param disableManager The {@link DisableManager} to use
     */
    public CommandTree(DisableManager disableManager) {
        this.root = new TreeRoot();

        DisableListener disableListener = () -> {
            for (CommandNode node : root.getChildren()) {
                CommandSystemUtil.unregisterCommand(node::isYourKeyword);
            }
        };

        disableManager.addListener(disableListener);
    }

    /**
     * Returns all commands
     *
     * @return All commands
     */
    public List<CommandNode> getAllCommands() {
        return root.getAllChildren();
    }

    /**
     * Returns the tree root
     *
     * @return The Tree's root
     */
    CommandNode getRoot() {
        return root;
    }

    /**
     * Checks if the {@link CommandNode} is the TreeRoot
     *
     * @param commandNode The {@link CommandNode} to check
     *
     * @return True if it is the root
     */
    boolean isRoot(CommandNode commandNode) {
        return commandNode instanceof TreeRoot;
    }

    /**
     * Adds a top level child
     *
     * @param node The {@link CommandNode} to add
     */
    public void addTopLevelChild(CommandNode node) {
        root.addChild(node);
    }

    /**
     * Attaches the help
     *
     * @param node The {@link CommandNode} to add it to
     * @param permission The permission for the help command
     * @param provider The MessageProvider to use
     */
    public void attachHelp(AbstractCommandNode node, String permission, MessageProvider provider) {
        node.addChild(new DefaultHelpCommand(new Permission(permission), provider, this));
    }

    /**
     * Adds a top level child and registers it at runtime
     *
     * @param node The {@link CommandNode} to add
     * @param executor The {@link CommandExecutor} to use
     * @param tabCompleter The {@link TabCompleter} to use
     * @param owner The owning {@link Plugin}
     * @param aliases The aliases for this command
     */
    public void addTopLevelChildAndRegister(CommandNode node, CommandExecutor executor, TabCompleter tabCompleter, Plugin owner, String... aliases) {
        root.addChild(node);
        List<String> alias = Arrays.asList(aliases);
        CommandSystemUtil.registerCommand(node.getKeyword(), owner, alias, executor, tabCompleter);
    }

    /**
     * Removes the command and unregisters it at runtime
     *
     * @param commandNode The {@link CommandNode} to remove
     */
    public void removeTopLevelChild(CommandNode commandNode) {
        if (!root.getChildren().contains(commandNode)) {
            return;
        }
        root.removeChild(root);
        CommandSystemUtil.unregisterCommand(commandNode.getKeyword());
    }

    /**
     * Finds a {@link CommandNode}
     *
     * @param sender The CommandSender to find it for
     * @param args The command arguments
     *
     * @return The {@link CommandFindResult}
     */
    public CommandFindResult find(CommandSender sender, String[] args) {
        return root.find(sender, args);
    }

    /**
     * Finds a command, then tab completes it.
     *
     * @param sender The CommandSender to tab complete for
     * @param userChat The user chat
     *
     * @return The tab completion of the command or an empty optional if the
     *         command was not found or tab complete returned null.
     */
    public Optional<List<String>> findAndTabComplete(CommandSender sender, List<String> userChat) {
        return root.findAndTabComplete(sender, userChat);
    }

    /** A private transparent tree root */
    private static class TreeRoot extends AbstractCommandNode {

        private List<CommandNode> children = new ArrayList<>();

        private TreeRoot() {
            super(new Permission("root", PermissionDefault.TRUE), CommandSenderType.ALL);
        }

        private void addChild(CommandNode child) {
            children.add(child);
        }

        @Override
        public List<CommandNode> getChildren() {
            return Collections.unmodifiableList(children);
        }

        /**
         * Finds a {@link AbstractCommandNode}
         *
         * @param sender The CommandSender to find it for
         * @param query The query to search
         *
         * @return The found command node or null if none found.
         */
        @Override
        protected CommandFindResult impl_find(CommandSender sender, Queue<String> query) {
            CommandFindResult chosenOne = new CommandFindResult(null, Collections.emptyList());

            String joinedQuery = query.stream().collect(Collectors.joining(" "));

            // now, is there any child that can continue
            for (CommandNode child : getChildren()) {
                // deepClone to prevent recursion to interfere with each other
                CommandFindResult findRes = child.find(sender, joinedQuery);

                if (findRes.wasFound()) {
                    // sweet, this child could take over. Let's honor it.
                    chosenOne = findRes;
                }
            }

            return chosenOne;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, List<String> wholeChat, int relativeIndex) {
            return Collections.emptyList();
        }

        @Override
        public boolean isYourKeyword(String string) {
            return false;
        }

        @Override
        public String getUsage() {
            return "root usage";
        }

        @Override
        public String getDescription() {
            return "root desc";
        }

        @Override
        public String getKeyword() {
            return "root keyword";
        }

        @Override
        public String getName() {
            return "root name";
        }
    }
}
