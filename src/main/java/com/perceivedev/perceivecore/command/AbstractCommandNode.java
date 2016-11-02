package com.perceivedev.perceivecore.command;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;

import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.command.argumentmapping.ArgumentMapper;
import com.perceivedev.perceivecore.command.argumentmapping.ArgumentMappers;
import com.perceivedev.perceivecore.command.argumentmapping.ConvertedParams;
import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.MethodPredicate;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.Modifier;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

/** A skeleton implementation for {@link CommandNode} */
public abstract class AbstractCommandNode implements CommandNode {

    private List<AbstractCommandNode>     children = new ArrayList<>();
    private Permission                    permission;
    private Collection<CommandSenderType> acceptedSenders;

    /**
     * Creates a new Command
     *
     * @param permission The Permission for this command
     * @param acceptedSenders The accepted {@link CommandSender}
     */
    public AbstractCommandNode(Permission permission, Collection<CommandSenderType> acceptedSenders) {
        if (acceptedSenders.isEmpty()) {
            throw new IllegalArgumentException("You must accept at least one sender");
        }

        this.permission = permission;
        this.acceptedSenders = EnumSet.copyOf(acceptedSenders);
    }

    /**
     * Creates a new Command
     *
     * @param permission The Permission for this command
     * @param acceptedSenders The accepted {@link CommandSender}
     *
     * @see #AbstractCommandNode(Permission, Collection)
     */
    public AbstractCommandNode(Permission permission, CommandSenderType... acceptedSenders) {
        this(permission, Arrays.asList(acceptedSenders));
    }

    /**
     * Creates a new Command
     *
     * @param permission The Permission for this command
     * @param acceptedSenders The accepted {@link CommandSender}
     *
     * @see #AbstractCommandNode(String, CommandSenderType...)
     */
    public AbstractCommandNode(String permission, CommandSenderType... acceptedSenders) {
        this(new Permission(permission), acceptedSenders);
    }

    // -------------------- Child Handling -------------------- //

    @Override
    public List<CommandNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public List<CommandNode> getAllChildren() {
        List<CommandNode> children = new ArrayList<>();
        children.addAll(getChildren());
        for (CommandNode commandNode : getChildren()) {
            children.addAll(commandNode.getChildren());
        }
        return children;
    }

    /**
     * Adds a child
     *
     * @param child The {@link CommandNode} to add
     */
    protected void addChild(AbstractCommandNode child) {
        children.add(child);
    }

    /**
     * Removes a child
     *
     * @param child The {@link CommandNode} to remove
     */
    protected void removeChild(AbstractCommandNode child) {
        children.remove(child);
    }

    @Override
    public CommandFindResult find(CommandSender sender, String commandQuery) {
        return find(sender, commandQuery.split(" "));
    }

    /**
     * Searches for a command
     *
     * @param sender The sender to search as
     * @param commandQuery The commandQuery to search for
     *
     * @return The found command
     *
     * @see #find(CommandSender, Collection)
     */
    public CommandFindResult find(CommandSender sender, String... commandQuery) {
        return find(sender, Arrays.asList(commandQuery));
    }

    /**
     * Searches for a command
     *
     * @param sender The sender to search as
     * @param commandQuery The commandQuery to search for
     *
     * @return The {@link CommandFindResult}
     */
    protected CommandFindResult find(CommandSender sender, Collection<String> commandQuery) {
        Queue<String> query = new ArrayDeque<>(commandQuery);
        return impl_find(sender, query);
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
        String keyword = query.poll();

        // nope, ends right here
        if (keyword == null || !isYourKeyword(keyword)) {
            return new CommandFindResult(null, Collections.emptyList());
        }
        // nope, doesn't work
        if (!hasPermission(sender) || !acceptsCommandSender(sender)) {
            return new CommandFindResult(null, Collections.emptyList());
        }

        CommandFindResult chosenOne = new CommandFindResult(this, new ArrayDeque<>(query));

        // now, is there any child that can continue
        for (AbstractCommandNode child : children) {
            // deepClone to prevent recursion to interfere with each other
            CommandFindResult findRes = child.impl_find(sender, new ArrayDeque<>(query));

            if (findRes.wasFound()) {
                // sweet, this child could take over. Let's honor it.
                chosenOne = findRes;
            }
        }

        return chosenOne;
    }

    // -------------------- Pre execution checks -------------------- //

    @Override
    public boolean hasPermission(Permissible permissible) {
        return permissible.hasPermission(permission);
    }

    @Override
    public Collection<CommandSenderType> getAcceptedCommandSenders() {
        return Collections.unmodifiableCollection(acceptedSenders);
    }

    @Override
    public boolean acceptsCommandSender(CommandSender commandSender) {
        return acceptedSenders.stream().anyMatch(senderType -> senderType.isThisType(commandSender));
    }

    // -------------------- Execution -------------------- //

    /**
     * Finds and then invokes a command
     *
     * @param sender The CommandSender to find it for
     * @param args The Arguments to pass
     *
     * @return The Result of invoking the command
     */
    public CommandResult findAndExecute(CommandSender sender, Collection<String> args) {
        CommandFindResult commandFindResult = find(sender, args);
        if (!commandFindResult.wasFound()) {
            return CommandResult.NOT_FOUND;
        }

        CommandResult result = commandFindResult.getCommandNode().execute(
                sender,
                commandFindResult.getRestArgs().toArray(new String[commandFindResult.getRestArgs().size()]));

        if (result == null) {
            PerceiveCore.getInstance().getLogger().log(Level.WARNING,
                    "Plugin returns null in on command " + commandFindResult.getCommandNode().getClass().getName());
        }
        return result == null ? CommandResult.ERROR : result;
    }

    /**
     * Finds and then invokes a command
     *
     * @param sender The CommandSender to find it for
     * @param query The Arguments to pass
     *
     * @return The Result of invoking the command
     *
     * @see #findAndExecute(CommandSender, Collection)
     */
    public CommandResult findAndExecute(CommandSender sender, String query) {
        return findAndExecute(sender, Arrays.asList(query.split(" ")));
    }

    @Override
    public CommandResult execute(CommandSender sender, String... args) {

        if (getMappedMethods(sender).count() > 0) {
            return invokeMappedMethod(sender, args);
        }

        // use the generic method
        if (getAcceptedCommandSenders().contains(CommandSenderType.ALL) || getAcceptedCommandSenders().contains(CommandSenderType.UNKNOWN)) {
            return executeGeneral(sender, args);
        }

        if (sender instanceof Player) {
            return executePlayer((Player) sender, args);
        } else if (sender instanceof BlockCommandSender) {
            return executeBlock((BlockCommandSender) sender, args);
        } else {
            return executeGeneral(sender, args);
        }
    }

    private CommandResult invokeMappedMethod(CommandSender sender, String... args) {
        Optional<Method> mappedMethod = getMappedMethods(sender).findAny();

        // no mapped method for this command sender
        if (!mappedMethod.isPresent()) {
            return CommandResult.NOT_FOUND;
        }
        Method method = mappedMethod.get();

        Queue<String> queue = new ArrayDeque<>(Arrays.asList(args));

        List<Object> objectList = new ArrayList<>();

        boolean wantsWholeChat = false;
        // populate objectList
        {
            ConvertedParams convertedParams = method.getAnnotation(ConvertedParams.class);

            // one for sender, one for String[] args
            if (convertedParams.targetClasses().length != method.getParameterCount() - 2) {

                // now, if it has an additional String at the end, don't sweat
                // it
                if (convertedParams.targetClasses().length != method.getParameterCount() - 3
                        || method.getParameterTypes()[method.getParameterCount() - 1] != String[].class) {

                    PerceiveCore.getInstance().getLogger()
                            .log(Level.SEVERE, "Argument length mismatch! Expected "
                                    + convertedParams.targetClasses().length
                                    + " params, got "
                                    + method.getParameterTypes().length
                                    + " in class "
                                    + getClass().getName());
                    return CommandResult.ERROR;
                } else {
                    wantsWholeChat = true;
                }
            }

            for (int i = 0; i < convertedParams.targetClasses().length; i++) {
                if (method.getParameterTypes()[i + 1] != convertedParams.targetClasses()[i]) {
                    PerceiveCore.getInstance().getLogger()
                            .log(Level.SEVERE, "Argument type mismatch! Expected "
                                    + convertedParams.targetClasses()[i].getName()
                                    + " got "
                                    + method.getParameterTypes()[i].getName()
                                    + " in class "
                                    + getClass().getName());
                    return CommandResult.ERROR;
                }
            }

            for (int counter = 0; !queue.isEmpty(); counter++) {

                if (counter >= convertedParams.targetClasses().length) {
                    // haven't got any more classes to convert. Got plenty
                    // arguments left though. They will be added to the array
                    // for the end.
                    break;
                }

                Class<?> clazz = convertedParams.targetClasses()[counter];
                Optional<ArgumentMapper<?>> mapperOptional = ArgumentMappers.getMapper(clazz);
                if (!mapperOptional.isPresent()) {
                    PerceiveCore.getInstance().getLogger()
                            .log(Level.SEVERE, "No ArgumentMapper for '" + clazz.getName() + "' in class " + getClass().getName()
                                    + ". Aborting command!");
                    // Well, the dev screwed up. Now, let's abort this.
                    return CommandResult.ERROR;
                }

                Optional<?> mapped = mapperOptional.get().map(queue);

                // user made an error...
                if (!mapped.isPresent()) {
                    objectList.add(null);
                    // return CommandResult.SEND_USAGE;
                } else {
                    objectList.add(mapped.get());
                }
            }

            // not enough params given. Send the usage
            if ((!wantsWholeChat && objectList.size() < method.getParameterCount() - 2)
                    || (wantsWholeChat && objectList.size() < method.getParameterCount() - 3)) {
                return CommandResult.SEND_USAGE;
            }
        }

        List<Object> params = new ArrayList<>();
        params.add(sender);
        params.addAll(objectList);
        params.add(queue.toArray(new String[0]));
        if (wantsWholeChat) {
            params.add(Arrays.copyOf(args, args.length));
        }

        ReflectResponse<Object> response = ReflectionUtil.invokeMethod(method, this, params.toArray(new Object[0]));
        if (!response.isValuePresent()) {
            PerceiveCore.getInstance().getLogger().log(Level.WARNING, "Command returned null: " + response.getException());
            return CommandResult.ERROR;
        }
        return (CommandResult) response.getValue();
    }

    private Stream<Method> getMappedMethods(CommandSender sender) {
        return ReflectionUtil.getMethods(this.getClass(), new MethodPredicate()
                .withModifiers(Modifier.PUBLIC)
                .withReturnType(CommandResult.class)
                .and(method -> method.isAnnotationPresent(ConvertedParams.class))
                .and(method -> method.getParameterCount() > 0)
                .and(method -> method.getParameterTypes()[0].isAssignableFrom(sender.getClass())));
    }

    /**
     * Executes this command
     * <p>
     * <br>
     * <b>Will be called when at least one is true:</b>
     * <ul>
     * <li>{@link #getAcceptedCommandSenders()} is more than one element</li>
     * <li>{@link #getAcceptedCommandSenders()} contains ALL or UNKNOWN</li>
     * </ul>
     *
     * @param sender The {@link CommandSender} to execute it as
     * @param args The Arguments for this command
     *
     * @return The Result of invoking this command
     */
    protected CommandResult executeGeneral(CommandSender sender, String... args) {
        return CommandResult.SEND_USAGE;
    }

    /**
     * Executes this command
     * <p>
     * <br>
     * <b>Will be called when:</b>
     * <ul>
     * <li>{@link #getAcceptedCommandSenders()} contains only one element,
     * PLAYER</li>
     * </ul>
     *
     * @param player The {@link Player} to execute it as
     * @param args The Arguments for this command
     *
     * @return The Result of invoking this command
     */
    protected CommandResult executePlayer(Player player, String... args) {
        return CommandResult.SEND_USAGE;
    }

    /**
     * Executes this command
     * <p>
     * <br>
     * <b>Will be called when:</b>
     * <ul>
     * <li>{@link #getAcceptedCommandSenders()} contains only one element, BLOCK
     * </li>
     * </ul>
     *
     * @param block The {@link BlockCommandSender} to execute it as
     * @param args The Arguments for this command
     *
     * @return The Result of invoking this command
     */
    protected CommandResult executeBlock(BlockCommandSender block, String... args) {
        return CommandResult.SEND_USAGE;
    }

    // -------------------- Tab completion -------------------- //

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
        CommandFindResult commandFindResult = find(sender, userChat);
        if (!commandFindResult.wasFound()) {
            return Optional.empty();
        }

        int index = userChat.size() - commandFindResult.getRestArgs().size();

        index--;    // index and stuff

        // if the user has just entered the command name, it is 0
        if (commandFindResult.getRestArgs().isEmpty()) {
            index = 0;
        }

        return Optional.ofNullable(
                commandFindResult.getCommandNode()
                        .tabComplete(
                                sender,
                                new ArrayList<>(userChat),
                                index));
    }

    /**
     * Finds a command, then tab completes it.
     *
     * @param sender The CommandSender to tab complete for
     * @param userChat The user chat
     *
     * @return The tab completion of the command or an empty optional if the
     *         command was not found or tab complete returned null.
     *
     * @see #findAndTabComplete(CommandSender, List)
     */
    public Optional<List<String>> findAndTabComplete(CommandSender sender, String userChat) {
        return findAndTabComplete(sender, Arrays.asList(userChat.split(" ")));
    }

    // -------------------- Shared utility methods -------------------- //

    /**
     * Returns the names of all players the CommandSender can see
     *
     * @param commandSender The CommandSender who wants to know them
     *
     * @return The names of all players he can see
     */
    protected List<String> getOnlinePlayerNames(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }

        return Bukkit.getOnlinePlayers().stream()
                .filter(((Player) commandSender)::canSee)
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
