package com.perceivedev.perceivecore.language;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * An implementation of the {@link MessageProvider} using
 */
public class I18N implements MessageProvider {

    /**
     * Regex it find things references e.g.
     * "Test 1234 [[path.to.other.message]]" ==> Matches
     * "[[path.to.other.message]]"
     */
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("(?<=\\[\\[)(.+?)(?=\\]\\])");

    private Set<String>                 categories          = new HashSet<>();
    private Map<String, ResourceBundle> fileResourceBundles = new HashMap<>();
    private Map<String, ResourceBundle> jarResourceBundles  = new HashMap<>();

    private Locale currentLanguage;
    private String basePackage;

    private ClassLoader callerClassLoader, fileClassLoader;

    private String defaultCategory;

    /**
     * Cache to increase performance. May be left out.
     */
    private Map<String, MessageFormat> messageFormatCache = new HashMap<>();

    /**
     * @param currentLanguage The current language
     * @param basePackage The base package in the jar to read from
     * @param savePath The save path
     * @param callerClassLoader Your class loader. Needed to query packages from
     * your jar file
     * @param defaultCategory The default category
     * @param more More categories
     *
     * @throws NullPointerException If any parameter is null
     */
    public I18N(Locale currentLanguage, String basePackage, Path savePath, ClassLoader callerClassLoader, String defaultCategory, String... more) {

        Objects.requireNonNull(currentLanguage);
        Objects.requireNonNull(basePackage);
        Objects.requireNonNull(savePath);
        Objects.requireNonNull(callerClassLoader);
        Objects.requireNonNull(defaultCategory);
        Objects.requireNonNull(more);

        this.currentLanguage = currentLanguage;
        this.basePackage = basePackage;
        this.callerClassLoader = callerClassLoader;

        this.categories.add(defaultCategory);

        this.defaultCategory = defaultCategory;

        fileClassLoader = new FileClassLoader(savePath);

        createBundles();
    }

    public I18N(JavaPlugin plugin, String basePackage) {
        this(Locale.ENGLISH, basePackage, ensureDirExists(plugin.getDataFolder().toPath().resolve("language")), plugin.getClass().getClassLoader(), "Messages");
    }

    private static Path ensureDirExists(Path path) {
        try {
            Files.createDirectories(path);
            return path;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates the bundles for all categories
     */
    private void createBundles() {
        ResourceBundle.clearCache(callerClassLoader);
        ResourceBundle.clearCache(fileClassLoader);

        jarResourceBundles.clear();
        fileResourceBundles.clear();

        messageFormatCache.clear();

        categories.forEach(category -> {
            if (!createBundle(category)) {
                System.out.println("Not found: " + category);
                // TODO: Log this properly
            }
        });
    }

    /**
     * Creates a bundle
     *
     * @param category The category
     *
     * @return True if the bundle was found in the jar or on file.
     */
    private boolean createBundle(String category) {
        try {
            ResourceBundle jarBundle = ResourceBundle.getBundle(basePackage + "." + category, currentLanguage, callerClassLoader);

            jarResourceBundles.put(category, jarBundle);
        } catch (MissingResourceException ignored) {
        }

        try {
            ResourceBundle fileBundle = ResourceBundle.getBundle(category, currentLanguage, fileClassLoader);

            fileResourceBundles.put(category, fileBundle);
        } catch (MissingResourceException ignored) {
        }

        return jarResourceBundles.containsKey(category) || fileResourceBundles.containsKey(category);
    }

    /**
     * Translates a String
     *
     * @param key The key
     * @param category The category
     *
     * @return The translated String
     *
     * @throws IllegalArgumentException If the category isn't in
     * {@link #categories}
     */
    private String translate(String key, String category) {
        Optional<String> translated = translateOrEmpty(key, category);
        if (translated.isPresent()) {
            return translated.get();
        }
        return "No translation for [" + key + "]";
    }

    /**
     * Translates a String
     *
     * @param key The key
     * @param category The category
     *
     * @return The translated String OR an empty Optional if not found
     *
     * @throws IllegalArgumentException If the category isn't in
     * {@link #categories}
     */
    private Optional<String> translateOrEmpty(String key, String category) {
        if (!categories.contains(category)) {
            throw new IllegalArgumentException("Category unknown!");
        }

        if (fileResourceBundles.containsKey(category)) {
            try {
                return Optional.of(fileResourceBundles.get(category).getString(key));
            } catch (MissingResourceException ignored) {
            }
        }

        try {
            return Optional.of(jarResourceBundles.get(category).getString(key));
        } catch (MissingResourceException e) {
            return Optional.empty();
        }
    }

    /**
     * Formats a (translated) string
     *
     * @param pattern The pattern
     * @param formattingObjects The formattingObjects
     *
     * @return The formatted String
     */
    private String format(String pattern, Object... formattingObjects) {
        MessageFormat format;
        if (messageFormatCache.containsKey(pattern)) {
            format = messageFormatCache.get(pattern);
        } else {
            try {
                format = new MessageFormat(pattern, getLanguage());
            } catch (IllegalArgumentException e) {
                String fixedPattern = pattern.replaceAll("\\{\\d.+?\\}", "[$1]");
                format = new MessageFormat(fixedPattern, getLanguage());
            }

            messageFormatCache.put(pattern, format);
        }

        return format.format(formattingObjects);
    }

    private String resolveReferences(String string, String category) {
        String result = string;
        Matcher matcher = REFERENCE_PATTERN.matcher(string);
        while (matcher.find()) {
            String found = matcher.group(1);
            String resolved = tr(found, category);
            result = result.replace("[[" + found + "]]", resolved);
        }
        return result;
    }

    @Override
    public String trOrDefault(String key, String category, String defaultString, Object... formattingObjects) {
        if (!categories.contains(category)) {
            throw new IllegalArgumentException("Unknown category");
        }

        Optional<String> formatted = translateOrEmpty(key, category);
        if (formatted.isPresent()) {
            return format(formatted.get(), formattingObjects);
        }
        return format(defaultString, formattingObjects);
    }

    /**
     * @param key The key
     * @param category The category it belongs to
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated, uncolored String
     *
     * @throws IllegalArgumentException If the category is unknown
     * @throws NullPointerException If any parameter is null
     */
    @Override
    public String trUncolored(String key, String category, Object... formattingObjects) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(category);
        Objects.requireNonNull(formattingObjects);

        if (!categories.contains(category)) {
            throw new IllegalArgumentException("Unknown category");
        }

        String formatted = format(translate(key, category), formattingObjects);

        formatted = resolveReferences(formatted, category);

        return formatted;
    }

    /**
     * @param key The key
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated, uncolored String
     *
     * @see #trUncolored(String, String, Object...)
     */
    @Override
    public String trUncolored(String key, Object... formattingObjects) {
        return trUncolored(key, defaultCategory, formattingObjects);
    }

    @Override
    public boolean setDefaultCategory(String categoryName) {
        Objects.requireNonNull(categoryName);
        if (!categories.contains(categoryName)) {
            return false;
        }

        defaultCategory = categoryName;
        return true;
    }

    @Override
    public String getDefaultCategory() {
        return defaultCategory;
    }

    @Override
    public void addCategory(String category) {
        Objects.requireNonNull(category);
        if (categories.contains(category)) {
            return;
        }
        categories.add(category);
        createBundle(category);
    }

    private boolean tryLanguage(Locale language) {
        for (String category : categories) {
            boolean found = false;
            try {
                ResourceBundle.getBundle(basePackage + "." + category, language, callerClassLoader);
                found = true;
            } catch (MissingResourceException ignored) {
            }

            try {
                ResourceBundle.getBundle(category, language, fileClassLoader);
                found = true;
            } catch (MissingResourceException ignored) {
            }

            if (!found) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param locale The Locale to set it to
     *
     * @return True if the language was changed
     *
     * @throws NullPointerException If locale is null
     */
    @Override
    public boolean setLanguage(Locale locale) {
        Objects.requireNonNull(locale);

        if (tryLanguage(locale)) {
            currentLanguage = locale;
            createBundles();
            return true;
        }
        return false;
    }

    @Override
    public Locale getLanguage() {
        return currentLanguage;
    }

    // TODO: Test this
    @Override
    public void reload() {
        createBundles();
    }

    /**
     * A classloader reading from a directory
     */
    private static class FileClassLoader extends ClassLoader {

        private Path   path;

        /**
         * @param path The base path to read from
         */
        FileClassLoader(Path path) {
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException("Path can only be a directory.");
            }
            this.path = path;
        }

        @Override
        public URL getResource(String name) {
            Path resourcePath = path.resolve(name);
            if (!Files.exists(resourcePath)) {
                return null;
            }
            try {
                return resourcePath.toUri().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            if (getResource(name) == null) {
                return null;
            }
            try {
                return Files.newInputStream(path.resolve(name), StandardOpenOption.READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // TODO: This is untested, as I have no jar file and I am too lazy to add an
    // artifact

    /**
     * @param defaultPackage The package they are in
     * @param targetDir The target directory
     * @param overwrite If the existing files should be overwritten.
     * @param file The jar file to copy it out from
     *
     * @return True if the files were written, false otherwise.
     *
     * @throws NullPointerException If defaultPackage, targetDir or jarFile is
     * null
     */
    public static boolean copyDefaultFiles(String defaultPackage, Path targetDir, boolean overwrite, File file) {
        Objects.requireNonNull(defaultPackage);
        Objects.requireNonNull(targetDir);
        Objects.requireNonNull(file);

        String packageName = defaultPackage.replace(".", "/");
        try {
            if (!file.getAbsolutePath().endsWith(".jar")) {
                return false;
            }
            // try for the resource here. Just to close it.
            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().startsWith(packageName)) {
                        Path copyTo = targetDir.resolve(entry.getName().replace(packageName + "/", ""));
                        if (Files.exists(copyTo) && !overwrite) {
                            continue;
                        }
                        Files.copy(jarFile.getInputStream(entry), copyTo, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
