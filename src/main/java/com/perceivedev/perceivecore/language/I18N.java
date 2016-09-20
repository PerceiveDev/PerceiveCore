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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * An implementation of the {@link MessageProvider} using
 */
public class I18N implements MessageProvider {

    /**
     * Regex it find things references
     * e.g.
     * "Test 1234 [[path.to.other.message]]" ==> Matches "[[path.to.other.message]]"
     */
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("(?<=\\[\\[)(.+?)(?=\\]\\])");


    private Map<String, Category> categories = new HashMap<>();
    private Map<Category, ResourceBundle> fileResourceBundles = new HashMap<>();
    private Map<Category, ResourceBundle> jarResourceBundles = new HashMap<>();

    private Locale currentLanguage;
    private String basePackage;

    private ClassLoader callerClassLoader, fileClassLoader;

    private Category defaultCategory;

    /**
     * Cache to increase performance. May be left out.
     */
    private Map<String, MessageFormat> messageFormatCache = new HashMap<>();


    /**
     * @param currentLanguage   The current language
     * @param basePackage       The base package in the jar to read from
     * @param savePath          The save path
     * @param callerClassLoader Your class loader. Needed to query packages from your jar file
     * @param defaultCategory   The default category
     * @param more              More categories
     *
     * @throws NullPointerException If any parameter is null
     */
    public I18N(Locale currentLanguage, String basePackage, Path savePath, ClassLoader callerClassLoader,
                Category defaultCategory, Category... more) {

        Objects.requireNonNull(currentLanguage);
        Objects.requireNonNull(basePackage);
        Objects.requireNonNull(savePath);
        Objects.requireNonNull(callerClassLoader);
        Objects.requireNonNull(defaultCategory);
        Objects.requireNonNull(more);

        this.currentLanguage = currentLanguage;
        this.basePackage = basePackage;
        this.callerClassLoader = callerClassLoader;

        this.categories.put(defaultCategory.getName(), defaultCategory);
        Arrays.stream(more)
                .forEach(category -> categories.put(category.getName(), category));

        this.defaultCategory = defaultCategory;

        fileClassLoader = new FileClassLoader(savePath, basePackage);

        createBundles();
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

        categories.values().forEach(category -> {
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
    private boolean createBundle(Category category) {
        try {
            ResourceBundle jarBundle =
                    ResourceBundle.getBundle(basePackage + "." + category.getName(),
                            currentLanguage,
                            callerClassLoader);

            jarResourceBundles.put(category, jarBundle);
        } catch (MissingResourceException ignored) {
        }

        try {
            ResourceBundle fileBundle = ResourceBundle
                    .getBundle(category.getName(), currentLanguage, fileClassLoader);

            fileResourceBundles.put(category, fileBundle);
        } catch (MissingResourceException ignored) {
        }

        return jarResourceBundles.containsKey(category) || fileResourceBundles.containsKey(category);
    }

    /**
     * Translates a String
     *
     * @param key      The key
     * @param category The category
     *
     * @return The translated String
     *
     * @throws IllegalArgumentException If the category isn't in {@link #categories}
     */
    private String translate(String key, Category category) {
        if (!categories.containsKey(category.getName())) {
            throw new IllegalArgumentException("Category unknown!");
        }

        if (fileResourceBundles.containsKey(category)) {
            try {
                return fileResourceBundles.get(category).getString(key);
            } catch (MissingResourceException ignored) {
            }
        }

        try {
            return jarResourceBundles.get(category).getString(key);
        } catch (MissingResourceException e) {
            return "No translation for [" + key + "]";
        }
    }

    /**
     * Formats a (translated) string
     *
     * @param pattern           The pattern
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

    /**
     * @param key               The key
     * @param category          The category it belongs to
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated, uncolored String
     *
     * @throws IllegalArgumentException If the category is unknown
     * @throws NullPointerException     If any parameter is null
     */
    @Override
    public String trUncolored(String key, String category, Object... formattingObjects) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(category);
        Objects.requireNonNull(formattingObjects);

        if (!categories.containsKey(category)) {
            throw new IllegalArgumentException("Unknown category");
        }

        String formatted = format(
                translate(key, categories.get(category)),
                formattingObjects
        );

        formatted = resolveReferences(formatted, category);

        return formatted;
    }

    /**
     * @param key               The key
     * @param formattingObjects The objects to format the message with
     *
     * @return The translated, uncolored String
     *
     * @see #trUncolored(String, String, Object...)
     */
    @Override
    public String trUncolored(String key, Object... formattingObjects) {
        return trUncolored(key, defaultCategory.getName(), formattingObjects);
    }

    @Override
    public boolean setDefaultCategory(String categoryName) {
        Objects.requireNonNull(categoryName);
        if (!categories.containsKey(categoryName)) {
            return false;
        }

        defaultCategory = categories.get(categoryName);
        return true;
    }

    @Override
    public void addCategory(Category category) {
        Objects.requireNonNull(category);
        if (categories.containsKey(category.getName())) {
            return;
        }
        categories.put(category.getName(), category);
        createBundle(category);
    }

    private boolean tryLanguage(Locale language) {
        for (Category category : categories.values()) {
            boolean found = false;
            try {
                ResourceBundle.getBundle(basePackage + "." + category.getName(), language, callerClassLoader);
                found = true;
            } catch (MissingResourceException ignored) {
            }

            try {
                ResourceBundle.getBundle(category.getName(), language, fileClassLoader);
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

        private Path path;
        private String defaultPackage;

        /**
         * @param path           The base path to read from
         * @param defaultPackage The default package. Used for correctly mapping the two file structures. The path in
         *                       the jar and outside.
         */
        FileClassLoader(Path path, String defaultPackage) {
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException("Path can only be a directory.");
            }
            this.path = path;
            this.defaultPackage = defaultPackage.replace(".", "/");
        }

        @Override
        public URL getResource(String name) {
            Path resourcePath = path.resolve(name.replace(defaultPackage + "/", ""));
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
                return Files.newInputStream(path.resolve(name.replace(defaultPackage + "/", "")),
                        StandardOpenOption.READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    // TODO: This is untested, as I have no jar file and I am too lazy to add an artifact

    /**
     * @param defaultPackage The package they are in
     * @param targetDir      The target directory
     * @param overwrite      If the existing files should be overwritten.
     * @param file           The jar file to copy it out from
     *
     * @return True if the files were written, false otherwise.
     *
     * @throws NullPointerException If defaultPackage, targetDir or jarFile is null
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
