/**
 * 
 */
package com.perceivedev.perceivecore.reflection;

import java.lang.reflect.Method;

import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

/**
 * @author Rayzr
 *
 */
public class ReflectedMethod {

    protected Method method;
    protected Object instance;

    /**
     * @param method
     */
    protected ReflectedMethod(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    /**
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @return the instance
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Invokes the method with the given params
     * 
     * @param params the params to use
     * @return the reflect response
     */
    public ReflectResponse<Object> invoke(Object... params) {
        return ReflectionUtil.invokeMethod(method, instance, params);
    }

}
