package com.perceivedev.perceivecore.config;

import com.perceivedev.perceivecore.config.handlers.LocationSerializer;
import com.perceivedev.perceivecore.config.handlers.VectorSerializer;
import com.perceivedev.perceivecore.config.handlers.WorldSerializer;
import com.perceivedev.perceivecore.util.ArrayUtils;
import com.perceivedev.perceivecore.util.Reflection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * The ConfigManager class allows dynamic saving and loading of data classes to
 * YAML config files. This class can have it's functionality extended by adding
 * new {@link ISerializationHandler} with
 * {@link #registerSerializationHandler(Class, ISerializationHandler)}
 *
 * @author Rayzr
 * @see YamlConfiguration
 */
public class ConfigManager {

    private static Map<Class<?>, ISerializationHandler<?>> serializationHandlers = new HashMap<>();

    /**
     * Registers an {@link ISerializationHandler} for the given class
     *
     * @param clazz   the class to associate this handler with
     * @param handler the handler itself
     *
     * @see VectorSerializer
     * @see WorldSerializer
     * @see LocationSerializer
     */
    public static void registerSerializationHandler(Class<?> clazz, ISerializationHandler<?> handler) {

        if (serializationHandlers.containsKey(clazz)) {
            System.out.println("WARNING: Registering serialization handler for class '" + clazz.getCanonicalName() +
                    "', but a handler was already present.");
        }
        serializationHandlers.put(clazz, handler);

    }

    static {

        registerSerializationHandler(Vector.class, new VectorSerializer());
        registerSerializationHandler(World.class, new WorldSerializer());
        registerSerializationHandler(Location.class, new LocationSerializer());

    }

    @SuppressWarnings("unused")
    private JavaPlugin plugin;
    private File dataFolder;

    /**
     * Create a ConfigManager instance
     *
     * @param plugin the plugin to associate this with
     */
    public ConfigManager(JavaPlugin plugin) {

        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();

        ensureFolderExists(dataFolder);

    }

    /**
     * Makes sure that a file exists. If it does not exist then it is created
     *
     * @param file the file to check
     *
     * @return Whether or not it existed
     */
    private boolean ensureFileExists(File file) {

        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;

    }

    /**
     * Make sure a folder exists. If it does not exist then it is created
     *
     * @param folder the folder to check
     *
     * @return Whether or not it existed
     */
    private boolean ensureFolderExists(File folder) {

        if (!folder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdir();
            return false;
        }

        return true;

    }

    /**
     * If the config file already exists it is not overwritten
     *
     * @param path the path of the file
     *
     * @return A new config file
     */
    @SuppressWarnings("WeakerAccess")   // Is part of the exposed API
    public YamlConfiguration createConfig(String path) {
        File file = new File(dataFolder, path);
        ensureFileExists(file);

        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves the file. Ignores any error silently and prints it out.
     *
     * @param config The {@link YamlConfiguration} to save
     * @param file   The File to save it to
     *
     * @throws NullPointerException if any parameter is null
     */
    public void saveConfig(YamlConfiguration config, File file) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(file);
        try {
            config.save(file);
        } catch (IOException e) {
            System.err.println("Failed to save config file");
        }
    }

    /**
     * Saves the file. Ignores any IO error silently and prints it out
     *
     * @param config The config to save
     * @param file   The path of the file to save it to. Relative to the data folder.
     *
     * @see #saveConfig(YamlConfiguration, File)
     */
    public void saveConfig(YamlConfiguration config, String file) {
        saveConfig(config, resolveFileName(file));

        // DRY violation @Rayzr
/*
        try {
//			createConfig(file); // this line does nothing useful
			config.save(resolveFileName(file));
		} catch (Exception e) {
			System.err.println("Failed to save config file");
		}
*/
    }

    // TODO: @Rayzr: Comment this.
    public <T> T load(Class<T> clazz, String path) {

        if (!resolveFileName(path).exists()) {
            return null;
        }

        YamlConfiguration config = createConfig(path);

        return clazz.cast(load(clazz, config));

        // DRY
/*

		Map<String, Object> data = convertToMap(config);

		return clazz.cast(deserialize(clazz, data));
*/

        // @Rayzr
        // added dynamic cast.
//		return (T) deserialize(clazz, data);
    }

    // TODO: @Rayzr: Comment this.
    public <T> T load(Class<T> clazz, YamlConfiguration config) {

        Map<String, Object> data = convertToMap(config);

        return clazz.cast(deserialize(clazz, data));
    }

    // TODO: @Rayzr: Comment this.
    public <T> T load(Class<T> clazz, ConfigurationSection section) {

        Map<String, Object> data = convertToMap(section);

        return clazz.cast(deserialize(clazz, data));

    }

    public void save(Object o, String path) {

        YamlConfiguration config = createConfig(path);

        save(o, config);

        // this is SO inefficient, remove this
        saveConfig(config, path);

    }

    public void save(Object o, YamlConfiguration config) {

        Map<String, Object> map = serialize(o);

        if (map == null) {
            return;
        }

        saveToConfig(config, map);
    }

    public void save(Object o, YamlConfiguration config, String path) {

        save(o, config);

        saveConfig(config, path);

    }

    public void save(Object o, ConfigurationSection section) {

        Map<String, Object> map = serialize(o);

        if (map == null) {
            return;
        }

        for (Entry<String, Object> entry : map.entrySet()) {

            section.set(entry.getKey(), entry.getValue());

        }

    }

    public static Map<String, Object> serialize(Object o) {

        if (!Reflection.hasInterface(o, ISerializable.class)) {
            System.err.println("Attempted to serialize a class that does not implement Serializable!");
            System.err.println("Invalid class: '" + o.getClass().getCanonicalName() + "'");
            System.err.println("Interfaces: " + ArrayUtils.concat(o.getClass().getInterfaces(), ", "));
            return null;
        }

        List<Field> fields = Reflection.getFieldsWithAnnotation(o.getClass(), Serialized.class);

        Map<String, Object> map = new HashMap<String, Object>();

        for (Field field : fields) {

            try {

                // Save the state of the field
                boolean accessible = field.isAccessible();
                field.setAccessible(true);

                // Check if it's another Serializable
                if (Reflection.hasInterface(field.getType(), ISerializable.class)) {
                    ISerializable serializable = (ISerializable) field.get(o);
                    if (serializable != null) {
                        serializable.onPreSerialize();
                        map.put(field.getName(), serialize(serializable));
                    }
                    else {
                        map.put(field.getName(), null);
                    }
                }
                else if (serializationHandlers.containsKey(field.getType())) {

                    // Get the handler for this type
                    ISerializationHandler<? extends Object> handler = serializationHandlers.get(field.getType());
                    try {
                        map.put(field.getName(), handler._serialize(field.get(o)));
                    } catch (ClassCastException e) {
                        System.err.println("SerializationHandler '" + handler.getClass().getCanonicalName() + "' " +
                                "encountered an invalid type while trying to load data for field '" + field.getName()
                                + "'");
                        e.printStackTrace();
                    } catch (Exception e) {

                    }

                }
                else {

                    // Insert the raw value (won't always work)
                    map.put(field.getName(), field.get(o));

                }

                field.setAccessible(accessible);

            } catch (IllegalArgumentException e) {

                e.printStackTrace();

            } catch (IllegalAccessException e) {

                e.printStackTrace();

            } catch (StackOverflowError e) {

                System.err.println("Data serializer caught in infinite loop while trying to serialize an object of " +
                        "type '" + o.getClass().getCanonicalName() + "'!");
                e.printStackTrace();

            }

        }

        return map;

    }

    /**
     * Attempts to deserialize the data provided in the context of the given
     * class
     *
     * @param clazz the class to deserialize to
     * @param data  the data provided
     *
     * @return The data loaded into an instance of the class, or null if
     * something went wrong
     */
    @SuppressWarnings("unchecked")
    public static Object deserialize(Class<? extends Object> clazz, Map<String, Object> data) {

        if (!ISerializable.class.isAssignableFrom(clazz)) {
            System.err.println("Attempted to deserialize to a non-serializable class!!");
            return null;
        }

        Object o;
        try {
            o = clazz.newInstance();
        } catch (Exception e) {
            System.err.println("Could not instantiate an object of type '" + clazz.getCanonicalName() + "'");
            System.err.println("Classes implementing Serializable should not have a constructor, instead they should" +
                    " " +
                    "use onDeserialize.");
            e.printStackTrace();
            return null;
        }

        List<Field> fields = Reflection.getFieldsWithAnnotation(o.getClass(), Serialized.class);

        for (Field field : fields) {
            if (!data.containsKey(field.getName())) {
                continue;
            }
            try {

                boolean map = (data.get(field.getName()) instanceof Map<?, ?>);

                if (Reflection.hasInterface(field.getType(), ISerializable.class)) {

                    if (!map) {

                        System.err.println("Expected a Map for field '" + field.getName() + "' in '" + clazz
                                .getCanonicalName() + "', however an instance of '" + data.get(field.getName())
                                .getClass().getCanonicalName() + "' was found!");
                        return null;

                    }

                    ISerializable deserialized = (ISerializable) deserialize(field.getType(), (Map<String, Object>)
                            data.get(field.getName()));
                    // If the object could not be deserialized then return null
                    if (deserialized == null) {
                        return null;
                    }
                    deserialized.onDeserialize();
                    Reflection.setValue(field, o, deserialized);

                }
                else if (serializationHandlers.containsKey(field.getType())) {

                    // If this isn't a map then it will error
                    if (!map) {

                        System.err.println("Expected a Map for field '" + field.getName() + "' in '" + clazz
                                .getCanonicalName() + "', however an instance of '" + data.get(field.getName())
                                .getClass().getCanonicalName() + "' was found!");
                        return null;

                    }

                    // Get the handler for this type
                    ISerializationHandler<?> handler = serializationHandlers.get(field.getType());
                    // Attempt to set the value to the deserialized value. This
                    // will error if data.get() does not return a map

                    try {
                        Reflection.setValue(field, o, handler.deserialize((Map<String, Object>) data.get(field.getName
                                ())));
                    } catch (Exception e) {
                        System.err.println("Tried to use serialization handler for type '" + field.getType()
                                .getCanonicalName() + "', but an error occured:");
                        e.printStackTrace();
                    }

                }
                else {

                    // Just set the raw value
                    Reflection.setValue(field, o, data.get(field.getName()));

                }
            } catch (Exception e) {

                // Do various things if specified in an OnFail annotation
                if (field.isAnnotationPresent(OnFail.class)) {

                    OnFail fail = field.getAnnotation(OnFail.class);
                    switch (fail.value()) {

                        case USE_DEFAULT:
                            break;
                        case CANCEL_LOAD:
                            System.err.println("Failed to load field '" + field.getName() + "' in class '" + o
                                    .getClass().getCanonicalName() + "'");
                            System.err.println("OnFail = CANCEL_LOAD, cancelling load");
                            return null;
                        case CONSOLE_ERR:
                            System.err.println("Failed to load field '" + field.getName() + "' in class '" + o
                                    .getClass().getCanonicalName() + "'");
                            break;
                        default:
                            break;

                    }

                }

            }

        }

        return o;

    }

    /**
     * Resolves a file relative to the dataFolder
     *
     * @param path The path to the file
     *
     * @return The Resulting file
     */
    private File resolveFileName(String path) {
        return new File(dataFolder, path);
    }

    @SuppressWarnings("unchecked")
    public static boolean saveToConfig(YamlConfiguration config, Map<String, Object> map) {

        if (config == null || map == null) {
            return false;
        }

        for (Entry<String, Object> entry : map.entrySet()) {

            if (entry.getValue() != null && Map.class.isAssignableFrom(entry.getValue().getClass())) {
                saveToConfig(config.createSection(entry.getKey()), (Map<String, Object>) entry.getValue());
            }
            else {
                config.set(entry.getKey(), entry.getValue());
            }

        }

        return true;

    }

    @SuppressWarnings("unchecked")
    public static void saveToConfig(ConfigurationSection section, Map<String, Object> map) {

        if (section == null || map == null) {
            return;
        }

        for (Entry<String, Object> entry : map.entrySet()) {
            if (Map.class.isAssignableFrom(entry.getValue().getClass())) {
                saveToConfig(section.createSection(entry.getKey()), (Map<String, Object>) entry.getValue());
            }
            else {
                section.set(entry.getKey(), entry.getValue());
            }
        }

    }

    public static Map<String, Object> convertToMap(ConfigurationSection section) {

        Map<String, Object> map = new HashMap<String, Object>();

        for (String key : section.getKeys(false)) {

            if (section.isConfigurationSection(key)) {
                map.put(key, convertToMap(section.getConfigurationSection(key)));
            }
            else {
                map.put(key, section.get(key));
            }

        }

        return map;

    }

}
