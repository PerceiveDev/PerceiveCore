package com.perceivedev.perceivecore;

/**
 * Some utility assert functions
 */
public class AssertUtil {

    public static void assertThrows(Runnable runnable, Class<? extends Throwable> exception) {
        boolean threw = false;
        try {
            runnable.run();
        } catch (Throwable e) {
            if (e.getClass() != exception) {
                throw new RuntimeException("Threw " + e.getClass().getName() + " exception: " + exception.getName(), e);
            }
            threw = true;
        }
        if (!threw) {
            throw new RuntimeException("Didn't throw any exception, expected: " + exception.getName());
        }
    }
}
