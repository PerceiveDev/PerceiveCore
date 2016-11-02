/**
 * 
 */
package com.perceivedev.perceivecore.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.perceivedev.perceivecore.reflection.ReflectionUtil.MethodPredicate;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

/**
 * A wrapper around both objects and classes, allowing for easy getting and
 * setting of fields, as well as invoking methods. <br>
 * <br>
 * This is inspired by <a href="https://www.github.com/phase/mirror">Mirror</a>
 * 
 * @author Rayzr
 */
public class ReflectedClass<T> {

    protected T        instance;
    protected Class<?> clazz;

    protected ReflectedClass(T instance) {
        this.instance = instance;
        clazz = instance.getClass();
    }

    /**
     * Returns the name of this class
     * 
     * @return the name
     */
    public String getSimpleName() {
        return clazz.getSimpleName();
    }

    /**
     * Returns the full name of this class
     * 
     * @return the name
     */
    public String getName() {
        return clazz.getCanonicalName();
    }

    /** @return the instance */
    public T getInstance() {
        return instance;
    }

    /**
     * Gets a {@link ReflectedMethod} with the given name
     * 
     * @param name the name of the method
     * @return the method, or null if none could be found.
     */
    public ReflectedMethod getMethod(String name) {
        ReflectResponse<Method> response = ReflectionUtil.getMethod(clazz, new MethodPredicate().withName(name));
        if (!response.isSuccessful() || !response.isValuePresent()) {
            return null;
        }
        return new ReflectedMethod(instance, response.getValue());
    }

    /**
     * Gets a {@link ReflectedMethod} with the given name and parameters
     * 
     * @param name the name of the method
     * @param params the parameters of the method
     * @return the method, or null if none could be found.
     */
    public ReflectedMethod getMethod(String name, Class<?>... params) {
        ReflectResponse<Method> response = ReflectionUtil.getMethod(clazz, new MethodPredicate().withName(name).withParameters(params));
        if (!response.isSuccessful() || !response.isValuePresent()) {
            return null;
        }
        return new ReflectedMethod(instance, response.getValue());
    }

    /**
     * Gets a {@link ReflectedField} with the given name
     * 
     * @param name the name of the field
     * @return the field, or null if none could be found.
     */
    public ReflectedField getField(String name) {
        ReflectResponse<Field> response = ReflectionUtil.getField(clazz, f -> f.getName().equals(name));
        if (!response.isSuccessful() || !response.isValuePresent()) {
            return null;
        }
        return new ReflectedField(instance, response.getValue());
    }

}
