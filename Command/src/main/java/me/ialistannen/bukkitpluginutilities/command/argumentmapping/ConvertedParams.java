package me.ialistannen.bukkitpluginutilities.command.argumentmapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks to what the parameters should be converted
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConvertedParams {

    Class<?>[] targetClasses();
}
