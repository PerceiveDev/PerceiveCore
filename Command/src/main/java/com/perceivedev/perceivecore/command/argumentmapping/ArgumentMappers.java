package com.perceivedev.perceivecore.command.argumentmapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

// @formatter:off
/**
 * Contains all ArgumentMappers
 * <p>
 * <br>
 * <b><i>Default mappers:</i></b>
 * <ul>
 *     <li>{@link EntityType}</li>
 *     <li>{@link Player}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Double}</li>
 * </ul>
 */
// @formatter:on
public class ArgumentMappers {

    private static Map<Class<?>, ArgumentMapper<?>> mappers = new HashMap<>();

    static {
        addDefaults();
    }

    /**
     * Adds an {@link ArgumentMapper}
     *
     * @param mapper The {@link ArgumentMapper} to add
     */
    @SuppressWarnings("WeakerAccess")
    public static void addMapper(ArgumentMapper<?> mapper) {
        mappers.put(mapper.getTargetClass(), mapper);
    }

    /**
     * Removes an {@link ArgumentMapper}
     *
     * @param clazz The Class of the mapper to remove
     */
    @SuppressWarnings("unused")
    public static void removeArgumentMapper(Class<?> clazz) {
        mappers.remove(clazz);
    }

    /**
     * Returns the ArgumentMapper
     *
     * @param clazz The class of the Mapper
     *
     * @return The ArgumentMapper for this class, if any
     */
    public static Optional<ArgumentMapper<?>> getMapper(Class<?> clazz) {
        return Optional.ofNullable(mappers.get(clazz));
    }

    /**
     * Adds the default handles
     */
    private static void addDefaults() {
        // ==== PLAYER ====
        ArgumentMappers.addMapper(new ArgumentMapper<Player>() {

            @Override
            public Class<Player> getTargetClass() {
                return Player.class;
            }

            @Override
            public Optional<? extends Player> map(Queue<String> strings) {
                if (strings.isEmpty()) {
                    return Optional.empty();
                }
                String name = strings.poll();

                return Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.getName().equals(name) || player.getDisplayName().equals(name))
                        .findFirst();
            }
        });

        // ==== ENTITY TYPE ====
        ArgumentMappers.addMapper(new ArgumentMapper<EntityType>() {

            @Override
            public Class<EntityType> getTargetClass() {
                return EntityType.class;
            }

            @Override
            public Optional<EntityType> map(Queue<String> strings) {
                if (strings.isEmpty()) {
                    return Optional.empty();
                }
                String name = strings.poll();

                return Arrays.stream(EntityType.values())
                        .filter(entityType -> entityType.name().equalsIgnoreCase(name))
                        .findAny();
            }
        });

        // ==== INTEGER ====
        ArgumentMappers.addMapper(new ArgumentMapper<Integer>() {

            @Override
            public Class<Integer> getTargetClass() {
                return Integer.class;
            }

            @Override
            public Optional<Integer> map(Queue<String> strings) {
                if (strings.isEmpty()) {
                    return Optional.empty();
                }
                String name = strings.poll();
                try {
                    return Optional.of(Integer.valueOf(name));
                } catch (NumberFormatException ignored) {

                }
                return Optional.empty();
            }
        });

        // ==== DOUBLE ====
        ArgumentMappers.addMapper(new ArgumentMapper<Double>() {

            @Override
            public Class<Double> getTargetClass() {
                return Double.class;
            }

            @Override
            public Optional<Double> map(Queue<String> strings) {
                if (strings.isEmpty()) {
                    return Optional.empty();
                }
                String name = strings.poll();
                try {
                    return Optional.of(Double.valueOf(name));
                } catch (NumberFormatException ignored) {

                }
                return Optional.empty();
            }
        });
    }
}
