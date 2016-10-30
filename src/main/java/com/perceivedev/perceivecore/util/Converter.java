/**
 * 
 */
package com.perceivedev.perceivecore.util;

/**
 * @author Rayzr
 *
 */
public interface Converter<A, B> {

    B convert(A input);

    A reverse(B input);

}
