package com.perceivedev.perceivecore.config.util;

import static com.perceivedev.perceivecore.util.text.TextUtils.normalizePathName;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import org.bukkit.plugin.Plugin;

import com.perceivedev.perceivecore.config.ConfigSerializable;
import com.perceivedev.perceivecore.config.SerializationManager;

/**
 * DataManager is an extension of a Map that supports saving and loading
 * of any {@link ConfigSerializable} data class.
 * 
 * @author Rayzr
 * @param <K> The key type
 * @param <V> The value type
 */
public abstract class DataManager<K, V extends ConfigSerializable> {
    private static final String ERROR_MESSAGE = "The path failed the `isValidPath` check for your DataManager instance";

    protected Path path;
    protected final Class<K> keyClass;
    protected final Class<V> dataClass;
    protected Map<K, V> map;

    /**
     * Creates a new {@link DataManager} that saves and loads data class of the
     * type specified, and stores them in the given map.
     * 
     * @param path The path to the data file/folder
     * @param keyClass The class for the key.
     *            {@link SerializationManager#isSerializableToString(Class)}
     *            must return true when given this.
     * @param dataClass The data class that this {@link DataManager} handles
     * @param map The map to store the data in
     */
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
     *            {@link SerializationManager#isSerializableToString(Class)}
     *            must return true when given this.
     * @param dataClass The data class that this {@link DataManager} handles
     * @see #DataManager(Path, Class, Class, Map)
     */
    public DataManager(Path path, Class<K> keyClass, Class<V> dataClass) {
        this(path, keyClass, dataClass, new LinkedHashMap<>());
    }

    /**
     * Creates a new {@link DataManager}
     * 
     * @param plugin The plugin to get the Data folder from
     * @param path The path to the data file/folder
     * @param keyClass The class for the key.
     *            {@link SerializationManager#isSerializableToString(Class)}
     *            must return true when given this.
     * @param dataClass The data class that this {@link DataManager} handles
     * @see #DataManager(Path, Class, Class)
     */
    public DataManager(Plugin plugin, String path, Class<K> keyClass, Class<V> dataClass) {
        this(plugin.getDataFolder().toPath().resolve(normalizePathName(path)), keyClass, dataClass);
    }

    /**
     * @return The size of the DataManager
     * 
     * @see java.util.Map#size()
     */
    public int size() {
        return map.size();
    }

    /**
     * @return Whether the manager is empty
     * 
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * @param key The key
     * @return The value for the given key
     * 
     * @see java.util.Map#get(java.lang.Object)
     */
    public V get(K key) {
        return map.get(key);
    }

    /**
     * @param key The key to add
     * @param value The value to add
     * @return The value previously associated with the key
     * 
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public V put(K key, V value) {
        return map.put(key, value);
    }

    /**
     * @param key The key
     * @return The value previously associated with the key
     * 
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(K key) {
        return map.remove(key);
    }

    /**
     * @param m The map with values to add
     * 
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear() {
        map.clear();
    }

    /**
     * @return All the keys in a set
     * 
     * @see java.util.Map#keySet()
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * @return All the values in a Collection
     * 
     * @see java.util.Map#values()
     */
    public Collection<V> values() {
        return map.values();
    }

    /**
     * @return All the Keys and Values
     * 
     * @see java.util.Map#entrySet()
     */
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    /**
     * @param key The key
     * @param defaultValue The default value
     * @return The value or the default
     * 
     * @see java.util.Map#getOrDefault(java.lang.Object, java.lang.Object)
     */
    public V getOrDefault(K key, V defaultValue) {
        map.putIfAbsent(key, defaultValue);
        return map.getOrDefault(key, defaultValue);
    }

    /**
     * @param action The consumer
     * 
     * @see java.util.Map#forEach(java.util.function.BiConsumer)
     */
    public void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach(action);
    }

    /**
     * @param key The key to add
     * @param value The value to add
     * @return The value previously associated with the key
     * 
     * @see java.util.Map#putIfAbsent(java.lang.Object, java.lang.Object)
     */
    public V putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value);
    }

    /**
     * @param key The key to remove
     * @param value The value to remove
     * @return {@code true} if the value was removed
     * 
     * @see java.util.Map#remove(java.lang.Object, java.lang.Object)
     */
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
    public Class<V> getDataClass() {
        return dataClass;
    }

    /**
     * Checks to make sure that a path is valid for this {@link DataManager} to
     * save and load from. This check is used inside of {@link #setPath(Path)}
     * and the constructor to ensure that the inputted paths point towards
     * files/folders that are valid for the {@link #save()} and {@link #load()}
     * methods to use.
     *
     * @param path The path to check
     * @return True if the path is valid
     */
    public abstract boolean isValidPath(Path path);

    /**
     * Saves all the data in the data map to the config file/folder (implemented
     * by child classes)
     * 
     * @see #getMap()
     */
    public abstract void save();

    /**
     * Loads all the data from the config file/folder into the data map
     * (implemented by child classes)
     * 
     * @see #getMap()
     */
    public abstract void load();
}