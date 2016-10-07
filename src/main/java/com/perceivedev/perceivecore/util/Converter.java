/**
 * 
 */
package com.perceivedev.perceivecore.util;

/**
 * @author Rayzr
 *
 */
public interface Converter<A extends Object, B extends Object> {

    public B convert(A input);

    public A reverse(B input);

}
