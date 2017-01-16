package me.ialistannen.bukkitpluginutilities.modulesystem;

import java.util.Set;
import java.util.logging.Logger;


/**
 * A module for the {@link ModuleSystemModule} plugin
 * <p>
 * The module must implement {@link Object#equals(Object)} and {@link Object#hashCode()} in a sensible way!
 */
public interface Module {

    /**
     * The version of the module
     * <p>
     * <br>Format: {@code "Major.Minor.Patch[-suffix]"}
     *
     * @return The module's version
     */
    @SuppressWarnings("unused")
    String getModuleVersion();

    /**
     * @return The description of the module
     */
    @SuppressWarnings("unused")
    String getModuleDescription();

    /**
     * @return The name of the module
     */
    String getModuleName();

    /**
     * @return The dependencies of this module. The {@link Class#getCanonicalName()} of the module class
     */
    @SuppressWarnings("unused")
    Set<String> getModuleDependencies();

    /**
     * Returns the module's {@link Logger}
     *
     * @return The logger for the module
     */
    Logger getLogger();

    /**
     * Checks if a module is compatible with the server version
     *
     * @return True if this module is compatible with the server version
     */
    @SuppressWarnings("unused")
    boolean isModuleCompatible();
    // TODO: 13.01.2017 Implement compatibility check 
}
