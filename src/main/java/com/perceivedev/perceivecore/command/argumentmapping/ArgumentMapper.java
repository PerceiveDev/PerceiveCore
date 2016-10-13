package com.perceivedev.perceivecore.command.argumentmapping;

import java.util.Optional;
import java.util.Queue;

/**
 * An argument mapper
 */
public interface ArgumentMapper<T> {

    /**
     * Returns the class it translates to
     *
     * @return The class it translates to
     */
    Class<T> getTargetClass();

    /**
     * Maps an object
     *
     * @param strings The Strings to get the information from. Remove what you need, it will be passed to the next mapper afterwards
     *
     * @return The Mapped object
     */
    Optional<? extends T> map(Queue<String> strings);
}
