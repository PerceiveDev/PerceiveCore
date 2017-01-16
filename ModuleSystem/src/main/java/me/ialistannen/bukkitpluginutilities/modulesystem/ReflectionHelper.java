package me.ialistannen.bukkitpluginutilities.modulesystem;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A reflection helper
 */
class ReflectionHelper {

    private static final Logger LOGGER = Logger.getLogger("ReflectionHelper");

    /**
     * Checks if a given class <i>somehow</i> inherits from another class
     *
     * @param toCheck The class to check
     * @param inheritedClass The inherited class, it should have
     *
     * @return True if {@code toCheck} somehow inherits from
     * {@code inheritedClass}
     */
    static boolean inheritsFrom(Class<?> toCheck, Class<?> inheritedClass) {
        if (inheritedClass.isAssignableFrom(toCheck)) {
            return true;
        }

        for (Class<?> implementedInterface : toCheck.getInterfaces()) {
            if (inheritsFrom(implementedInterface, inheritedClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Instantiates a class using the default constructor
     *
     * @param clazz The {@link Class} to instantiate
     * @param <T> The type of the class
     *
     * @return The class instance
     */
    static <T> T instantiate(Class<?> clazz) {
        try {
            @SuppressWarnings("unchecked")
            T newInstance = (T) clazz.newInstance();
            return newInstance;
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, "Reflection error!", e);
        }
        return null;
    }
}
