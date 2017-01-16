package me.ialistannen.bukkitpluginutilities.config.util;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

import me.ialistannen.bukkitpluginutilities.config.ConfigSerializable;
import me.ialistannen.bukkitpluginutilities.config.SerializationManager;
import me.ialistannen.bukkitpluginutilities.utilities.text.TextUtils;

/**
 * DataManager is an extension of a Map that supports saving and loading
 * of any {@link ConfigSerializable} data class.
 *
 * @param <K> The key type
 * @param <V> The value type
 *
 * @author Rayzr
 */
public abstract class DataManager <K, V extends ConfigSerializable> {
    private static final String ERROR_MESSAGE = "The path failed the `isValidPath` check for your DataManager instance";
    @SuppressWarnings("WeakerAccess")
    protected static final Logger LOGGER = Logger.getLogger("DataManagerLogger");

    private Path path;
    private final Class<K> keyClass;
    private final Class<V> dataClass;
    protected Map<K, V> map;

    /**
     * Creates a new {@link DataManager} that saves and loads data class of the
     * type specified, and stores them in the given map.
     *
     * @param path The path to the data file/folder
     * @param keyClass The class for the key.
     * {@link SerializationManager#isSerializableToString(Class)}
     * must return true when given this.
     * @param dataClass The data class that this {@link DataManager} handles
     * @param map The map to store the data in
     */
    @SuppressWarnings("WeakerAccess")
    public DataManager(Path path, Class<K> keyClass, Class<V> dataClass, Map<K, V> map) {
        if (!isValidPath(path)) {
            throw new InvalidPathException(path.toString(), ERROR_MESSAGE);
        }
        if (!SerializationManager.isSerializableToString(keyClass)) {
            throw new IllegalArgumentException(String.format("keyClass '%s' is not serializable to a String. "
                    + "Consider adding a SimpleSerializationProxy for it.", keyClass.getName()));
        }
        this.path = path;
        this.keyClass = keyClass;
        this.dataClass = dataClass;
        this.map = map;
    }

    /**
     * Creates a new {@link DataManager} that uses a {@link LinkedHashMap}
     *
     * @param path The path to the data file/folder
     * @param keyClass The class for the key.
     * {@link SerializationManager#isSerializableToString(Class)}
     * must return true when given this.
     * @param dataClass The data class that this {@link DataManager} handles
     *
     * @see #DataManager(Path, Class, Class, Map)
     */
    @SuppressWarnings("WeakerAccess")
    public DataManager(Path path, Class<K> keyClass, Class<V> dataClass) {
        this(path, keyClass, dataClass, new LinkedHashMap<>());
    }

    /**
     * Creates a new {@link DataManager}
     *
     * @param plugin The plugin to get the Data folder from
     * @param path The path to the data file/folder
     * @param keyClass The class for the key.
     * {@link SerializationManager#isSerializableToString(Class)}
     * must return true when given this.
     * @param dataClass The data class that this {@link DataManager} handles
     *
     * @see #DataManager(Path, Class, Class)
     */
    @SuppressWarnings("unused")
    public DataManager(Plugin plugin, String path, Class<K> keyClass, Class<V> dataClass) {
        this(plugin.getDataFolder().toPath().resolve(TextUtils.normalizePathName(path)), keyClass, dataClass);
    }

    /**
     * /**
     *
     * @return The size of the DataManager
     *
     * @see Map#size()
     */
    @SuppressWarnings("unused")
    public int size() {
        return map.size();
    }

    /**
     * @return Whether the manager is empty
     *
     * @see Map#isEmpty()
     */
    @SuppressWarnings("unused")
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * @param key The key
     *
     * @return The value for the given key
     *
     * @see Map#get(Object)
     */
    @SuppressWarnings("unused")
    public V get(K key) {
        return map.get(key);
    }

    /**
     * @param key The key to add
     * @param value The value to add
     *
     * @return The value previously associated with the key
     *
     * @see Map#put(Object, Object)
     */
    public V put(K key, V value) {
        return map.put(key, value);
    }

    /**
     * @param key The key
     *
     * @return The value previously associated with the key
     *
     * @see Map#remove(Object)
     */
    @SuppressWarnings("unused")
    public V remove(K key) {
        return map.remove(key);
    }

    /**
     * @param m The map with values to add
     *
     * @see Map#putAll(Map)
     */
    @SuppressWarnings("unused")
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    /**
     * @see Map#clear()
     */
    @SuppressWarnings("WeakerAccess")
    public void clear() {
        map.clear();
    }

    /**
     * @return All the keys in a set
     *
     * @see Map#keySet()
     */
    @SuppressWarnings("unused")
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * @return All the values in a Collection
     *
     * @see Map#values()
     */
    @SuppressWarnings("unused")
    public Collection<V> values() {
        return map.values();
    }

    /**
     * @return All the Keys and Values
     *
     * @see Map#entrySet()
     */
    @SuppressWarnings("unused")
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    /**
     * @param key The key
     * @param defaultValue The default value
     *
     * @return The value or the default
     *
     * @see Map#getOrDefault(Object, Object)
     */
    @SuppressWarnings("unused")
    public V getOrDefault(K key, V defaultValue) {
        map.putIfAbsent(key, defaultValue);
        return map.getOrDefault(key, defaultValue);
    }

    /**
     * @param action The consumer
     *
     * @see Map#forEach(BiConsumer)
     */
    @SuppressWarnings("unused")
    public void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach(action);
    }

    /**
     * @param key The key to add
     * @param value The value to add
     *
     * @return The value previously associated with the key
     *
     * @see Map#putIfAbsent(Object, Object)
     */
    @SuppressWarnings("unused")
    public V putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value);
    }

    /**
     * @param key The key to remove
     * @param value The value to remove
     *
     * @return {@code true} if the value was removed
     *
     * @see Map#remove(Object, Object)
     */
    @SuppressWarnings("unused")
    public boolean remove(K key, V value) {
        return map.remove(key, value);
    }

    /**
     * This method returns the path to the config file/folder. Whether it is a
     * file or a folder is determined by the child implementations of this
     * class.
     *
     * @return The path to the config file/folder
     */
    @SuppressWarnings("WeakerAccess")
    public Path getPath() {
        return path;
    }

    /**
     * Sets a new path to where this {@link DataManager} should save and load.
     * Whether this path should be a file or a folder is entirely up to the
     * child implementation.
     *
     * @param path The new path to set for the config file/folder
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public void setPath(Path path) {
        if (!isValidPath(path)) {
            throw new InvalidPathException(path.toString(), ERROR_MESSAGE);
        }
        this.path = path;
    }

    /**
     * Gets raw data map that is used for storing the information associated
     * with this {@link DataManager} instance
     *
     * @return The raw data map
     */
    public Map<K, V> getMap() {
        return map;
    }

    /**
     * @param map The new map to use
     */
    public void setMap(Map<K, V> map) {
        this.map = map;
    }

    /**
     * @return The data class which is used for saving and loading
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public Class<V> getDataClass() {
        return dataClass;
    }

    /**
     * @return The class of the key
     */
    @SuppressWarnings("WeakerAccess")
    protected Class<K> getKeyClass() {
        return keyClass;
    }

    /**
     * Checks to make sure that a path is valid for this {@link DataManager} to
     * save and load from. This check is used inside of {@link #setPath(Path)}
     * and the constructor to ensure that the inputted paths point towards
     * files/folders that are valid for the {@link #save()} and {@link #load()}
     * methods to use.
     *
     * @param path The path to check
     *
     * @return True if the path is valid
     */
    public abstract boolean isValidPath(Path path);

    /**
     * Saves all the data in the data map to the config file/folder (implemented
     * by child classes)
     *
     * @see #getMap()
     */
    @SuppressWarnings("unused")
    public abstract void save();

    /**
     * Loads all the data from the config file/folder into the data map
     * (implemented by child classes)
     *
     * @see #getMap()
     */
    @SuppressWarnings("unused")
    public abstract void load();
}
