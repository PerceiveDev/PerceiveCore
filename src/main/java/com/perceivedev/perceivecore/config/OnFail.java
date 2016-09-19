
package com.perceivedev.perceivecore.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents how the deserializer will respond if a variable fails to load
 * 
 * @author Rayzr
 * 
 * @see FailResponse
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OnFail {

	public FailResponse value();

}
