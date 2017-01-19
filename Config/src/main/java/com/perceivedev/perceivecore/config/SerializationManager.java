package com.perceivedev.perceivecore.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

import com.perceivedev.perceivecore.config.handlers.EnumSerializer;
import com.perceivedev.perceivecore.config.handlers.LocationSerializer;
import com.perceivedev.perceivecore.config.handlers.MapSerializer;
import com.perceivedev.perceivecore.config.handlers.UUIDSerializer;
import com.perceivedev.perceivecore.config.handlers.VectorSerializer;
import com.perceivedev.perceivecore.config.handlers.WorldSerializer;
import com.perceivedev.perceivecore.reflection.ReflectionUtil;

import javafx.util.Pair;


/**
 * Manages the serialization
 */
public class SerializationManager {

    private static final Logger LOGGER = Logger.getLogger("SerializationManager");
    private static final int MAX_DEPTH = 20;
    private static final Set<Class<?>> RAW_INSERTABLE_CLASSES = new HashSet<>();

    static {
        RAW_INSERTABLE_CLASSES.add(String.class);

        RAW_INSERTABLE_CLASSES.add(Number.class);
        RAW_INSERTABLE_CLASSES.add(Long.class);
        RAW_INSERTABLE_CLASSES.add(Integer.class);
        RAW_INSERTABLE_CLASSES.add(Double.class);
        RAW_INSERTABLE_CLASSES.add(Float.class);
        RAW_INSERTABLE_CLASSES.add(Short.class);
        RAW_INSERTABLE_CLASSES.add(Byte.class);
        RAW_INSERTABLE_CLASSES.add(Long.TYPE);
        RAW_INSERTABLE_CLASSES.add(Integer.TYPE);
        RAW_INSERTABLE_CLASSES.add(Double.TYPE);
        RAW_INSERTABLE_CLASSES.add(Float.TYPE);
        RAW_INSERTABLE_CLASSES.add(Short.TYPE);
        RAW_INSERTABLE_CLASSES.add(Byte.TYPE);
    }

    private static Map<Class<?>, SerializationProxy<?>> serializationProxyMap = new HashMap<>();

    /**
     * Adds a proxy for a class
     *
     * @param clazz The clazz to proxy
     * @param proxy The proxy
     * @param <T> The Type of the clazz to proxy
     */
    @SuppressWarnings("WeakerAccess")
    public static <T> void addSerializationProxy(Class<T> clazz, SerializationProxy<T> proxy) {
        serializationProxyMap.put(clazz, proxy);
    }

    static {
        addSerializationProxy(Vector.class, new VectorSerializer());
        addSerializationProxy(Location.class, new LocationSerializer());
        addSerializationProxy(World.class, new WorldSerializer());
        addSerializationProxy(UUID.class, new UUIDSerializer());
        addSerializationProxy(Map.class, new MapSerializer());
        addSerializationProxy(Enum.class, new EnumSerializer());
    }

    /**
     * Removes a proxy for a class
     *
     * @param clazz The clazz to remove the proxy for
     */
    @SuppressWarnings("unused")
    public static void removeSerializationProxy(Class<?> clazz) {
        serializationProxyMap.remove(clazz);
    }

    /**
     * Returns the serialization proxy for a class
     *
     * @param clazz The clazz to get the SerializationProxy for
     *
     * @return The Serialization proxy for the given class
     */
    private static SerializationProxy<?> getSerializationProxy(Class<?> clazz) {
        if (serializationProxyMap.containsKey(clazz)) {
            return serializationProxyMap.get(clazz);
        }

        if (clazz.isPrimitive()) {
            return null;
        }

        Class<?> superClass = clazz;
        while ((superClass = superClass.getSuperclass()) != Class.class && superClass != Object.class && superClass
                != null) {
            if (getProxyForClassExact(superClass) != null) {
                return getProxyForClassExact(superClass);
            }
            for (Class<?> implementedInterface : superClass.getInterfaces()) {
                if (getProxyForClassExact(implementedInterface) != null) {
                    return getProxyForClassExact(implementedInterface);
                }
            }
        }

        return null;
    }

    private static SerializationProxy<?> getProxyForClassExact(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        // only use superclass proxies if they were registered for interfaces or
        // abstract classes
        if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
            return null;
        }
        if (serializationProxyMap.containsKey(clazz)) {
            return serializationProxyMap.get(clazz);
        }
        return null;
    }

    /**
     * Checks if a class is serializable
     *
     * @param clazz The class to check
     *
     * @return True if the class is serializable
     */
    public static boolean isSerializable(Class<?> clazz) {
        return ConfigurationSerializable.class.isAssignableFrom(clazz) || ConfigSerializable.class.isAssignableFrom
                (clazz) || RAW_INSERTABLE_CLASSES
                .contains(clazz)
                || getSerializationProxy(clazz) != null;

    }

    /**
     * Checks if a class is serializable to a String
     * <p>
     * This is true if and only if the class is insertable as a RAW value (e.g.
     * int, float, String, byte)
     * OR a {@link SimpleSerializationProxy} is in place
     *
     * @param clazz The class to check
     *
     * @return True if the class is serializable to a String
     */
    public static boolean isSerializableToString(Class<?> clazz) {
        return RAW_INSERTABLE_CLASSES.contains(clazz) || getSerializationProxy(clazz) instanceof
                SimpleSerializationProxy;
    }

    /**
     * Serializes a class
     *
     * @param configSerializable The {@link ConfigSerializable} to serialize
     *
     * @return The Serialized form
     *
     * @throws IllegalArgumentException if a field couldn't be serialized
     * @throws IllegalStateException    if a too deep loop is detected
     */
    public static Map<String, Object> serialize(ConfigSerializable configSerializable) {
        return serialize(configSerializable, 0);
    }

    /**
     * Serializes a class
     *
     * @param object The {@link Object} to serialize
     * @param depth The recursion depth
     *
     * @return The Serialized form
     *
     * @throws IllegalArgumentException if a field couldn't be serialized
     * @throws IllegalArgumentException if the object can't be serialized
     * @throws IllegalStateException    if a too deep loop is detected
     */
    public static Map<String, Object> serialize(Object object, int depth) {
        if (object == null) {
            return Collections.emptyMap();
        }

        if (!isSerializable(object.getClass())) {
            throw new IllegalArgumentException("Can't serialize class: " + object.getClass().getName());
        }

        if (depth > MAX_DEPTH) {
            throw new IllegalStateException("Trapped in a loop? Recursion amount too high.");
        }

        Map<String, Object> map = new HashMap<>();

        for (Field field : getFieldsToSerialize(object.getClass())) {
            Class<?> type = field.getType();

            if (getField(field, object) == null) {
                map.put(field.getName(), null);
                continue;
            }

            try {
                // yes, that will actually throw off the depth calc. Should
                // still prevent Stack overflows.
                Object value = getField(field, object);
                map.put(field.getName(), serializeOneLevel(value, depth + 1));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The field '" + field.getName() + "' of type '" + type.getName() +
                        "' is not serializable", e);
            }
        }

        return map;
    }

    /**
     * Serializes ONLY THIS object
     *
     * @param object The Object to serialize
     *
     * @return The serialized object
     *
     * @throws NullPointerException if object is null
     */
    public static Object serializeOneLevel(Object object) {
        return serializeOneLevel(object, 0);
    }

    /**
     * Serializes ONLY THIS object
     *
     * @param object The Object to serialize
     * @param depth The maximum depth
     *
     * @return The serialized object
     *
     * @throws NullPointerException if object is null
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object serializeOneLevel(Object object, int depth) {
        Objects.requireNonNull(object, "object can not be null");

        Class<?> type = object.getClass();
        if (ConfigSerializable.class.isAssignableFrom(type)) {
            return serialize(object, depth + 1);
        }
        else if (getSerializationProxy(type) != null) {
            SerializationProxy proxy = getSerializationProxy(type);
            Object data;
            if (proxy instanceof SimpleSerializationProxy) {
                data = ((SimpleSerializationProxy) proxy).serializeSimple(object);
            }
            else {
                assert proxy != null;   // will be true, checked in startTicker
                // if block
                data = proxy.serialize(object);
            }
            return data;
        }
        else if (object instanceof ConfigurationSerializable) {
            ConfigurationSerializable configurationSerializable = (ConfigurationSerializable) object;
            return configurationSerializable.serialize();
        }
        else if (RAW_INSERTABLE_CLASSES.contains(type)) {
            return object;
        }
        else if (object instanceof List) {
            List<?> list = (List<?>) object;
            if (list.isEmpty()) {
                return Collections.emptyList();
            }

            List<Pair<String, Object>> pairs = new ArrayList<>();

            for (Object element : list) {
                if (element == null) {
                    pairs.add(new Pair<>(null, null));
                    continue;
                }
                pairs.add(new Pair<>(element.getClass().getName(), element));
            }
            return pairs;
        }
        else {
            throw new IllegalArgumentException(type.getName() + " is not serializable.");
        }
    }

    /**
     * Deserializes an object.
     *
     * @param clazz The clazz to deserialize
     * @param data The serialized data (ConfigurationSection or
     * YamlConfiguration)
     * @param <T> The type of the class to deserialize
     *
     * @return The deserialized class
     */
    public static <T> T deserialize(Class<T> clazz, ConfigurationSection data) {
        Objects.requireNonNull(data, "data cannot be null!");
        Objects.requireNonNull(clazz, "clazz cannot be null!");

        return deserialize(clazz, convertToMap(data), 0);
    }

    /**
     * Deserializes an object.
     *
     * @param clazz The clazz to deserialize
     * @param data The serialized data
     * @param <T> The type of the class to deserialize
     *
     * @return The deserialized class
     */
    public static <T> T deserialize(Class<T> clazz, Map<String, Object> data) {
        Objects.requireNonNull(data, "data cannot be null!");
        Objects.requireNonNull(clazz, "clazz cannot be null!");

        return deserialize(clazz, data, 0);
    }

    /**
     * Deserializes an object.
     *
     * @param clazz The clazz to deserialize
     * @param data The serialized data
     * @param depth The recursion depth
     * @param <T> The type of the class to deserialize
     *
     * @return The deserialized class or null if any other error occurred
     *
     * @throws IllegalStateException    if a too deep loop is detected
     * @throws IllegalArgumentException if it doesn't know how to deal with a
     *                                  field
     */
    private static <T> T deserialize(Class<T> clazz, Map<String, Object> data, int depth) {
        if (depth > MAX_DEPTH) {
            throw new IllegalStateException("Trapped in a loop? Recursion amount too high.");
        }

        if (!hasDefaultConstructor(clazz)) {
            throw new IllegalArgumentException("The class " + clazz.getCanonicalName() + " does not have a default " +
                    "constructor!");
        }
        T instance = instantiate(clazz);
        if (instance == null) {
            return null;
        }

        for (Field field : getFieldsToSerialize(clazz)) {
            Class<?> type = field.getType();

            if (data.containsKey(field.getName())) {
                Object serializedData = data.get(field.getName());

                // don't let the deserializers deal with nulls.
                // Do it yourself.
                if (serializedData == null) {
                    setField(field, instance, null);
                    continue;
                }

                try {
                    setField(field, instance, deserializeOneLevel(serializedData, type, depth + 1));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("No deserialize method found for field '" + field.getName() +
                            "' of type '" + type
                            .getName() + "'", e);
                }
            }
        }

        return instance;
    }

    /**
     * Returns the deserialized object. Deserializes ONE ONE LEVEL. <b>DO NOT
     * USE THIS METHOD if you don't know what that means</b>
     *
     * @param object The Object to deserialize
     * @param type The class of the object to deserialize
     *
     * @return The deserialized object
     */
    public static Object deserializeOneLevel(Object object, Class<?> type) {
        return deserializeOneLevel(object, type, 0);
    }

    /**
     * Returns the deserialized object. Deserializes ONE ONE LEVEL. <b>DO NOT
     * USE THIS METHOD if you don't know what that means</b>
     *
     * @param object The Object to deserialize
     * @param type The class of the object to deserialize
     *
     * @return The deserialized object
     */
    private static Object deserializeOneLevel(Object object, Class<?> type, int depth) {
        if (depth > MAX_DEPTH) {
            throw new IllegalStateException("Trapped in a loop? Recursion amount too high.");
        }

        if (getSerializationProxy(type) != null) {
            SerializationProxy<?> proxy = getSerializationProxy(type);
            if (object instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) object;

                // damn IntelliJ. I checked that, I swear!
                assert proxy != null;

                return proxy.deserialize(map);
            }
            else {
                if (proxy instanceof SimpleSerializationProxy) {
                    return ((SimpleSerializationProxy<?>) proxy).deserializeSimple(object);
                }
                else {
                    throw new IllegalArgumentException("Deserialization found no map for proxy: " + type.getName());
                }
            }
        }
        else if (ConfigSerializable.class.isAssignableFrom(type)) {
            if (!(object instanceof Map)) {
                throw new IllegalArgumentException("Deserialization found no map for ConfigSerializable: " + type
                        .getName());
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) object;
            return deserialize(type, map, depth + 1);
        }
        else if (ConfigurationSerializable.class.isAssignableFrom(type)) {
            if (object instanceof Map) {
                // System.out.println("Found a map. This shouldn't happen, as it
                // means the Bukkit configuration hasn't done it's job.");

                Object fieldValue = null;
                if (getMethod("deserialize", type, Map.class) != null) {
                    fieldValue = invoke(getMethod("deserialize", type, Map.class), null, object);
                }
                else if (getMethod("valueOf", type, Map.class) != null) {
                    fieldValue = invoke(getMethod("valueOf", type, Map.class), null, object);
                }

                if (fieldValue != null) {
                    return fieldValue;
                }

                if (isConstructorPresent(type, Map.class)) {
                    return instantiate(type, new Class[]{Map.class}, object);
                }
                else {
                    LOGGER.warning("No deserialization method found for ConfigurationSerializable " + type.getName());
                }
            }
            else {
                return object;
            }
        }
        else if (ReflectionUtil.inheritsFrom(type, List.class)) {
            List<?> serializedList = (List<?>) object;

            if (serializedList.isEmpty()) {
                return new ArrayList<>();
            }

            List<Object> newList = new ArrayList<>();

            for (Object element : serializedList) {
                if (element == null) {
                    newList.add(null);
                }
                Pair<?, ?> pair = (Pair<?, ?>) deserializeOneLevel(element, Pair.class, depth + 1);
                newList.add(pair == null ? null : pair.getValue());
            }

            return newList;
        }
        else if (RAW_INSERTABLE_CLASSES.contains(type)) {
            // convert numbers. YML doesn't know some types. It always returns
            // doubles for example, no floats
            if (ReflectionUtil.inheritsFrom(object.getClass(), Number.class)) {
                Number number = (Number) object;
                if (type == Float.class || type == Float.TYPE) {
                    return number.floatValue();
                }
                else if (type == Double.class || type == Double.TYPE) {
                    return number.doubleValue();
                }
                else if (type == Byte.class || type == Byte.TYPE) {
                    return number.byteValue();
                }
                else if (type == Short.class || type == Short.TYPE) {
                    return number.shortValue();
                }
                else if (type == Integer.class || type == Integer.TYPE) {
                    return number.intValue();
                }
                else if (type == Long.class || type == Long.TYPE) {
                    return number.longValue();
                }
            }
            return object;
        }
        throw new IllegalArgumentException("No deserialize method found for type '" + type.getName() + "'");
    }

    /**
     * @param section the ConfigurationSection to convert
     */
    private static Map<String, Object> convertToMap(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        for (Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (section.isConfigurationSection(entry.getKey())) {
                data.put(entry.getKey(), convertToMap(section.getConfigurationSection(entry.getKey())));
            }
            else {
                data.put(entry.getKey(), entry.getValue());
            }
        }
        return data;
    }

    /**
     * Returns the value of a field
     *
     * @param f The Field to get the value from
     * @param handle The handle to get it from
     * @param <T> The Type to cast it to
     *
     * @return The value or null if an error occurred
     */
    @SuppressWarnings("unchecked")
    private static <T> T getField(Field f, Object handle) {
        try {
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            // noinspection unchecked
            return (T) f.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the value of a field
     *
     * @param field The field to set the value for
     * @param handle The handle to set it for
     * @param value The value to set it to
     */
    private static void setField(Field field, Object handle, Object value) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(handle, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Instantiates an object
     *
     * @param constructor The constructor to instantiate
     * @param params The parameters to pass
     * @param <T> The Type to cast it to
     *
     * @return The instantiated object or null if an error occurred
     */
    @SuppressWarnings("unchecked")
    private static <T> T instantiate(Constructor<?> constructor, Object... params) {
        try {
            constructor.setAccessible(true);
            return (T) constructor.newInstance(params);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Instantiates a class using the base constructor (no params)
     *
     * @param clazz The class to instantiate
     * @param <T> The Type to cast it to
     *
     * @return The instantiated object or null if an error occurred
     */
    private static <T> T instantiate(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            return instantiate(constructor);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if the class has a default constructor
     *
     * @param clazz The class to check
     *
     * @return True if the class has a default constructor
     */
    private static boolean hasDefaultConstructor(Class<?> clazz) {
        try {
            clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    /**
     * Instantiates an object
     *
     * @param clazz The class to instantiate
     * @param classes The parameter classes
     * @param params The parameters
     * @param <T> The Type to cast it to
     *
     * @return The instantiated object or null if an error occurred
     */
    private static <T> T instantiate(Class<?> clazz, Class<?>[] classes, Object... params) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(classes);
            return instantiate(constructor, params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Invokes a method
     *
     * @param method The method to invoke
     * @param handle The handle to invoke it on
     * @param params The parameters of it
     *
     * @return The resulting Object
     */
    private static Object invoke(Method method, @SuppressWarnings("SameParameterValue") Object handle, Object...
            params) {
        method.setAccessible(true);
        try {
            return method.invoke(handle, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a method
     *
     * @param name The name of the method
     * @param clazz The clazz of the method
     * @param params The parameters of the method
     *
     * @return The method or null if none found.
     */
    private static Method getMethod(String name, Class<?> clazz, Class<?>... params) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(name) && Arrays.equals(params, method.getParameterTypes())) {
                return method;
            }
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(name) && Arrays.equals(params, method.getParameterTypes())) {
                return method;
            }
        }
        return null;
    }

    /**
     * Checks if a constructor is present
     *
     * @param clazz The class to check
     * @param params The Parameters of the Constructor
     *
     * @return True if there is a constructor for it
     */
    private static boolean isConstructorPresent(Class<?> clazz, Class<?>... params) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (Arrays.equals(constructor.getParameterTypes(), params)) {
                return true;
            }
        }
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (Arrays.equals(constructor.getParameterTypes(), params)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all the fields that need to be serialized
     *
     * @param clazz The class to get all the fields from
     *
     * @return All the fields that need to be serialized
     */
    private static List<Field> getFieldsToSerialize(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .collect(Collectors.toList());
    }
}
