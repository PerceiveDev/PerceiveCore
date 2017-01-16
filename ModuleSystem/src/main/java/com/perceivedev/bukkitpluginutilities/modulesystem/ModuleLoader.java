package com.perceivedev.bukkitpluginutilities.modulesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.UnknownDependencyException;

import com.google.common.base.Throwables;

/**
 * The module loader
 */
public class ModuleLoader {

    private Map<String, ModuleFile> moduleFileMap = new HashMap<>();
    private Set<String> loadedModuleMains = new HashSet<>();
    private Set<Module> loadedModules = new HashSet<>();
    private Path moduleFolder;
    private Logger logger;

    /**
     * @param moduleFolder The {@link Path} to load the modules from
     * @param logger The logger to use
     */
    public ModuleLoader(Path moduleFolder, Logger logger) {
        this.moduleFolder = moduleFolder;
        this.logger = logger;

        if (Files.notExists(moduleFolder)) {
            try {
                Files.createDirectories(moduleFolder);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Couldn't create module folder", e);
                throw Throwables.propagate(e);
            }
        }

        loadedModuleMains.add(ModuleSystemModule.class.getCanonicalName());
        Properties moduleSystemProperties = new Properties();
        moduleSystemProperties.setProperty("module.name", "ModuleSystem");
        moduleSystemProperties.setProperty("module.dependencies", "");
        moduleSystemProperties.setProperty("module.main", ModuleSystemModule.class.getCanonicalName());
        moduleFileMap.put(
                ModuleSystemModule.class.getCanonicalName(),
                new ModuleFile(moduleFolder, moduleSystemProperties)
        );
        ModuleManager.INSTANCE.registerModule(new ModuleSystemModule());
    }

    /**
     * Loads all modules
     *
     * @return False if a critical IO error prevent the loading of some module
     */
    public Set<Module> load() {
        try {
            Files.walkFileTree(moduleFolder, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!file.getFileName().toString().endsWith(".jar")) {
                        return FileVisitResult.CONTINUE;
                    }
                    try (JarFile jarFile = new JarFile(file.toFile())) {
                        JarEntry jarEntry = null;
                        {
                            Enumeration<JarEntry> entries = jarFile.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry tmp = entries.nextElement();
                                if (tmp.getName().endsWith("module.properties")) {
                                    jarEntry = tmp;
                                    break;
                                }
                            }
                        }

                        if (jarEntry == null) {
                            throw new FileNotFoundException("'module.properties' not found in jar root");
                        }

                        Properties properties = new Properties();
                        NioStreamReadUtil.doWithZipEntryStream(jarFile, jarEntry, inputStream -> {
                            try {
                                properties.load(inputStream);
                            } catch (IOException e) {
                                throw Throwables.propagate(e);
                            }
                            return Void.TYPE;
                        });

                        ModuleFile module = new ModuleFile(file, properties);
                        if (moduleFileMap.containsKey(module.getMain())) {
                            throw new IllegalStateException("Duplicate module: '"
                                    + file
                                    + "' is already loaded from file '"
                                    + moduleFileMap.get(module.getMain())
                                    + "'!");
                        }
                        moduleFileMap.put(module.getMain(), module);
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Error reading module: '" + file + "'", e);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    logger.log(Level.WARNING, "Error reading module: '" + file + "'", exc);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error loading modules", e);
            e.printStackTrace();
            return Collections.emptySet();
        }

        for (ModuleFile module : moduleFileMap.values()) {
            loadModule(module);
        }

        return loadedModules;
    }

    /**
     * Loads a module and all dependencies
     *
     * @param moduleFile The module to load
     */
    private void loadModule(ModuleFile moduleFile) {
        if (loadedModuleMains.contains(moduleFile.getMain())) {
            return;
        }
        loadedModuleMains.add(moduleFile.getMain());
        for (String dependency : moduleFile.getDependencies()) {
            ModuleFile parentDependency = moduleFileMap.values().stream()
                    .filter(moduleFile1 -> moduleFile1.getName().equals(dependency))
                    .limit(1)
                    .findAny()
                    .orElse(null);
            if (parentDependency == null) {
                logger.log(
                        Level.WARNING,
                        "Unknown dependency for file: '" + moduleFile.file + "'",
                        new UnknownDependencyException("Unknown dependency: '" + dependency + "'")
                );
                return;
            }
            loadModule(parentDependency);
        }

        Module module = getModuleFromFile(moduleFile);
        if (module != null) {
            loadedModules.add(module);
        }
    }

    private Module getModuleFromFile(ModuleFile moduleFile) {
        try {
            ModuleManager.INSTANCE.addToClassPath(moduleFile.getFile().toUri().toURL());
            Class<?> moduleMainClass = Class.forName(moduleFile.getMain());

            if (!ReflectionHelper.inheritsFrom(moduleMainClass, Module.class)) {
                throw new IllegalArgumentException("Module main class does not inherit from the 'Module' class!");
            }

            return ReflectionHelper.instantiate(moduleMainClass);
        } catch (MalformedURLException e) {
            logger.log(
                    Level.WARNING,
                    "Malformed URL while creating Module ClassLoader",
                    e
            );
        } catch (ClassNotFoundException e) {
            logger.log(
                    Level.WARNING,
                    "Error loading module '" + moduleFile.getName() + "'. Main class not found!",
                    new InvalidPluginException("Main class '" + moduleFile.getMain() + "' not found!")
            );
        } catch (Exception e) {
            logger.log(
                    Level.WARNING,
                    "Error loading module '" + moduleFile.getName() + "'.",
                    e
            );
        }

        return null;
    }

    private static class ModuleFile {
        private Path file;
        private Set<String> dependencies;
        private String main, name;

        private ModuleFile(Path file, Properties properties) {
            this.file = file;

            dependencies = Arrays.stream(
                    properties.getProperty("module.dependencies")
                            .split("\\|")
            )
                    .filter(string -> !string.isEmpty())
                    .collect(Collectors.toSet());
            main = properties.getProperty("module.main");
            name = properties.getProperty("module.name");
        }

        /**
         * @return The dependencies of the module
         */
        private Set<String> getDependencies() {
            return dependencies;
        }

        /**
         * @return The Jar file of the module
         */
        private Path getFile() {
            return file;
        }

        private String getMain() {
            return main;
        }

        private String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ModuleFile)) {
                return false;
            }
            ModuleFile that = (ModuleFile) o;
            return Objects.equals(file, that.file);
        }

        @Override
        public int hashCode() {
            return Objects.hash(file);
        }
    }
}
