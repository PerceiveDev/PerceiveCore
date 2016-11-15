package com.perceivedev.perceivecore.config.util;

import static com.perceivedev.perceivecore.util.TextUtils.normalizePathName;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.config.ConfigSerializable;
import com.perceivedev.perceivecore.config.SerializationManager;

/**
 * A {@link DataManager} that saves data in one file per player in the
 * specified folder
 */
public class DataFileManager<K, V extends ConfigSerializable> extends DataManager<K, V> {

    private final Class<K> keyClass;

    /**
     * Creates a new {@link DataFileManager} that saves and loads data
     * class of the
     * type specified, and stores them in the given map.
     *
     * @param path The path to the data file/folder
     * @param dataClass The data class that this {@link DataManager} handles
     * @param keyClass The class for the key.
     *            {@link SerializationManager#isSerializableToString(Class)}
     *            must return true when given this.
     * @param map The map to store the data in
     */
    public DataFileManager(Path path, Class<V> dataClass, Class<K> keyClass, Map<K, V> map) {
        super(path, dataClass, map);
        this.keyClass = keyClass;

        if (!SerializationManager.isSerializableToString(keyClass)) {
            throw new IllegalArgumentException(String.format("KeyClass '%s' is not serializable to a String. "
                    + "Consider adding a SimpleSerializationProxy for it.", keyClass.getName()));
        }
    }

    /**
     * Creates a new {@link DataFileManager} that uses a
     * {@link HashMap}
     *
     * @param path The path to the data file/folder
     * @param dataClass The data class that this {@link DataManager} handles
     * @param keyClass The class for the key.
     *            {@link SerializationManager#isSerializableToString(Class)}
     *            must return true when given this.
     * @see #DataFileManager(Path, Class, Class, Map)
     */
    public DataFileManager(Path path, Class<V> dataClass, Class<K> keyClass) {
        this(path, dataClass, keyClass, new HashMap<>());
    }

    /**
     * Creates a new {@link DataManager}
     *
     * @param plugin The plugin to get the Data folder from
     * @param path The path to the data file/folder
     * @param dataClass The data class that this {@link DataManager} handles
     * @param keyClass The class for the key.
     *            {@link SerializationManager#isSerializableToString(Class)}
     *            must return true when given this.
     * @see #DataFileManager(Path, Class, Class)
     */
    public DataFileManager(Plugin plugin, String path, Class<V> dataClass, Class<K> keyClass) {
        this(plugin.getDataFolder().toPath().resolve(normalizePathName(path)), dataClass, keyClass);
    }

    @Override
    public boolean isValidPath(Path path) {
        return Files.notExists(path) || Files.isDirectory(path);
    }

    @Override
    public void save() {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            YamlConfiguration configuration = new YamlConfiguration();

            Object serializedKey = SerializationManager.serializeOneLevel(entry.getKey());
            if (!(serializedKey instanceof String)) {
                PerceiveCore.getInstance().getLogger().log(Level.WARNING, "Fascinating."
                        + " A class somehow broke the promise of SerializationManager to serialize to a String!"
                        + " Class: " + entry.getKey().getClass()
                        + " Value: " + entry.getKey());
                continue;
            }
            Map<String, Object> serializedValue = SerializationManager.serialize(entry.getValue());

            configuration.createSection((String) serializedKey, serializedValue);

            try {
                configuration.save(getPath().resolve((String) serializedKey + ".yml").toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void load() {
        try {
            Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), 0, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file.toFile());

                    for (String configKey : configuration.getKeys(false)) {
                        V value = SerializationManager.deserialize(dataClass, configuration.getConfigurationSection(configKey));
                        @SuppressWarnings("unchecked")
                        K key = (K) SerializationManager.deserializeOneLevel(configKey, keyClass);

                        put(key, value);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            PerceiveCore.getInstance().getLogger().log(
                    Level.WARNING,
                    "Failed to read a File in DataFileManager. This is most likely not the fault of this plugin.",
                    e);
        }
    }
}
