package com.perceivedev.perceivecore.command;

import static com.perceivedev.perceivecore.util.TextUtils.colorize;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import com.perceivedev.perceivecore.language.MessageProvider;
import com.perceivedev.perceivecore.other.Pager;
import com.perceivedev.perceivecore.other.Pager.Options;
import com.perceivedev.perceivecore.other.Pager.Page;
import com.perceivedev.perceivecore.other.Pager.PagerFilterable;
import com.perceivedev.perceivecore.other.Pager.SearchMode;
import com.perceivedev.perceivecore.util.TextUtils;

// @formatter:off I WANT THAT FORMATTING
/**
 * The default help command
 * <p>
 * <br>
 * <b>Language:</b>
 * Base key: "command.help"
 * <br>Needs all keys from {@link TranslatedCommandNode} 
 * <p>
 * Keys:
 * <ul>
 *     <li>"command.help.format.with.usage" ==> The help format with the usage.
 *       <br><b>Default:</b> <i>"&3{0}&9: &7{1} &7<&6{2}&7><newline>  &cUsage: {3}"</i>
 *       <br><b>Format parameters:</b>
 *     </li>
 *       <ol>
 *           <li>Name</li>
 *           <li>Description</li>
 *           <li>Children amount</li>
 *           <li>Usage</li>
 *       </ol>
 *     <li>"command.help.format.without.usage" ==> The help format without the usage.
 *       <br><b>Default: </b> <i>"&3{0}&9: &7{1} &7<&6{2}&7>"</i>
 *       <br><b>Format parameters:</b>
 *       <ol>
 *           <li>Name</li>
 *           <li>Description</li>
 *           <li>Children amount</li>
 *           <li>Usage</li>
 *       </ol>
 *     </li>
 *     <li>"command.help.top.level.prefix" ==> The prefix for a top level command
 *       <br><b>Default:</b> <i>Nothing</i>
 *     </li>
 *     <li>"command.help.sub.level.prefix" ==> The prefix for a sub level command
 *       <br><b>Default:</b> <i>Nothing</i>
 *     </li>
 *     <li>"command.help.padding.char"     ==> The padding char
 *       <br><b>Default:</b> <i>Space</i>
 *     </li>
 * </ul>
 */
// @formatter:on
public class DefaultHelpCommand extends TranslatedCommandNode {

    private OptionsParser parser = new OptionsParser();
    private CommandTree   commandTree;

    /**
     * The base key will be "command.help". <br>
     * For all keys used by this command, look at the class javadoc:
     * {@link DefaultHelpCommand}
     *
     * @param permission The Permission to use
     * @param messageProvider The {@link MessageProvider} to use
     * @param commandTree The {@link CommandTree} to make it for
     */
    public DefaultHelpCommand(Permission permission, MessageProvider messageProvider, CommandTree commandTree) {
        super(permission, "command.help", messageProvider, CommandSenderType.ALL);
        this.commandTree = commandTree;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> wholeChat, int relativeIndex) {
        return Arrays.asList("--depth=", "--page=", "--entriesPerPage=", "--showUsage=",
                "--search=", "--regex="); // , "--contains=", "--regexFind=",
                                          // "--regexMatch=", "--regexMatchCI="
    }

    @Override
    protected CommandResult executeGeneral(CommandSender sender, String... args) {
        Options options = parser.parse(args);
        AtomicInteger depth = new AtomicInteger(10);
        AtomicBoolean showUsage = new AtomicBoolean(false);

        for (String argument : args) {
            if (argument.matches("--depth=.+")) {
                try {
                    depth.set(Integer.parseInt(argument.replace("--depth=", "")));
                } catch (NumberFormatException ignored) {

                }
            } else if (argument.matches("--showUsage=.+")) {
                showUsage.set(Boolean.parseBoolean(argument.replace("--showUsage=", "")));
            }
        }

        CommandNode node = commandTree.getRoot();

        // use the targeted node, if any
        {
            CommandFindResult findResult = commandTree.find(sender, args);
            if (findResult.wasFound()) {
                node = findResult.getCommandNode();
            }
        }

        List<PagerFilterable> filterable = getCommandFilterable(getMessageProvider(), node, commandTree, showUsage.get(), depth.get(), 0);

        Page page = Pager.getPageFromFilterable(options, filterable);

        page.send(sender, getMessageProvider());

        return CommandResult.SUCCESSFULLY_INVOKED;
    }

    /**
     * Sends help for one command
     *
     * @param language The language to use for the key translation
     * @param node The CommandNode to start with
     * @param withUsage If true, the usage will be shown
     * @param maxDepth The maximum depth. Index based. 0 ==> Just this command,
     *            1 ==> Command and children
     * @param counter The current counter. Just supply 0. Used for recursion.
     */
    private static List<PagerFilterable> getCommandFilterable(MessageProvider language, CommandNode node, CommandTree tree,
            boolean withUsage, int maxDepth,
            int counter) {
        List<PagerFilterable> list = new ArrayList<>();

        if (!tree.isRoot(node)) {
            PagerFilterable filterable = new CommandFilterable(node, withUsage, node.getChildren().size(),
                    language, counter);
            list.add(filterable);
        } else {
            counter--;
        }

        if (counter >= maxDepth) {
            return list;
        }

        for (CommandNode commandNode : node.getChildren()) {
            list.addAll(getCommandFilterable(language, commandNode, tree, withUsage, maxDepth, counter + 1));
        }

        return list;
    }

    private static class CommandFilterable implements PagerFilterable {

        private CommandNode     node;
        private boolean         showUsage;
        private String          childrenAmount;
        private MessageProvider language;
        private int             depth;

        private List<String>    allLines;

        CommandFilterable(CommandNode node, boolean showUsage, int childrenAmount,
                MessageProvider language, int depth) {
            this.node = node;
            this.showUsage = showUsage;
            this.childrenAmount = childrenAmount == 0 ? "" : Integer.toString(childrenAmount);
            this.language = language;
            this.depth = depth;

            calculateAllLines();
        }

        @Override
        public boolean accepts(Options options) {
            // match against what is shown
            for (String line : allLines) {
                if (options.matchesPattern(strip(line))) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @param coloredString The String to strip the colors from
         *
         * @return The uncolored String
         */
        private static String strip(String coloredString) {
            return ChatColor.stripColor(coloredString);
        }

        @SuppressWarnings("StringConcatenationInLoop")
        private void calculateAllLines() {
            String finalString;
            {
                if (showUsage) {
                    String key = "command.help.format.with.usage";
                    finalString = language.trOrDefault(key,
                            "&3{0}&9: &7{1} &7<&6{2}&7><newline>  &cUsage: {3}",
                            new Object[] { node.getName(), node.getDescription(), childrenAmount, node.getUsage() });
                } else {
                    String key = "command.help.format.without.usage";
                    finalString = language.trOrDefault(key,
                            "&3{0}&9: &7{1} &7<&6{2}&7>",
                            new Object[] { node.getName(), node.getDescription(), childrenAmount, node.getUsage() });
                }
                finalString = colorize(finalString);
            }

            List<String> list = new ArrayList<>();

            for (String s : finalString.split("<newline>")) {
                if (depth == 0) {
                    s = colorize(language.trOrDefault("command.help.top.level.prefix", "")) + s;
                } else {
                    s = colorize(language.trOrDefault("command.help.sub.level.prefix", "")) + s;
                }
                s = TextUtils.repeat(language.trOrDefault("command.help.padding.char", "  "), depth) + s;

                if (!s.isEmpty()) {
                    list.add(s);
                }
            }

            allLines = list;
        }

        @Override
        public @Nonnull List<String> getAllLines() {
            return allLines;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CommandFilterable)) {
                return false;
            }
            CommandFilterable that = (CommandFilterable) o;
            return Objects.equals(node, that.node);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node);
        }

        @Override
        public String toString() {
            return "CommandFilterable{" +
                    "node=" + node.getName() +
                    ", showUsage=" + showUsage +
                    ", childrenAmount='" + childrenAmount + '\'' +
                    ", depth=" + depth +
                    ", allLines=" + getAllLines() +
                    '}';
        }
    }

    /** Parses the Builder options */
    private static class OptionsParser {
        private Map<Pattern, BiConsumer<String, Options.Builder>> optionsParserMap = new HashMap<>();

        {
            optionsParserMap.put(Pattern.compile("--page=.+", CASE_INSENSITIVE), (s, builder) -> {
                String page = s.replace("--page=", "");
                Integer integer = toInt(page);
                if (integer != null) {
                    builder.setPageIndex(integer);
                }
            });

            optionsParserMap.put(Pattern.compile("--entriesPerPage=.+", CASE_INSENSITIVE), (s, builder) -> {
                String entries = s.replace("--entriesPerPage=", "");
                Integer integer = toInt(entries);
                if (integer != null) {
                    builder.setEntriesPerPage(integer);
                }
            });

            optionsParserMap.put(Pattern.compile("--search=.+", CASE_INSENSITIVE), (s, builder) -> {
                String entries = s.replace("--search=", "");
                builder.setSearchModes(SearchMode.CONTAINS_IGNORE_CASE);
                builder.setSearchPattern(entries);
            });

            optionsParserMap.put(Pattern.compile("--regex=.+", CASE_INSENSITIVE), (s, builder) -> {
                String entries = s.replace("--regex=", "");
                builder.setSearchModes(SearchMode.REGEX_FIND_CASE_INSENSITIVE);
                builder.setSearchPattern(entries);
            });

            // special options (not shown, but there :P)

            optionsParserMap.put(Pattern.compile("--contains=.+", CASE_INSENSITIVE), (s, builder) -> {
                String entries = s.replace("--contains=", "");
                builder.setSearchModes(SearchMode.CONTAINS);
                builder.setSearchPattern(entries);
            });

            optionsParserMap.put(Pattern.compile("--regexFind=.+", CASE_INSENSITIVE), (s, builder) -> {
                String entries = s.replace("--regexFind=", "");
                builder.setSearchModes(SearchMode.REGEX_FIND);
                builder.setSearchPattern(entries);
            });

            optionsParserMap.put(Pattern.compile("--regexMatch=.+", CASE_INSENSITIVE), (s, builder) -> {
                String entries = s.replace("--regexMatch=", "");
                builder.setSearchModes(SearchMode.REGEX_MATCHES);
                builder.setSearchPattern(entries);
            });

            optionsParserMap.put(Pattern.compile("--regexMatchCI=.+", CASE_INSENSITIVE), (s, builder) -> {
                String entries = s.replace("--regexMatchCI=", "");
                builder.setSearchModes(SearchMode.REGEX_MATCHES_CASE_INSENSITIVE);
                builder.setSearchPattern(entries);
            });
        }

        /**
         * Parses the options
         *
         * @param args The Arguments to parse
         *
         * @return The parsed options
         */
        public Options parse(String[] args) {
            Options.Builder builder = Options.builder();
            for (String argument : args) {
                for (Entry<Pattern, BiConsumer<String, Options.Builder>> entry : optionsParserMap.entrySet()) {
                    Matcher matcher = entry.getKey().matcher(argument);
                    if (matcher.find()) {
                        entry.getValue().accept(argument, builder);
                    }
                }
            }
            return builder.build();
        }

        /**
         * @param string The String to convert
         *
         * @return The converted Integer or null if not parsable
         */
        private static Integer toInt(String string) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
