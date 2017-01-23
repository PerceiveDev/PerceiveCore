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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.perceivedev.perceivecore.language.YamlResourceBundle.YamlResourceBundleControl;
import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.MethodPredicate;

import javax.annotation.Nonnull;

/**
 * An implementation of the {@link MessageProvider} using
 */
public class I18N implements MessageProvider {

    private static final Logger LOGGER = Logger.getLogger("I18N");
    private static final String PREFIX = "[I18N] ";

    /**
     * Regex it find things references e.g.
     * "Test 1234 [[path.to.other.message]]" ==> Matches
     * "[[path.to.other.message]]"
     */
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("(?<=\\[\\[)(.+?)(?=]])");

    private ResourceBundle.Control bundleControl;

    private Set<String> categories = new HashSet<>();
    private Map<String, ResourceBundle> fileResourceBundles = new HashMap<>();
    private Map<String, ResourceBundle> jarResourceBundles = new HashMap<>();

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
    @SuppressWarnings("WeakerAccess")
    public I18N(Locale currentLanguage, String basePackage, Path savePath, ClassLoader callerClassLoader,
                ResourceBundle.Control bundleControl, String defaultCategory, String... more) {

        Objects.requireNonNull(currentLanguage, "currentLanguage can not be null");
        Objects.requireNonNull(basePackage, "basePackage can not be null");
        Objects.requireNonNull(savePath, "savePath can not be null");
        Objects.requireNonNull(callerClassLoader, "callerClassLoader can not be null");
        Objects.requireNonNull(defaultCategory, "defaultCategory can not be null");
        Objects.requireNonNull(more, "more can not be null");

        this.currentLanguage = currentLanguage;
        this.basePackage = basePackage;
        this.callerClassLoader = callerClassLoader;

        this.categories.add(defaultCategory);

        this.defaultCategory = defaultCategory;

        fileClassLoader = new FileClassLoader(savePath);

        this.bundleControl = bundleControl;

        createBundles();
    }

    /**
     * @param plugin Your plugin
     * @param basePackage The base package in the jar to read from
     */
    @SuppressWarnings("unused")
    public I18N(JavaPlugin plugin, String basePackage) {
        this(plugin, basePackage, null);
    }

    /**
     * @param plugin Your plugin
     * @param basePackage The base package in the jar to read from
     * @param bundleControl The {@link ResourceBundle.Control} to use. You can use the
     * {@link YamlResourceBundleControl}, if you want YAML language files
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public I18N(JavaPlugin plugin, String basePackage, ResourceBundle.Control bundleControl) {
        this(Locale.ENGLISH,
                basePackage,
                ensureDirExists(plugin.getDataFolder()
                        .toPath()
                        .resolve("language")),
                plugin.getClass().getClassLoader(),
                bundleControl,
                "Messages");
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
                LOGGER.warning(PREFIX + "Not found: " + category);
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
            ResourceBundle jarBundle;
            if (bundleControl == null) {
                jarBundle = ResourceBundle.getBundle(basePackage + "." + category, currentLanguage,
                        callerClassLoader);
            }
            else {
                jarBundle = ResourceBundle.getBundle(basePackage + "." + category, currentLanguage,
                        callerClassLoader, bundleControl);
            }

            jarResourceBundles.put(category, jarBundle);
        } catch (MissingResourceException ignored) {
        }

        try {
            ResourceBundle fileBundle;
            if (bundleControl == null) {
                fileBundle = ResourceBundle.getBundle(category, currentLanguage, fileClassLoader);
            }
            else {
                fileBundle = ResourceBundle.getBundle(category, currentLanguage, fileClassLoader, bundleControl);
            }

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
     *                                  {@link #categories}
     * @see #impl_translateObject(String, String)
     */
    private String translateString(String key, String category) {
        Object object = impl_translateObject(key, category);
        if (object instanceof String) {
            return (String) object;
        }
        return "Key '" + key + "' is no String (" + object.getClass().getSimpleName() + ")";
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
     *                                  {@link #categories}
     */
    private Object impl_translateObject(String key, String category) {
        Optional<Object> translated = translateOrEmpty(key, category);
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
     *                                  {@link #categories}
     */
    private Optional<Object> translateOrEmpty(String key, String category) {
        if (!categories.contains(category)) {
            throw new IllegalArgumentException("Category unknown!");
        }

        if (fileResourceBundles.containsKey(category)) {
            try {
                return Optional.of(fileResourceBundles.get(category).getObject(key));
            } catch (MissingResourceException ignored) {
            }
        }

        if (jarResourceBundles.containsKey(category)) {
            try {
                return Optional.of(jarResourceBundles.get(category).getObject(key));
            } catch (MissingResourceException ignored) {
            }
        }
        return Optional.empty();
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
        }
        else {
            try {
                format = new MessageFormat(pattern, getLanguage());
            } catch (IllegalArgumentException e) {
                String fixedPattern = pattern.replaceAll("\\{\\d.+?}", "[$1]");
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
            String resolved = translateString(found, category);
            result = result.replace("[[" + found + "]]", resolved);
        }
        return result;
    }

    @Override
    @Nonnull
    public String translateCategoryOrDefault(@Nonnull String key, @Nonnull String category,
                                             @Nonnull String defaultString,
                                             @Nonnull Object... formattingObjects) {
        if (!categories.contains(category)) {
            throw new IllegalArgumentException("Unknown category");
        }

        // it is present, let the correct method deal with it
        if (translateOrEmpty(key, category).isPresent()) {
            return translateCategory(key, category, formattingObjects);
        }

        // do it yourself
        return color(format(defaultString, formattingObjects));
    }

    /**
     * @param key The key
     * @param category The category it belongs to
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated, uncolored String
     *
     * @throws IllegalArgumentException If the category is unknown
     * @throws NullPointerException     If any parameter is null
     */
    @Override
    @Nonnull
    public String translateCategoryUncolored(@Nonnull String key, @Nonnull String category,
                                             @Nonnull Object... formattingObjects) {
        Objects.requireNonNull(key, "key can not be null");
        Objects.requireNonNull(category, "category can not be null");
        Objects.requireNonNull(formattingObjects, "formattingObjects can not be null");

        if (!categories.contains(category)) {
            throw new IllegalArgumentException("Unknown category");
        }

        String formatted = format(translateString(key, category), formattingObjects);

        formatted = resolveReferences(formatted, category);

        return formatted;
    }

    /**
     * @param key The key
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated, uncolored String
     *
     * @see #translateCategoryUncolored(String, String, Object...)
     */
    @Override
    @Nonnull
    public String translateUncolored(@Nonnull String key, @Nonnull Object... formattingObjects) {
        return translateCategoryUncolored(key, defaultCategory, formattingObjects);
    }

    /**
     * Retrieves a list and translates it
     *
     * @param translatedObject The translated Object
     * @param key The key of the list
     * @param category The category it belongs to
     * @param formattingObjects The objects to format the message with
     *
     * @return The list, with all entries formatted and colored
     */
    private List<String> translateCollection(Object translatedObject, String key, String category,
                                             Object... formattingObjects) {
        if (!(translatedObject instanceof Collection)) {
            return Collections.singletonList(
                    "Key [" + key + "] is not a Collection, but rather " + translatedObject.getClass().getSimpleName()
            );
        }
        Collection<?> collection = (Collection<?>) translatedObject;
        List<String> result = new ArrayList<>(collection.size());

        for (Object obj : collection) {
            if (!(obj instanceof String)) {
                return Collections.singletonList(
                        "The values of key [" + key + "] are not Strings, but rather "
                                + obj.getClass().getSimpleName()
                );
            }

            String message = format((String) obj, formattingObjects);
            message = resolveReferences(message, category);
            message = color(message);

            result.add(message);
        }
        return result;
    }

    @Nonnull
    @Override
    public Object translateObjectCategory(@Nonnull String key, @Nonnull String category,
                                          @Nonnull Object... formattingObjects) {

        Object translatedObject = impl_translateObject(key, category);

        if (translatedObject instanceof Collection) {
            return translateCollection(translatedObject, key, category, formattingObjects);
        }

        return translatedObject;
    }

    @Override
    public boolean setDefaultCategory(@Nonnull String categoryName) {
        Objects.requireNonNull(categoryName, "categoryName can not be null");
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
    public void addCategory(@Nonnull String category) {
        Objects.requireNonNull(category, "category can not be null");
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
    public boolean setLanguage(@Nonnull Locale locale) {
        Objects.requireNonNull(locale, "locale can not be null");

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

    @Override
    public void reload() {
        createBundles();
    }

    /**
     * A classloader reading from a directory
     */
    private static class FileClassLoader extends ClassLoader {

        private Path path;

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

    /**
     * @param defaultPackage The package they are in
     * @param targetDir The target directory
     * @param overwrite If the existing files should be overwritten.
     * @param file The jar file to copy it out from
     *
     * @return True if the files were written, false otherwise.
     *
     * @throws NullPointerException If defaultPackage, targetDir or jarFile is
     *                              null
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean copyDefaultFiles(String defaultPackage, Path targetDir, boolean overwrite, File file) {
        Objects.requireNonNull(defaultPackage, "defaultPackage can not be null");
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
                        if (Files.isDirectory(copyTo)) {
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

    /**
     * @param input The input to color
     *
     * @return The colored input
     */
    private static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    /**
     * @param plugin Your plugin
     * @param overwrite If the existing files should be overwritten.
     * @param basePackage The base package in the jar to read from
     *
     * @return True if the files were written, false otherwise.
     *
     * @throws NullPointerException If defaultPackage, targetDir or jarFile is
     *                              null
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static boolean copyDefaultFiles(JavaPlugin plugin, boolean overwrite, String basePackage) {
        File pluginJar = (File) ReflectionUtil.invokeMethod(
                JavaPlugin.class,
                new MethodPredicate()
                        .withName("getFile"),
                plugin
        ).getValue();

        Path targetDir = plugin.getDataFolder().toPath().resolve("language");
        if (Files.notExists(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return copyDefaultFiles(basePackage, targetDir, overwrite, pluginJar);
    }
}
