package com.perceivedev.bukkitpluginutilities.modulesystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.base.Throwables;

/**
 * A module for this ModuleSystem
 */
public abstract class AbstractModule implements Module {

    protected Logger logger = Logger.getLogger(getClass().getName());

    private final String name, description, version;
    private final Set<String> dependencies;

    /**
     * @param name The name of the module
     * @param description The description of the module
     * @param version The version of the module.
     * @param dependencies The dependencies of this module. The {@link Class#getCanonicalName()} of the module class
     */
    public AbstractModule(String name, String description, String version, Collection<String> dependencies) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.dependencies = new HashSet<>(dependencies);
    }

    // @formatter:off
    /**
     * Loads the module from the properties file.
     * <p>
     * <br>
     * <b>Keys:</b>
     * <ul>
     *     <li>
     *         <b>module.name</b>
     *         <br>The name of the module
     *     </li>
     *     <li>
     *         <b>module.description</b>
     *         <br>The description of the module
     *     </li>
     *     <li>
     *         <b>module.version</b>
     *         <br>The version of the module
     *     </li>
     *     <li>
     *         <b>module.dependencies</b>
     *         <br>The dependencies of the module, separated by "|"
     *     </li>
     * </ul>
     *
     * @param moduleProperties The {@link Properties} file for the module
     */
    // @formatter:on
    public AbstractModule(Properties moduleProperties) {
        this(moduleProperties.getProperty("module.name"),
                moduleProperties.getProperty("module.description"),
                moduleProperties.getProperty("module.version"),
                Arrays.asList(moduleProperties.getProperty("module.dependencies").split("\\|")));
    }

    /**
     * The version of the module
     * <p>
     * <br>Format: {@code "Major.Minor.Patch[-suffix]"}
     *
     * @return The module's version
     */
    @Override
    public String getModuleVersion() {
        return version;
    }

    /**
     * @return The description of the module
     */
    @Override
    public String getModuleDescription() {
        return description;
    }

    /**
     * @return The name of the module
     */
    @Override
    public String getModuleName() {
        return name;
    }

    /**
     * @return The dependencies of this module. The {@link Class#getCanonicalName()} of the module class
     */
    @Override
    public Set<String> getModuleDependencies() {
        return dependencies;
    }

    /**
     * Returns the module's {@link Logger}
     *
     * @return The logger for the module
     */
    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * Checks if a module is compatible with the server version
     * <p>
     * The default implementation returns {@code true}, overwrite if needed
     *
     * @return True if this module is compatible with the server version
     */
    @Override
    public boolean isModuleCompatible() {
        return true;
    }

    /**
     * Loads the module.properties using the given {@link ClassLoader}
     *
     * @param clazz The {@link Class} of the module to use
     *
     * @return The loaded Properties
     *
     * @throws RuntimeException wrapping a IO exception
     */
    protected static Properties getModulePropertiesFromJar(Class<? extends Module> clazz) {
        try (InputStream inputStream = clazz.getResourceAsStream(
                "/" + clazz.getPackage()
                        .getName().replace(".", "/") + "/module.properties"
        )) {
            Properties properties = new Properties();
            properties.load(inputStream);

            return properties;
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public String toString() {
        return "AbstractModule{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", version='" + version + '\'' +
                ", dependencies=" + dependencies +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractModule)) {
            return false;
        }
        AbstractModule that = (AbstractModule) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, version);
    }
}
