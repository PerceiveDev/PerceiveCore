package com.perceivedev.perceivecore.language;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nonnull;

/**
 * A ResourceBundle loaded from YAML files
 */
public class YamlResourceBundle extends ResourceBundle {

    private static final Yaml YAML = new Yaml();

    private Map<String, Object> map;

    /**
     * @param yamlFile The {@link InputStream} to load the YAML from
     */
    @SuppressWarnings("WeakerAccess")
    public YamlResourceBundle(InputStream yamlFile) {
        this(YAML.load(yamlFile));
    }

    /**
     * @param yamlFile The {@link Reader} to load the YAML from
     */
    @SuppressWarnings("unused")
    public YamlResourceBundle(Reader yamlFile) {
        this(YAML.load(yamlFile));
    }

    /**
     * @param yaml The {@link String} to load the YAML from
     */
    @SuppressWarnings("unused")
    public YamlResourceBundle(String yaml) {
        this(YAML.load(yaml));
    }

    /**
     * @param map The object read by {@link #YAML}.
     *
     * @throws IllegalArgumentException if the object is not a Map
     */
    private YamlResourceBundle(Object map) {
        if (!(map instanceof Map)) {
            throw new IllegalArgumentException(
                    "Object read from YAML is of wrong type! "
                            + "Expected: 'java.util.Map', Got: '" + map.getClass().getName() + "'"
            );
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> tmp = (Map<String, Object>) map;
        this.map = tmp;
    }

    /**
     * Gets an object for the given key from this resource bundle.
     * Returns null if this resource bundle does not contain an
     * object for the given key.
     *
     * @param key the key for the desired object
     *
     * @return the object for the given key, or null
     *
     * @throws NullPointerException if <code>key</code> is <code>null</code>
     */
    @Override
    protected Object handleGetObject(@Nonnull String key) {
        return map.get(key);
    }

    /**
     * Returns an enumeration of the keys.
     *
     * @return an <code>Enumeration</code> of the keys contained in
     * this <code>ResourceBundle</code> and its parent bundles.
     */
    @Override
    @Nonnull
    public Enumeration<String> getKeys() {
        if (parent == null) {
            return new ChainedIteratorEnumeration<>(map.keySet().iterator());
        }

        return new ChainedIteratorEnumeration<>(map.keySet().iterator(), new EnumerationIterator<>(parent.getKeys()));
    }


    private static class EnumerationIterator <T> implements Iterator<T> {
        private Enumeration<T> enumeration;

        /**
         * @param enumeration The enumeration
         */
        EnumerationIterator(Enumeration<T> enumeration) {
            this.enumeration = enumeration;
        }

        @Override
        public boolean hasNext() {
            return enumeration.hasMoreElements();
        }

        @Override
        public T next() {
            return enumeration.nextElement();
        }
    }

    private static class ChainedIteratorEnumeration <T> implements Enumeration<T> {
        private Iterator<T>[] iterators;
        private int index;

        /**
         * @param iterators The iterators
         */
        @SafeVarargs
        ChainedIteratorEnumeration(Iterator<T>... iterators) {
            this.iterators = iterators;
        }

        /**
         * Tests if this enumeration contains more elements.
         *
         * @return <code>true</code> if and only if this enumeration object
         * contains at least one more element to provide;
         * <code>false</code> otherwise.
         */
        @Override
        public boolean hasMoreElements() {
            return iterators[index].hasNext()           // Current has more
                    || index < iterators.length - 1     // We have more iterators in the array
                    && iterators[index + 1].hasNext();  // The next iterator has more elements
        }

        /**
         * Returns the next element of this enumeration if this enumeration
         * object has at least one more element to provide.
         *
         * @return the next element of this enumeration.
         *
         * @throws NoSuchElementException if no more elements exist.
         */
        @Override
        public T nextElement() {
            if (iterators[index].hasNext()) {
                return iterators[index].next();
            }
            // we have more iterators
            if (index < iterators.length - 1) {
                index++;
                return nextElement();
            }
            throw new NoSuchElementException("No more elements in the iterator!");
        }
    }

    /**
     * Tells {@link ResourceBundle#getBundle(String)} to use YAML files instead of {@link Properties} ones
     */
    @SuppressWarnings("unused")
    public static class YamlResourceBundleControl extends Control {

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader classLoader,
                                        boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {

            String bundleName = toBundleName(baseName, locale);

            String resourceName = toResourceName(bundleName, "yml");

            InputStream inputStream = null;
            if (reload) {
                URL url = classLoader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        // Disable caches to get fresh data for
                        // reloading.
                        connection.setUseCaches(false);
                        inputStream = connection.getInputStream();
                    }
                }
            }
            else {
                inputStream = classLoader.getResourceAsStream(resourceName);
            }

            if (inputStream == null) {
                return null;
            }
            YamlResourceBundle yamlResourceBundle = new YamlResourceBundle(inputStream);
            inputStream.close();

            return yamlResourceBundle;
        }
    }
}
