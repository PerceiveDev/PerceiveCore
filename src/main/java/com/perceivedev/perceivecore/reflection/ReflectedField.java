package com.perceivedev.perceivecore.reflection;

import java.lang.reflect.Field;

import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

/** @author Rayzr */
public class ReflectedField {

    protected Object instance;
    protected Field field;

    /**
     * @param instance The instance
     * @param field The field
     */
    protected ReflectedField(Object instance, Field field) {
        this.instance = instance;
        this.field = field;
    }

    /** @return the instance */
    public Object getInstance() {
        return instance;
    }

    /** @return the field */
    public Field getField() {
        return field;
    }

    /**
     * Gets the value of this field
     * 
     * @return the ReflectResponse
     */
    public ReflectResponse<Object> get() {
        return ReflectionUtil.getFieldValue(field, instance);
    }

    /**
     * Sets the value of this field
     * 
     * @param value the value to set
     * @return the ReflectResponse
     */
    public ReflectResponse<Void> set(Object value) {
        return ReflectionUtil.setFieldValue(field, instance, value);
    }

}
