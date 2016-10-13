package com.perceivedev.perceivecore.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * A Command tree
 */
public class CommandTree {

    private TreeRoot root;

    /**
     * Creates a new Command Tree
     */
    public CommandTree() {
        this.root = new TreeRoot();
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
     * @return The tab completion of the command or an empty optional if the command was not found or tab complete returned null.
     */
    public Optional<List<String>> findAndTabComplete(CommandSender sender, List<String> userChat) {
        return root.findAndTabComplete(sender, userChat);
    }

    /**
     * A private transparent tree root
     */
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
        protected CommandFindResult impl_find(CommandSender sender, Queue<String> query) {
            CommandFindResult chosenOne = new CommandFindResult(null, Collections.emptyList());

            String joinedQuery = query.stream().collect(Collectors.joining(" "));

            // now, is there any child that can continue
            for (CommandNode child : getChildren()) {
                // clone to prevent recursion to interfere with each other
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
    }
}
