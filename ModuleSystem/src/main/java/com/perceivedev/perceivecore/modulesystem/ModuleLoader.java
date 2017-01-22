package com.perceivedev.perceivecore.modulesystem;

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
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.plugin.InvalidPluginException;

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

    private Queue<PostponedMessage> postponedMessages = new LinkedList<>();

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
     * @return All the messages this loader postponed, as nobody looks <i>that</i> far up in a log.
     */
    public Queue<PostponedMessage> getPostponedMessages() {
        return postponedMessages;
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
                            String reason = String.format(
                                    Locale.ROOT,
                                    "&6Duplicate module: '&a%s&6' is already loaded from file '&a%s&6'!",
                                    file.toString(), moduleFileMap.get(module.getMain()).getFile().toString()
                            );
                            postponedMessages.add(new PostponedMessage(
                                    "&6Duplicate module",
                                    reason,
                                    "&6A module was loaded twice. There may be two jars in the module folder",
                                    "&6Delete one of the two files mentioned in the 'Reason' part of the error",
                                    Level.WARNING
                            ));

                            throw new IllegalStateException("Duplicate module: '"
                                    + file
                                    + "' is already loaded from file '"
                                    + moduleFileMap.get(module.getMain()).getFile().toString()
                                    + "'!");
                        }
                        moduleFileMap.put(module.getMain(), module);
                    } catch (Exception e) {
                        postponedMessages.add(
                                new PostponedMessage(
                                        "&6Error reading module",
                                        "&6An error occurred while loading the module: " + e.getMessage(),
                                        "&6An unknown error occurred. More information is higher in this log.",
                                        null,
                                        Level.WARNING
                                )
                        );
                        logger.log(Level.WARNING, "Error loading module", e);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    postponedMessages.add(
                            new PostponedMessage(
                                    "&6Error reading a module file",
                                    "&6Error reading module: '&a" + file + "&6'",
                                    "&6Some error occurred while reading the file. More information is " +
                                            "higher in this log",
                                    null,
                                    Level.WARNING
                            )
                    );
                    logger.log(Level.WARNING, "Error reading module file", exc);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            postponedMessages.add(
                    new PostponedMessage(
                            "&6I/O Error reading module.",
                            "&6An I/O error occurred reading a module: " + e.getMessage(),
                            "&6Some error occurred while reading the file. More information is " +
                                    "higher in this log",
                            null,
                            Level.WARNING
                    )
            );
            logger.log(Level.WARNING, "I/O Error reading module", e);
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
                String reason = "&6Unknown dependency for file: '&a" + moduleFile.file + "&6'";
                postponedMessages.add(new PostponedMessage(
                        "&6Unknown dependency",
                        reason,
                        String.format(Locale.ROOT,
                                "&6The module '&a%s&6' needs the dependency '&a%s&6', but it could not be found.",
                                moduleFile.getName(), dependency
                        ),
                        String.format(Locale.ROOT,
                                "&6Download the module '&a%s&6' and copy it in the modules folder",
                                dependency
                        ),
                        Level.WARNING
                ));
                return;
            }
            loadModule(parentDependency);
        }

        Module module = getModuleFromFile(moduleFile);
        if (module == null) {
            return;
        }
        if (!module.isModuleCompatible()) {
            String reason = String.format(Locale.ROOT,
                    "&6The module '&a%s&6' is not compatible with your server version. It will not be enabled!",
                    module.getModuleName()
            );
            postponedMessages.add(new PostponedMessage(
                    "&6Module incompatible",
                    reason,
                    String.format(Locale.ROOT,
                            "&6The module '&a%s&6' is not compatible with your minecraft version.",
                            module.getModuleName()
                    ),
                    "&6Check if a newer version of the module is available.",
                    Level.WARNING
            ));
            return;
        }
        loadedModules.add(module);
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
            postponedMessages.add(new PostponedMessage(
                    "&6Malformed URL",
                    "&6Malformed URL while creating Module ClassLoader",
                    String.format(Locale.ROOT,
                            "&6The url to the module file ('&a%s&6') is not valid.",
                            moduleFile.getFile().toAbsolutePath().toString()
                    ),
                    "&6Report this!",
                    Level.WARNING
            ));
        } catch (ClassNotFoundException e) {
            logger.log(
                    Level.WARNING,
                    "Error loading module '" + moduleFile.getName() + "'. Main class not found!",
                    new InvalidPluginException("Main class '" + moduleFile.getMain() + "' not found!")
            );
            postponedMessages.add(new PostponedMessage(
                    "&6Class not found",
                    String.format(Locale.ROOT,
                            "&6The main class of the module '&a%s&6'was not found.",
                            moduleFile.getMain()
                    ),
                    "&6The main class is needed to load the module, but it wasn't where I was told :(",
                    "&6Report the error. The module jar file probably needs to be corrected.",
                    Level.WARNING
            ));
        } catch (Exception e) {
            logger.log(
                    Level.WARNING,
                    "Error loading module '" + moduleFile.getName() + "'.",
                    e
            );
            postponedMessages.add(new PostponedMessage(
                    "&6An error occurred loading a module",
                    String.format(Locale.ROOT,
                            "&6The module '&a%s&6' could not be loaded: " + e.getMessage(),
                            moduleFile.getName()
                    ),
                    "&6An unknown error occurred. More information is higher in this log.",
                    null,
                    Level.WARNING
            ));
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

    /**
     * A postponed message
     */
    public static class PostponedMessage {
        private String error, reason, description, solution;
        private Level level;

        /**
         * @param error The error
         * @param reason The reason for the error
         * @param description A description of the error
         * @param solution A solution for it
         * @param level The level of the error
         */
        PostponedMessage(String error, String reason, String description, String solution, Level level) {
            this.error = error;
            this.reason = reason;
            this.description = description;
            this.solution = solution;
            this.level = level;
        }

        /**
         * @return The error
         */
        public String getError() {
            return error;
        }

        /**
         * @return The reason for the error
         */
        public String getReason() {
            return reason;
        }

        /**
         * @return A description of the error
         */
        public String getDescription() {
            return description;
        }

        /**
         * @return A solution for the error
         */
        public String getSolution() {
            return solution;
        }

        /**
         * @return The {@link Level} of the message
         */
        public Level getLevel() {
            return level;
        }
    }
}
