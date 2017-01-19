package com.perceivedev.perceivecore.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.bukkit.Bukkit;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse.ResultType;

/**
 * Provides utility methods for reflection
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ReflectionUtil {

    private static final String SERVER_VERSION;

    // <editor-fold desc="INIT">
    // ==== INIT SERVER VERSION ====

    static {
        String name = Bukkit.getServer() == null
                      ? "org.bukkit.craftbukkit.v1_10_R1"
                      : Bukkit.getServer().getClass().getPackage().getName();
        String[] split = name.split("\\.");
        name = split[split.length - 1];

        SERVER_VERSION = name;
    }
    // </editor-fold>

    // <editor-fold desc="Version Validation Methods">
    // ==== VERSION VALIDATION METHODS ===

    /**
     * Returns the major version of the server
     *
     * @return The major version of the server
     */
    public static int getMajorVersion() {
        String name = Bukkit.getVersion();

        name = name.substring(name.indexOf("MC: ") + "MC: ".length());
        name = name.replace(")", "");

        return Integer.parseInt(name.split("\\.")[0]);
    }

    /**
     * Returns the minor version of the server
     *
     * @return The minor version of the server
     */
    public static int getMinorVersion() {
        String name = Bukkit.getVersion();

        name = name.substring(name.indexOf("MC: ") + "MC: ".length());
        name = name.replace(")", "");

        return Integer.parseInt(name.split("\\.")[1]);
    }

    /**
     * Returns the patch version of the server
     *
     * @return The patch version of the server
     */
    public static int getPatchVersion() {
        String name = Bukkit.getVersion();

        name = name.substring(name.indexOf("MC: ") + "MC: ".length());
        name = name.replace(")", "");

        String[] splitted = name.split("\\.");
        if (splitted.length < 3) {
            return 0;
        }
        return Integer.parseInt(splitted[2]);
    }
    // </editor-fold>

    // <editor-fold desc="Class Search Functions">
    // ==== CLASS SEARCH FUNCTIONS ====

    /**
     * Returns the class with the given name in the given package
     *
     * @param nameSpace The {@link NameSpace} of the class
     * @param qualifiedName The qualified name of the class inside the
     * {@link NameSpace}
     *
     * @return The Class, if found
     *
     * @throws NullPointerException if any parameter is null
     */
    public static Optional<Class<?>> getClass(NameSpace nameSpace, String qualifiedName) {
        Objects.requireNonNull(nameSpace, "nameSpace can not be null");
        Objects.requireNonNull(qualifiedName, "qualifiedName can not be null");

        String fullyQualifiedName = nameSpace.resolve(qualifiedName);
        return classForName(fullyQualifiedName);
    }

    /**
     * Returns the class with the given name in the given package
     *
     * @param nameWithIdentifier The qualified name of the class inside the
     * {@link NameSpace}, prefixed with the {@link NameSpace}
     * identifier.
     *
     * @return The Class, if found
     *
     * @throws NullPointerException if nameWithIdentifier is null
     * @see #getClass(NameSpace, String) #getClass(NameSpace, String) with the
     * resolved NameSpace and name
     */
    public static Optional<Class<?>> getClass(String nameWithIdentifier) {
        Objects.requireNonNull(nameWithIdentifier, "nameWithIdentifier can not be null");

        Optional<NameSpace> fromIdentifier = NameSpace.getFromIdentifier(nameWithIdentifier);
        if (!fromIdentifier.isPresent()) {
            throw new IllegalArgumentException("Identifier unknown '" + nameWithIdentifier + "'");
        }
        return getClass(fromIdentifier.get(), nameWithIdentifier);
    }

    /**
     * Alias of {@link #getClass(String)}
     *
     * @param className the class name
     *
     * @return the optional class
     *
     * @throws NullPointerException if <code>className</code> is null
     */
    public static Optional<Class<?>> $(String className) {
        Objects.requireNonNull(className, "className can not be null");

        return getClass(className);
    }

    /**
     * Returns the class for the name using the {@link Class#forName(String)}
     * method
     *
     * @param fullyQualifiedName The fully qualified name of a class
     *
     * @return The class or an empty optional if there is none
     */
    private static Optional<Class<?>> classForName(String fullyQualifiedName) {
        try {
            return Optional.ofNullable(Class.forName(fullyQualifiedName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    // </editor-fold>

    // ==== CLASS ====

    /**
     * Checks if a given class <i>somehow</i> inherits from another class
     *
     * @param toCheck The class to check
     * @param inheritedClass The inherited class, it should have
     *
     * @return True if {@code toCheck} somehow inherits from
     * {@code inheritedClass}
     */
    public static boolean inheritsFrom(Class<?> toCheck, Class<?> inheritedClass) {
        if (inheritedClass.isAssignableFrom(toCheck)) {
            return true;
        }

        for (Class<?> implementedInterface : toCheck.getInterfaces()) {
            if (inheritsFrom(implementedInterface, inheritedClass)) {
                return true;
            }
        }

        return false;
    }

    // <editor-fold desc="Fields">
    // ==== FIELDS ====

    /**
     * Returns the first field matching the selector
     *
     * @param clazz The Class to get the fields for
     * @param selector The Selector function to use
     *
     * @return The first field matching the selector
     *
     * @throws NullPointerException if any parameter is null
     */
    public static ReflectResponse<Field> getField(Class<?> clazz, Predicate<Field> selector) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(selector, "selector can not be null");

        Optional<Field> first = getFields(clazz).filter(selector).findFirst();

        if (!first.isPresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }
        return new ReflectResponse<>(first.get());
    }

    /**
     * Returns ALL fields (public {@code ->} private) of a class, filtered by
     * the given
     * Predicate.
     *
     * @param clazz The Class to get the fields for
     * @param predicate The Predicate to use to filter the fields
     *
     * @return The fields of the class. No {@link ReflectResponse}, as the only
     * error that can occur is a SecurityException.
     *
     * @throws NullPointerException if any parameter is null
     */
    public static Stream<Field> getFields(Class<?> clazz, Predicate<Field> predicate) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(predicate, "predicate can not be null");

        return getFields(clazz).filter(predicate);
    }

    /**
     * Returns ALL fields (public {@code ->} private) of a class
     *
     * @param clazz The Class to get the fields for
     *
     * @return The fields of the class
     */
    private static Stream<Field> getFields(Class<?> clazz) {
        return Stream.concat(
                Arrays.stream(clazz.getDeclaredFields()), Arrays.stream(clazz.getFields())).distinct();
    }

    /**
     * Returns the value of a field
     *
     * @param clazz The clazz get the Field from
     * @param handle The handle to get it for
     * @param selector The selector to match the field
     *
     * @return The value of the field.
     *
     * @throws NullPointerException if clazz or selector is null
     * @see #getFieldValue(Field, Object)
     */
    public static ReflectResponse<Object> getFieldValue(Class<?> clazz, Object handle, Predicate<Field> selector) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(selector, "selector can not be null");

        ReflectResponse<Field> field = getField(clazz, selector);
        if (!field.isValuePresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }

        return getFieldValue(field.getValue(), handle);
    }

    /**
     * Returns the value of a field
     *
     * @param name The name of the field
     * @param clazz The clazz get the Field from
     * @param handle The handle to get it for
     *
     * @return The value of the field.
     *
     * @throws NullPointerException if clazz or selector is null
     * @see #getFieldValue(Class, Object, Predicate)
     */
    public static ReflectResponse<Object> getFieldValue(String name, Class<?> clazz, Object handle) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(name, "name can not be null");

        return getFieldValue(clazz, handle, new MemberPredicate<Field>().withName(name));
    }

    /**
     * Returns the value of a field
     *
     * @param field The field to get
     * @param handle The handle to get it for
     *
     * @return The value of the field.
     *
     * @throws NullPointerException if field is null
     * @throws NullPointerException If field is null
     */
    public static ReflectResponse<Object> getFieldValue(Field field, Object handle) {
        Objects.requireNonNull(field, "field can not be null");

        try {
            field.setAccessible(true);
            return new ReflectResponse<>(field.get(handle));
        } catch (IllegalAccessException e) {
            // This method must be logged. It is critical and you can't recover
            // from it.
            e.printStackTrace();
            return new ReflectResponse<>(e);
        }
    }

    /**
     * Sets the value of a field
     *
     * @param field The field to set the value for
     * @param handle The handle to set it for
     * @param value The value to set it to
     *
     * @return The result if setting it. Will just be SUCCESSFUL but not have a
     * value.
     *
     * @throws NullPointerException if field is null
     */
    public static ReflectResponse<Void> setFieldValue(Field field, Object handle, Object value) {
        Objects.requireNonNull(field, "field can not be null");

        try {
            field.setAccessible(true);
            field.set(handle, value);
            return new ReflectResponse<>(ResultType.SUCCESSFUL);
        } catch (IllegalAccessException e) {
            // This method must be logged. It is critical and you can't recover
            // from it.
            e.printStackTrace();
            return new ReflectResponse<>(e);
        }
    }

    /**
     * Sets the value of a field
     *
     * @param clazz The clazz get the field from
     * @param selector The selector to match the field
     * @param handle The handle to set it for
     * @param value The value to set it to
     *
     * @return The result if setting it. Will just be SUCCESSFUL but not have a
     * value.
     *
     * @throws NullPointerException if clazz or selector is null
     * @see #setFieldValue(Field, Object, Object)
     */
    public static ReflectResponse<Void> setFieldValue(Class<?> clazz, Predicate<Field> selector, Object handle,
                                                      Object value) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(selector, "selector can not be null");

        ReflectResponse<Field> field = getField(clazz, selector);
        if (!field.isValuePresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }

        return setFieldValue(field.getValue(), handle, value);
    }

    /**
     * Sets the value of a field
     *
     * @param selector The name of the field
     * @param clazz The clazz get the field from
     * @param handle The handle to set it for
     * @param value The value to set it to
     *
     * @return The result if setting it. Will just be SUCCESSFUL but not have a
     * value.
     *
     * @throws NullPointerException if clazz or selector is null
     * @see #setFieldValue(Field, Object, Object)
     */
    public static ReflectResponse<Void> setFieldValue(String selector, Class<?> clazz, Object handle, Object value) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(selector, "selector can not be null");

        return setFieldValue(clazz, new MemberPredicate<Field>().withName(selector), handle, value);
    }
    // </editor-fold>

    // <editor-fold desc="Methods">
    // ==== METHODS ====

    /**
     * Returns the first method matching the selector
     *
     * @param clazz The class to get methods from
     * @param selector The Selector function to use
     *
     * @return The first function matching the selector
     *
     * @throws NullPointerException if any parameter is null
     */
    public static ReflectResponse<Method> getMethod(Class<?> clazz, Predicate<Method> selector) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(selector, "selector can not be null");

        Optional<Method> firstMethod = getMethods(clazz).filter(selector).findFirst();

        if (!firstMethod.isPresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }
        return new ReflectResponse<>(firstMethod.get());
    }

    /**
     * Invokes a method
     *
     * @param method The method to invoke
     * @param handle The handle of the method
     * @param params The parameters of the method
     *
     * @return The result of invoking the method.
     *
     * @throws NullPointerException if any parameter (except handle) is null
     */
    public static ReflectResponse<Object> invokeMethod(Method method, Object handle, Object... params) {
        Objects.requireNonNull(method, "method can not be null");
        Objects.requireNonNull(params, "params can not be null");

        try {
            method.setAccessible(true);
            return new ReflectResponse<>(method.invoke(handle, params));
        } catch (IllegalAccessException e) {
            // This method must be logged. It is critical and you can't recover
            // from it.
            e.printStackTrace();
            return new ReflectResponse<>(e);
        } catch (InvocationTargetException | IllegalArgumentException e) {
            return new ReflectResponse<>(e);
        }
    }

    /**
     * Invokes a method
     *
     * @param clazz The class to get the method from
     * @param selector The Selector function to use
     * @param handle The handle of the method
     * @param params The parameters of the method
     *
     * @return The result of invoking the method.
     *
     * @throws NullPointerException if any parameter (except handle) is null
     * @see #invokeMethod(Method, Object, Object...)
     */
    public static ReflectResponse<Object> invokeMethod(Class<?> clazz, Predicate<Method> selector, Object handle,
                                                       Object... params) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(selector, "selector can not be null");
        Objects.requireNonNull(params, "params can not be null");

        ReflectResponse<Method> method = getMethod(clazz, selector);
        if (!method.isValuePresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }

        return invokeMethod(method.getValue(), handle, params);
    }

    /**
     * Invokes an instance method
     *
     * @param handle The class to get the method from
     * @param selector The Selector function to use
     * @param params The parameters of the method
     *
     * @return The result of invoking the method.
     *
     * @throws NullPointerException if any parameter is null
     * @see #invokeMethod(Class, Predicate, Object, Object...)
     */
    public static ReflectResponse<Object> invokeInstanceMethod(Object handle, Predicate<Method> selector, Object...
            params) {
        Objects.requireNonNull(handle, "handle can not be null");
        Objects.requireNonNull(selector, "selector can not be null");
        Objects.requireNonNull(params, "params can not be null");
        return invokeMethod(handle.getClass(), selector, params);
    }

    /**
     * Invokes an instance method
     *
     * @param handle The class to get the method from
     * @param name The name of the method.
     * @param parameterClasses The classes of the parameters. Empty array for
     * none, {@code null} is <b>NOT</b> permitted.
     * @param params The parameters of the method
     *
     * @return The result of invoking the method.
     *
     * @throws NullPointerException if any parameter is null
     * @see #invokeMethod(Class, Predicate, Object, Object...)
     */
    public static ReflectResponse<Object> invokeInstanceMethod(Object handle, String name, Class<?>[]
            parameterClasses, Object... params) {
        Objects.requireNonNull(handle, "handle can not be null");
        Objects.requireNonNull(name, "name can not be null");
        Objects.requireNonNull(parameterClasses, "parameterClasses can not be null");
        Objects.requireNonNull(params, "params can not be null");

        return invokeMethod(handle.getClass(), new MethodPredicate().withParameters(parameterClasses)
                .withName(name), handle, params);
    }

    /**
     * Returns all methods (public {@code ->} private) from the class, filtered
     * by the
     * given predicate
     *
     * @param clazz The Class to get the methods from
     * @param predicate The predicate to use to filter
     *
     * @return All the methods in the class. No {@link ReflectResponse}, as the
     * only error that can occur is a SecurityException.
     *
     * @throws NullPointerException if any parameter is null
     */
    public static Stream<Method> getMethods(Class<?> clazz, Predicate<Method> predicate) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(predicate, "predicate can not be null");

        return getMethods(clazz).filter(predicate);
    }

    /**
     * Returns all methods (public {@code ->} private) from the class
     *
     * @param clazz The Class to get the methods from
     *
     * @return All the methods in the class.
     */
    private static Stream<Method> getMethods(Class<?> clazz) {
        return Stream.concat(Arrays.stream(clazz.getMethods()), Arrays.stream(clazz.getDeclaredMethods())).distinct();
    }
    // </editor-fold>

    // <editor-fold desc="Constructor">
    // ==== CONSTRUCTORS ====

    /**
     * Returns the first constructor matching the selector
     *
     * @param clazz The class to get the constructors from
     * @param selector The Selector function to use
     *
     * @return The first function matching the selector
     *
     * @throws NullPointerException if any parameter is null
     */
    public static ReflectResponse<Constructor<?>> getConstructor(Class<?> clazz, Predicate<Constructor<?>> selector) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(selector, "selector can not be null");

        Optional<Constructor<?>> firstConstructor = getAllConstructors(clazz).filter(selector).findFirst();

        if (!firstConstructor.isPresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }

        return new ReflectResponse<>(firstConstructor.get());
    }

    /**
     * Returns the first constructor matching the parameters
     *
     * @param clazz The class to get the constructors from
     * @param params The parameter of the constructor
     *
     * @return The first constructor with the given params
     *
     * @throws NullPointerException if any parameter is null
     * @see #getConstructor(Class, Predicate)
     */
    public static ReflectResponse<Constructor<?>> getConstructor(Class<?> clazz, Class<?>... params) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(params, "params can not be null");
        return getConstructor(clazz, new ExecutablePredicate<Constructor<?>>().withParameters(params));
    }

    /**
     * Instantiates the constructor
     *
     * @param constructor The constructor
     * @param params The parameters to pass
     * @param <T> The type of the class to instantiate
     *
     * @return The instantiated Object
     *
     * @throws NullPointerException if any parameter is null
     */
    public static <T> ReflectResponse<T> instantiate(Constructor<T> constructor, Object... params) {
        Objects.requireNonNull(constructor, "constructor can not be null");
        Objects.requireNonNull(params, "params can not be null");

        try {
            constructor.setAccessible(true);
            return new ReflectResponse<>(constructor.newInstance(params));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new ReflectResponse<>(e);
        } catch (InstantiationException | InvocationTargetException | IllegalArgumentException e) {
            return new ReflectResponse<>(e);
        }
    }

    /**
     * Instantiates the constructor
     *
     * @param clazz The class to get the constructors from
     * @param selector The Selector function to use
     * @param params The parameters to pass
     *
     * @return The instantiated Object
     *
     * @throws NullPointerException if any parameter is null
     * @see #instantiate(Constructor, Object...)
     */
    public static ReflectResponse<?> instantiate(Class<?> clazz, Predicate<Constructor<?>> selector, Object... params) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(selector, "selector can not be null");
        Objects.requireNonNull(params, "params can not be null");

        ReflectResponse<Constructor<?>> constructor = getConstructor(clazz, selector);

        if (!constructor.isValuePresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }

        return instantiate(constructor.getValue(), params);
    }

    /**
     * Returns all constructors (public {@code ->} private) from the class,
     * filtered by
     * the given predicate
     *
     * @param clazz The class to get the constructors from
     * @param predicate The predicate to use to filter
     *
     * @return All the constructors in the class. No {@link ReflectResponse}, as
     * the only error that can occur is a SecurityException.
     *
     * @throws NullPointerException if any parameter is null
     */
    public static Stream<Constructor<?>> getAllConstructors(Class<?> clazz, Predicate<Constructor<?>> predicate) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(predicate, "predicate can not be null");

        return getAllConstructors(clazz).filter(predicate);
    }

    /**
     * Returns all (public {@code ->} private) constructors of a class.
     *
     * @param clazz The Class to get the constructors for
     *
     * @return All the {@link Constructor}s of that class
     */
    private static Stream<Constructor<?>> getAllConstructors(Class<?> clazz) {
        return Stream.concat(Arrays.stream(clazz.getConstructors()), Arrays.stream(clazz.getDeclaredConstructors()))
                .distinct();
    }
    // </editor-fold>

    // <editor-fold desc="Enums">
    // ==== ENUMS ====

    /**
     * Tries to find an enum constant.
     *
     * @param clazz The class to get it from
     * @param predicate The predicate to filter with
     *
     * @return The found constant.
     *
     * @throws NullPointerException     if any parameter is null
     * @throws IllegalArgumentException if <code>clazz</code> is not an enum
     */
    public static ReflectResponse<Enum<?>> getEnumConstant(Class<?> clazz, Predicate<Enum<?>> predicate) {
        Objects.requireNonNull(clazz, "clazz can not be null");
        Objects.requireNonNull(predicate, "predicate can not be null");

        if (!clazz.isEnum()) {
            throw new IllegalArgumentException("The class is no enum: " + clazz.getName());
        }

        Optional<? extends Enum<?>> anEnum = Arrays.stream(clazz.getEnumConstants())
                .map(o -> (Enum<?>) o)
                .filter(predicate)
                .findFirst();

        if (!anEnum.isPresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }

        return new ReflectResponse<>(anEnum.get());
    }

    /**
     * Tries to find an enum constant.
     *
     * @param clazz The class to get it from
     * @param name The name it should match. Case sensitive.
     *
     * @return The found constant.
     *
     * @throws NullPointerException     if any parameter is null
     * @throws IllegalArgumentException if 'clazz' is not an enum
     */
    public static ReflectResponse<Enum<?>> getEnumConstant(Class<?> clazz, String name) {
        return getEnumConstant(clazz, anEnum -> anEnum.name().equals(name));
    }
    // </editor-fold>

    // <editor-fold desc="Utility Classes">
    // ==== UTILITY CLASSES ====

    // <editor-fold desc="NameSpace">

    /**
     * The namespaces
     */
    public enum NameSpace {
        /**
         * The {@code net.minecraft.server} namespace
         */
        NMS(Pattern.compile("\\{nms}\\.", Pattern.CASE_INSENSITIVE), string -> "net.minecraft.server." +
                SERVER_VERSION + "." + string),
        /**
         * The {@code org.bukkit.craftbukkit} namespace
         */
        OBC(Pattern.compile("\\{obc}\\.", Pattern.CASE_INSENSITIVE), string -> "org.bukkit.craftbukkit." +
                SERVER_VERSION + "." + string);

        private Pattern detectionPattern;
        private Function<String, String> resolverFunction;

        /**
         * @param detectionPattern The pattern to identify this type
         * @param resolverFunction Maps a class name to a fully qualified one
         */
        NameSpace(Pattern detectionPattern, Function<String, String> resolverFunction) {
            this.detectionPattern = detectionPattern;
            this.resolverFunction = resolverFunction;
        }

        /**
         * Checks if the input is this pattern
         *
         * @param input The input to check
         *
         * @return True if the pattern matches
         */
        private boolean matchesPattern(String input) {
            return detectionPattern.matcher(input).find();
        }

        /**
         * Removes the pattern from the String
         *
         * @param string The String to remove the pattern from
         *
         * @return The String without the pattern
         */
        private String removePattern(String string) {
            Matcher matcher = detectionPattern.matcher(string);
            if (!matcher.find()) {
                return string;
            }
            return string.replace(matcher.group(), "");
        }

        /**
         * Resolves a class name.
         * <p>
         * Format is: <br>
         * {@literal <relative class name>}
         *
         * @param className The class name to resolve.
         *
         * @return The resolved className
         */
        public String resolve(String className) {
            return resolverFunction.apply(removePattern(className));
        }

        /**
         * Returns the {@link NameSpace} which contains the identifier
         *
         * @param input The input string, containing the identifier (and what
         * else it wants)
         *
         * @return The NameSpace which has this identifier
         */
        public static Optional<NameSpace> getFromIdentifier(String input) {
            for (NameSpace nameSpace : values()) {
                if (nameSpace.matchesPattern(input)) {
                    return Optional.of(nameSpace);
                }
            }
            return Optional.empty();
        }
    }
    // </editor-fold>

    // <editor-fold desc="ReflectResponse">

    /**
     * The response to a reflective Operation.
     *
     * @param <T> The class that is wrapped
     */
    public static class ReflectResponse <T> {
        private T value;
        private ResultType resultType;
        private Throwable exception;

        private ReflectResponse(T value, ResultType resultType, Throwable exception) {
            this.value = value;
            this.resultType = resultType;
            this.exception = exception;
        }

        /**
         * Will automatically set {@link #getResultType()} to
         * {@link ResultType#ERROR}
         *
         * @param exception The exception that occurred
         */
        private ReflectResponse(Throwable exception) {
            this(null, ResultType.ERROR, exception);
        }

        /**
         * Will automatically set {@link #getResultType()} to
         * {@link ResultType#SUCCESSFUL}
         *
         * @param value The method value. May be null.
         */
        private ReflectResponse(T value) {
            this(value, ResultType.SUCCESSFUL, null);
        }

        /**
         * Will automatically set {@link #getValue()}} and
         * {@link #getException()} to null.
         *
         * @param resultType The type of the result.
         */
        private ReflectResponse(ResultType resultType) {
            this.resultType = resultType;
        }

        /**
         * Returns the value wrapped in an optional
         *
         * @return The value of present
         */
        public Optional<T> get() {
            return Optional.ofNullable(value);
        }

        /**
         * Returns the raw value
         *
         * @return The raw value
         */
        public T getValue() {
            return value;
        }

        /**
         * Returns the raw value, or throws an exception if the result is
         * {@link ResultType#ERROR} or {@link ResultType#NOT_FOUND}
         *
         * @param messages The messages to append to the Exception. May NOT be
         * null, but empty
         *
         * @return The raw value
         */
        public T getValueOrThrow(String... messages) {
            Objects.requireNonNull(messages, "messages can not be null!");

            if (getResultType() == ResultType.ERROR) {
                throw new RuntimeException(String.join(" - ", messages), getException());
            }
            else if (getResultType() == ResultType.NOT_FOUND) {
                throw new RuntimeException(String.join(" - ", messages) + " Result was 'NOT_FOUND'");
            }
            return getValue();
        }

        /**
         * Returns the result type
         *
         * @return The result type
         */
        public ResultType getResultType() {
            return resultType;
        }

        /**
         * Returns the thrown exception
         *
         * @return The exception. Only set if {@link #getResultType()} is
         * {@link ResultType#ERROR}
         */
        public Throwable getException() {
            return exception;
        }

        /**
         * Checks if the result type is successful
         *
         * @return True if the result type is SUCCESSFUL
         */
        public boolean isSuccessful() {
            return getResultType() == ResultType.SUCCESSFUL;
        }

        /**
         * Checks if the value is not null
         *
         * @return True if a value other than null is present
         */
        public boolean isValuePresent() {
            return getValue() != null;
        }

        @Override
        public String toString() {
            return "ReflectResponse{" + "=" + get() + ", successful=" + isSuccessful() + ", valuePresent=" +
                    isValuePresent() + '}';
        }

        /**
         * The result of the operation
         */
        public enum ResultType {
            /**
             * All went well
             */
            SUCCESSFUL,
            /**
             * If the method/field was not found
             */
            NOT_FOUND,
            /**
             * An error occurred
             */
            ERROR
        }
    }
    // </editor-fold>

    // <editor-fold desc="Predicates">
    // <editor-fold desc="Member Predicate">

    /**
     * A member predicate
     *
     * @param <T> The type of the member
     */
    public static class MemberPredicate <T extends Member> implements Predicate<T> {

        private String name;
        private Collection<Modifier> modifiers = Collections.emptyList();
        private Collection<Modifier> withoutModifier = Collections.emptyList();

        /**
         * @param name The name of the method. Null for don't check. Is a
         * <b>RegEx</b>
         * @param modifiers The modifiers. Empty list for don't check
         * @param withoutModifier The modifiers it must not have. Empty list for
         * don't check
         */
        public MemberPredicate(String name, Collection<Modifier> modifiers, Collection<Modifier> withoutModifier) {
            this.name = name;
            this.modifiers = modifiers;
            this.withoutModifier = withoutModifier;
        }

        /**
         * Accepts anything
         */
        public MemberPredicate() {

        }

        /**
         * Sets the modifiers
         *
         * @param modifiers The modifiers. An empty list for don't check.
         *
         * @return This predicate
         */
        public MemberPredicate<T> withModifiers(Collection<Modifier> modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        /**
         * Sets the modifiers
         *
         * @param modifiers The modifiers. An empty list for don't check.
         *
         * @return This predicate
         *
         * @see #withModifiers(Collection)
         */
        public MemberPredicate<T> withModifiers(Modifier... modifiers) {
            return withModifiers(Arrays.asList(modifiers));
        }

        /**
         * Sets the modifiers it <b>must not</b> have
         *
         * @param modifiers The modifiers. An empty list for don't check.
         *
         * @return This predicate
         */
        public MemberPredicate<T> withoutModifiers(Collection<Modifier> modifiers) {
            this.withoutModifier = new ArrayList<>(modifiers);
            return this;
        }

        /**
         * Sets the modifiers it <b>must not</b> have
         *
         * @param modifiers The modifiers. An empty list for don't check.
         *
         * @return This predicate
         *
         * @see #withoutModifiers(Collection)
         */
        public MemberPredicate<T> withoutModifiers(Modifier... modifiers) {
            return withoutModifiers(Arrays.asList(modifiers));
        }

        /**
         * Sets the name of the method
         *
         * @param name The name of the method. Null for don't check. Is a
         * <b>RegEx</b>
         *
         * @return This predicate
         */
        public MemberPredicate<T> withName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public boolean test(Member member) {
            if (name != null && !member.getName().matches(name)) {
                return false;
            }
            for (Modifier modifier : modifiers) {
                if (!modifier.isSet(member.getModifiers())) {
                    return false;
                }
            }
            for (Modifier modifier : withoutModifier) {
                if (modifier.isSet(member.getModifiers())) {
                    return false;
                }
            }
            return true;
        }
    }
    // </editor-fold>

    // <editor-fold desc="Field Predicate">

    /**
     * A predicate for {@link Field}s
     */
    public static class FieldPredicate extends MemberPredicate<Field> {

        private Class<?> type;

        /**
         * @param name The name of the method. Null for don't check. Is a
         * <b>RegEx</b>
         * @param modifiers The modifiers. Empty list for don't check
         * @param withoutModifier The modifiers it must not have. Empty list for
         * don't check
         * @param type The type of the Field. Null for don't check
         */
        public FieldPredicate(String name, Collection<Modifier> modifiers, Collection<Modifier> withoutModifier,
                              Class<?> type) {
            super(name, modifiers, withoutModifier);
            this.type = type;
        }

        /**
         * @param type The class of the type
         */
        public FieldPredicate(Class<?> type) {
            super();
            this.type = type;
        }

        public FieldPredicate() {
            super();
        }

        /**
         * Sets the type of the {@link Field}
         *
         * @param type The type of the {@link Field}
         *
         * @return This {@link FieldPredicate}
         */
        public FieldPredicate setType(Class<?> type) {
            this.type = type;
            return this;
        }

        // <editor-fold desc="Overwritten methods to change return type">
        @Override
        public FieldPredicate withName(String name) {
            return (FieldPredicate) super.withName(name);
        }

        @Override
        public FieldPredicate withModifiers(Collection<Modifier> modifiers) {
            return (FieldPredicate) super.withModifiers(modifiers);
        }

        @Override
        public FieldPredicate withModifiers(Modifier... modifiers) {
            return (FieldPredicate) super.withModifiers(modifiers);
        }

        @Override
        public FieldPredicate withoutModifiers(Collection<Modifier> modifiers) {
            return (FieldPredicate) super.withoutModifiers(modifiers);
        }

        @Override
        public FieldPredicate withoutModifiers(Modifier... modifiers) {
            return (FieldPredicate) super.withoutModifiers(modifiers);
        }
        // </editor-fold>

        @Override
        public boolean test(Member member) {
            if (!(member instanceof Field) || !super.test(member)) {
                return false;
            }
            Field field = (Field) member;

            return type == null || field.getType() == type;
        }
    }
    // </editor-fold>

    // <editor-fold desc="ExecutablePredicate">

    /**
     * A {@link Predicate} for an {@link Executable}
     *
     * @param <T> The type of the {@link Executable}
     */
    public static class ExecutablePredicate <T extends Executable> extends MemberPredicate<T> {

        private Class<?>[] parameters;

        /**
         * @param name The name of the method. Null for don't check. Is a
         * <b>RegEx</b>
         * @param modifiers The modifiers. Empty list for don't check
         * @param withoutModifier The modifiers it must not have. Empty list for
         * don't check
         * @param parameters The parameters. Null for don't check
         */
        public ExecutablePredicate(String name, Collection<Modifier> modifiers, Collection<Modifier> withoutModifier,
                                   Class<?>[] parameters) {
            super(name, modifiers, withoutModifier);
            this.parameters = parameters;
        }

        /**
         * An empty one. Just returns true.
         */
        public ExecutablePredicate() {
            super();
        }

        /**
         * Sets the required parameters
         *
         * @param parameters The parameters. Null for don't check.
         *
         * @return This predicate
         */
        public ExecutablePredicate<T> withParameters(Class<?>... parameters) {
            this.parameters = parameters;
            return this;
        }

        // <editor-fold desc="Overwritten methods to change Return type">
        // there must be a nicer way!
        @Override
        public ExecutablePredicate<T> withModifiers(Collection<Modifier> modifiers) {
            return (ExecutablePredicate<T>) super.withModifiers(modifiers);
        }

        @Override
        public ExecutablePredicate<T> withModifiers(Modifier... modifiers) {
            return (ExecutablePredicate<T>) super.withModifiers(modifiers);
        }

        @Override
        public ExecutablePredicate<T> withoutModifiers(Collection<Modifier> modifiers) {
            return (ExecutablePredicate<T>) super.withoutModifiers(modifiers);
        }

        @Override
        public ExecutablePredicate<T> withoutModifiers(Modifier... modifiers) {
            return (ExecutablePredicate<T>) super.withoutModifiers(modifiers);
        }

        @Override
        public ExecutablePredicate<T> withName(String name) {
            return (ExecutablePredicate<T>) super.withName(name);
        }
        // </editor-fold>

        @Override
        public boolean test(Member member) {
            if (!(member instanceof Executable) || !super.test(member)) {
                return false;
            }
            Executable executable = (Executable) member;
            if (parameters != null) {
                if (!Arrays.equals(executable.getParameterTypes(), parameters)) {
                    return false;
                }
            }

            return true;
        }
    }
    // </editor-fold>

    // <editor-fold desc="Method Predicate">

    /**
     * A Predicate for a method
     */
    public static class MethodPredicate extends ExecutablePredicate<Method> {

        private Class<?> returnType;

        /**
         * @param name The name of the method. Null for don't check. Is a
         * <b>RegEx</b>
         * @param modifiers The modifiers. Empty list for don't check
         * @param withoutModifier The modifiers it must not have. Empty list for
         * don't check
         * @param parameters The parameters. Null for don't check
         * @param returnType The return type. Null for don't check
         */
        public MethodPredicate(String name, Collection<Modifier> modifiers, Collection<Modifier> withoutModifier,
                               Class<?>[] parameters, Class<?> returnType) {
            super(name, modifiers, withoutModifier, parameters);
            this.returnType = returnType;
        }

        /**
         * An empty one. Just returns true.
         */
        public MethodPredicate() {
            super();
        }

        /**
         * Sets the required return type
         *
         * @param returnType The return type. Null for don't check
         *
         * @return This predicate
         */
        public MethodPredicate withReturnType(Class<?> returnType) {
            this.returnType = returnType;
            return this;
        }

        // <editor-fold desc="Overwritten methods to change return type">
        // there must be a nicer way!
        @Override
        public MethodPredicate withParameters(Class<?>... parameters) {
            return (MethodPredicate) super.withParameters(parameters);
        }

        @Override
        public MethodPredicate withModifiers(Collection<Modifier> modifiers) {
            return (MethodPredicate) super.withModifiers(modifiers);
        }

        @Override
        public MethodPredicate withModifiers(Modifier... modifiers) {
            return (MethodPredicate) super.withModifiers(modifiers);
        }

        @Override
        public MethodPredicate withoutModifiers(Collection<Modifier> modifiers) {
            return (MethodPredicate) super.withoutModifiers(modifiers);
        }

        @Override
        public MethodPredicate withoutModifiers(Modifier... modifiers) {
            return (MethodPredicate) super.withoutModifiers(modifiers);
        }

        @Override
        public MethodPredicate withName(String name) {
            return (MethodPredicate) super.withName(name);
        }
        // </editor-fold>

        @Override
        public boolean test(Member member) {
            if (!(member instanceof Method) || !super.test(member)) {
                return false;
            }
            Method method = (Method) member;
            return returnType == null || returnType.equals(method.getReturnType());
        }
    }
    // </editor-fold>
    // </editor-fold>

    // <editor-fold desc="Modifier">

    /**
     * The possible modifiers
     */
    public enum Modifier {
        PUBLIC(1),
        PRIVATE(2),
        PROTECTED(4),
        STATIC(8),
        FINAL(16),
        SYNCHRONIZED(32),
        VOLATILE(64),
        TRANSIENT(128),
        NATIVE(256),
        INTERFACE(512),
        ABSTRACT(1024),
        STRICT(2048);

        private int bitMask;

        /**
         * @param bitMask The bitmask of the modifier
         */
        Modifier(int bitMask) {
            this.bitMask = bitMask;
        }

        /**
         * Checks if the this modifier is set
         *
         * @param modifiers The modifiers
         *
         * @return True if the method has this modifier
         */
        public boolean isSet(int modifiers) {
            return (modifiers & bitMask) != 0;
        }

        @Override
        public String toString() {
            return "Modifier{" + "bitMask=" + bitMask + '}';
        }
    }
    // </editor-fold>
    // </editor-fold>
}