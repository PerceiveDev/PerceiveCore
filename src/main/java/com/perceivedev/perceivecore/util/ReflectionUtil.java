package com.perceivedev.perceivecore.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.nbt.ItemNBTUtil;
import com.perceivedev.perceivecore.nbt.NBTWrappers;
import com.perceivedev.perceivecore.nbt.NBTWrappers.INBTBase;
import com.perceivedev.perceivecore.util.ReflectionUtil.ReflectResponse.ResultType;

/**
 * Provides utility methods for reflection
 */
public class ReflectionUtil {

    private static final String SERVER_VERSION;

    // ==== INIT SERVER VERSION ====

    static {
        // String name = Bukkit.getServer().getClass().getName();
        String name = "org.bukkit.craftbukkit.v1_8_R3.CraftServer;";
        name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
        name = name.substring(0, name.indexOf("."));

        SERVER_VERSION = name;
    }

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

    // ==== CLASS SEARCH FUNCTIONS ====

    /**
     * Returns the class with the given name in the given package
     *
     * @param nameSpace The {@link NameSpace} of the class
     * @param qualifiedName The qualified name of the class inside the
     * {@link NameSpace}
     *
     * @return The Class, if found
     */
    public static Optional<Class<?>> getClass(NameSpace nameSpace, String qualifiedName) {
        Objects.requireNonNull(nameSpace);
        Objects.requireNonNull(qualifiedName);
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
     */
    public static Optional<Class<?>> getClass(String nameWithIdentifier) {
        Optional<NameSpace> fromIdentifier = NameSpace.getFromIdentifier(nameWithIdentifier);
        if (!fromIdentifier.isPresent()) {
            throw new IllegalArgumentException("Identifier unknown '" + nameWithIdentifier + "'");
        }
        return getClass(fromIdentifier.get(), nameWithIdentifier);
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

    // ==== FIELDS ====

    /**
     * Returns the first field matching the selector
     *
     * @param clazz The Class to get the fields for
     * @param selector The Selector function to use
     *
     * @return The first field matching the selector
     */
    public static ReflectResponse<Field> getField(Class<?> clazz, Predicate<Field> selector) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(selector);

        Optional<Field> first = getFields(clazz).filter(selector).findFirst();

        if (!first.isPresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }
        return new ReflectResponse<>(first.get());
    }

    /**
     * Returns ALL fields (public -> private) of a class
     *
     * @param clazz The Class to get the fields for
     *
     * @return The fields of the class
     */
    private static Stream<Field> getFields(Class<?> clazz) {
        return Stream.concat(Arrays.stream(clazz.getDeclaredFields()), Arrays.stream(clazz.getFields()));
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
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(selector);

        ReflectResponse<Field> field = getField(clazz, selector);
        if (!field.isValuePresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }

        return getFieldValue(field.getValue(), handle);
    }

    /**
     * Returns the value of a field
     *
     * @param clazz The clazz get the Field from
     * @param handle The handle to get it for
     * @param name The name of the field
     *
     * @return The value of the field.
     *
     * @throws NullPointerException if clazz or selector is null
     * @see #getFieldValue(Class, Object, Predicate)
     */
    public static ReflectResponse<Object> getFieldValue(Class<?> clazz, Object handle, String name) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(name);

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
     * @throws NullPointerException If field is null
     */
    public static ReflectResponse<Object> getFieldValue(Field field, Object handle) {
        Objects.requireNonNull(field);

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
     */
    public static ReflectResponse<Void> setFieldValue(Field field, Object handle, Object value) {
        Objects.requireNonNull(field);

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
     */
    public static ReflectResponse<Void> setFieldValue(Class<?> clazz, Predicate<Field> selector, Object handle, Object value) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(selector);

        ReflectResponse<Field> field = getField(clazz, selector);
        if (!field.isValuePresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }

        return setFieldValue(field.getValue(), handle, value);
    }

    // ==== METHODS ====

    /**
     * Returns the first method matching the selector
     *
     * @param clazz The class to get methods from
     * @param selector The Selector function to use
     *
     * @return The first function matching the selector
     */
    public static ReflectResponse<Method> getMethod(Class<?> clazz, Predicate<Method> selector) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(selector);

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
     */
    public static ReflectResponse<Object> invokeMethod(Method method, Object handle, Object... params) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(params);

        try {
            method.setAccessible(true);
            return new ReflectResponse<>(method.invoke(handle, params));
        } catch (IllegalAccessException e) {
            // This method must be logged. It is critical and you can't recover
            // from it.
            e.printStackTrace();
            return new ReflectResponse<>(e);
        } catch (InvocationTargetException e) {
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
     */
    public static ReflectResponse<Object> invokeMethod(Class<?> clazz, Predicate<Method> selector, Object handle, Object... params) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(selector);
        Objects.requireNonNull(params);

        ReflectResponse<Method> method = getMethod(clazz, selector);
        if (!method.isValuePresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }

        return invokeMethod(method.getValue(), handle, params);
    }

    /**
     * Returns all methods (public -> private) from the class
     *
     * @param clazz The Class to get the methods from
     *
     * @return All the methods in the class.
     */
    private static Stream<Method> getMethods(Class<?> clazz) {
        return Stream.concat(Arrays.stream(clazz.getMethods()), Arrays.stream(clazz.getDeclaredMethods()));
    }

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
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(selector);

        Optional<? extends Constructor<?>> firstConstructor = getAllConstructors(clazz).filter(selector).findFirst();

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
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(params);
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
        Objects.requireNonNull(constructor);
        Objects.requireNonNull(params);

        try {
            return new ReflectResponse<>(constructor.newInstance(params));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new ReflectResponse<>(e);
        } catch (InstantiationException | InvocationTargetException e) {
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
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(selector);
        Objects.requireNonNull(params);

        ReflectResponse<Constructor<?>> constructor = getConstructor(clazz, selector);

        if (!constructor.isValuePresent()) {
            return new ReflectResponse<>(ResultType.NOT_FOUND);
        }

        return instantiate(constructor.getValue(), params);
    }

    /**
     * Returns all (public -> private) constructors of a class.
     *
     * @param clazz The Class to get the constructors for
     *
     * @return All the {@link Constructor}s of that class
     */
    @SuppressWarnings("unchecked")  // it is checked. The methods only use
    // wildcards, as the array could be
    // modified, which it isn't
    private static <T> Stream<Constructor<T>> getAllConstructors(Class<T> clazz) {
        return Stream.concat(Arrays.stream(clazz.getConstructors()).map(constructor -> (Constructor<T>) constructor),
                  Arrays.stream(clazz.getDeclaredConstructors()).map(constructor -> (Constructor<T>) constructor));
    }

    // ==== UTILITY CLASSES ====

    /**
     * The namespaces
     */
    public enum NameSpace {
        /**
         * The {@code net.minecraft.server} namespace
         */
        NMS(Pattern.compile("\\{nms\\}\\.", Pattern.CASE_INSENSITIVE), string -> "net.minecraft.server." + SERVER_VERSION + "." + string),
        /**
         * The {@code org.bukkit.craftbukkit} namespace
         */
        OBC(Pattern.compile("\\{obc\\}\\.", Pattern.CASE_INSENSITIVE), string -> "org.bukkit.craftbukkit." + SERVER_VERSION + "." + string);

        private Pattern                  detectionPattern;
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

    /**
     * The response to a reflective Operation.
     *
     * @param <T> The class that is wrapped
     */
    public static class ReflectResponse<T> {
        private T          value;
        private ResultType resultType;
        private Throwable  exception;

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
            return "ReflectResponse{" + "=" + get() + ", successful=" + isSuccessful() + ", valuePresent=" + isValuePresent() + '}';
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

    public static class MemberPredicate<T extends Member> implements Predicate<T> {

        private String name;
        private Collection<Modifier> modifiers = Collections.emptyList();

        /**
         * @param name The name of the method. Null for don't check. Is a
         * <b>RegEx</b>
         * @param modifiers The modifiers. Empty list for don't check
         */
        public MemberPredicate(String name, Collection<Modifier> modifiers) {
            this.name = name;
            this.modifiers = modifiers;
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
            return true;
        }
    }

    public static class ExecutablePredicate<T extends Executable> extends MemberPredicate<T> {

        private Class<?>[] parameters;

        /**
         * @param name The name of the method. Null for don't check. Is a
         * <b>RegEx</b>
         * @param modifiers The modifiers. Empty list for don't check
         * @param parameters The parameters. Null for don't check
         */
        public ExecutablePredicate(String name, Collection<Modifier> modifiers, Class<?>[] parameters) {
            super(name, modifiers);
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
        public ExecutablePredicate<T> withName(String name) {
            return (ExecutablePredicate<T>) super.withName(name);
        }

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

    /**
     * A Predicate for a method
     */
    public static class MethodPredicate<T extends Method> extends ExecutablePredicate<T> {

        private Class<?> returnType;

        /**
         * @param name The name of the method. Null for don't check. Is a
         * <b>RegEx</b>
         * @param modifiers The modifiers. Empty list for don't check
         * @param parameters The parameters. Null for don't check
         * @param returnType The return type. Null for don't check
         */
        public MethodPredicate(String name, Collection<Modifier> modifiers, Class<?>[] parameters, Class<?> returnType) {
            super(name, modifiers, parameters);
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
        public MethodPredicate<T> withReturnType(Class<?> returnType) {
            this.returnType = returnType;
            return this;
        }

        // there must be a nicer way!
        @Override
        public MethodPredicate<T> withParameters(Class<?>... parameters) {
            return (MethodPredicate<T>) super.withParameters(parameters);
        }

        @Override
        public MethodPredicate<T> withModifiers(Collection<Modifier> modifiers) {
            return (MethodPredicate<T>) super.withModifiers(modifiers);
        }

        @Override
        public MethodPredicate<T> withModifiers(Modifier... modifiers) {
            return (MethodPredicate<T>) super.withModifiers(modifiers);
        }

        @Override
        public MethodPredicate<T> withName(String name) {
            return (MethodPredicate<T>) super.withName(name);
        }

        @Override
        public boolean test(Member member) {
            if (!(member instanceof Method) || !super.test(member)) {
                return false;
            }
            Method method = (Method) member;
            return returnType == null || returnType.equals(method.getReturnType());
        }
    }

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
    }

    public static void main(String[] args) {
        {
            System.out.println(" ");
            System.out.println("=== WRAPPERS ====");
            System.out.println(" ");
            System.out.println("=== BYTE ===");
            NBTWrappers.NBTTagByte nbtTagByte = new NBTWrappers.NBTTagByte((byte) 20);
            System.out.println(nbtTagByte);
            System.out.println(nbtTagByte.toNBT());
            System.out.println(INBTBase.fromNBT(nbtTagByte.toNBT()));
            System.out.println(nbtTagByte.equals(INBTBase.fromNBT(nbtTagByte.toNBT())));

            System.out.println("=== SHORT ===");
            NBTWrappers.NBTTagShort nbtTagShort = new NBTWrappers.NBTTagShort((short) 20);
            System.out.println(nbtTagShort);
            System.out.println(nbtTagShort.toNBT());
            System.out.println(INBTBase.fromNBT(nbtTagShort.toNBT()));
            System.out.println(nbtTagShort.equals(INBTBase.fromNBT(nbtTagShort.toNBT())));

            System.out.println("=== INT ===");
            NBTWrappers.NBTTagInt nbtTagInt = new NBTWrappers.NBTTagInt(20);
            System.out.println(nbtTagInt);
            System.out.println(nbtTagInt.toNBT());
            System.out.println(INBTBase.fromNBT(nbtTagInt.toNBT()));
            System.out.println(nbtTagInt.equals(INBTBase.fromNBT(nbtTagInt.toNBT())));

            System.out.println("=== LONG ===");
            NBTWrappers.NBTTagLong nbtTagLong = new NBTWrappers.NBTTagLong(20);
            System.out.println(nbtTagLong);
            System.out.println(nbtTagLong.toNBT());
            System.out.println(INBTBase.fromNBT(nbtTagLong.toNBT()));
            System.out.println(nbtTagLong.equals(INBTBase.fromNBT(nbtTagLong.toNBT())));

            System.out.println("=== BYTE ARRAY ===");
            NBTWrappers.NBTTagByteArray nbtTagByteArray = new NBTWrappers.NBTTagByteArray(new byte[] { 20, 21, 22, 23 });
            System.out.println(nbtTagByteArray);
            System.out.println(nbtTagByteArray.toNBT());
            System.out.println(INBTBase.fromNBT(nbtTagByteArray.toNBT()));
            System.out.println(nbtTagByteArray.equals(INBTBase.fromNBT(nbtTagByteArray.toNBT())));

            System.out.println("=== INT ARRAY ===");
            NBTWrappers.NBTTagIntArray nbtTagIntArray = new NBTWrappers.NBTTagIntArray(new int[] { 20, 21, 22, 23 });
            System.out.println(nbtTagIntArray);
            System.out.println(nbtTagIntArray.toNBT());
            System.out.println(INBTBase.fromNBT(nbtTagIntArray.toNBT()));
            System.out.println(nbtTagIntArray.equals(INBTBase.fromNBT(nbtTagIntArray.toNBT())));

            System.out.println("=== LIST ===");
            NBTWrappers.NBTTagList nbtTagList = new NBTWrappers.NBTTagList();
            nbtTagList.add(new NBTWrappers.NBTTagInt(50));
            nbtTagList.add(new NBTWrappers.NBTTagInt(100));
            System.out.println(nbtTagList);
            System.out.println(nbtTagList.toNBT());
            System.out.println(INBTBase.fromNBT(nbtTagList.toNBT()));
            System.out.println(nbtTagList.equals(INBTBase.fromNBT(nbtTagList.toNBT())));

            System.out.println("=== String ===");
            NBTWrappers.NBTTagString nbtTagString = new NBTWrappers.NBTTagString("This is a String");
            System.out.println(nbtTagString);
            System.out.println(nbtTagString.toNBT());
            System.out.println(INBTBase.fromNBT(nbtTagString.toNBT()));
            System.out.println(nbtTagString.equals(INBTBase.fromNBT(nbtTagString.toNBT())));

            System.out.println("=== COMPOUND ===");
            NBTWrappers.NBTTagCompound nbtTagCompound = new NBTWrappers.NBTTagCompound();
            nbtTagCompound.setString("Test", "This is a test");
            nbtTagCompound.setByte("TestByte", (byte) 20);
            nbtTagCompound.setShort("TestShort", (short) 900);
            nbtTagCompound.setInt("TestInt", 2032);
            nbtTagCompound.setLong("TestLong", Integer.MAX_VALUE * 2L);
            nbtTagCompound.setIntArray("TestIntArray", new int[] { 20, 21, 22, 23 });
            nbtTagCompound.setByteArray("TestByteArray", new byte[] { 20, 21, 22, 23 });
            nbtTagCompound.setBoolean("TestBoolean", true);
            System.out.println(nbtTagCompound);
            System.out.println(nbtTagCompound.toNBT());
            System.out.println(INBTBase.fromNBT(nbtTagCompound.toNBT()));
            System.out.println(nbtTagCompound.equals(INBTBase.fromNBT(nbtTagCompound.toNBT())));
        }

        {
            System.out.println(" ");
            System.out.println("=== ITEM NBT UTIL ===");
            System.out.println(" ");
            ItemStack stack = new ItemStack(Material.APPLE);
            NBTWrappers.NBTTagCompound tag = ItemNBTUtil.getTag(stack);
            System.out.println(tag);
            tag.setString("Test", "A nice test");
            tag.setInt("Test", -200);
            stack = ItemNBTUtil.setNBTTag(tag, stack);
            System.out.println(ItemNBTUtil.getTag(stack));
            System.out.println(ItemNBTUtil.getTag(stack));
            System.out.println(tag.equals(ItemNBTUtil.getTag(stack)));
        }
    }
}
