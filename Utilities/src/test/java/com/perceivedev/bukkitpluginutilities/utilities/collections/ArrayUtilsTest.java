package com.perceivedev.bukkitpluginutilities.utilities.collections;

import java.lang.reflect.Array;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the Array Utils class
 */
public class ArrayUtilsTest {

    @Test
    void concat() {
        String[] strings = generateArray(
                () -> Integer.toString(ThreadLocalRandom.current().nextInt()),
                ThreadLocalRandom.current().nextInt(100),
                String.class
        );
        String delimiter = ", ";
        String joined = String.join(delimiter, strings);

        Assertions.assertEquals(joined, ArrayUtils.concat(strings, delimiter));
    }

    private <T> T[] generateArray(Supplier<T> supplier, int size, Class<T> tClass) {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(tClass, size);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.get();
        }
        return array;
    }

    @Test
    void contains() {
        String[] strings = generateArray(
                () -> Integer.toString(ThreadLocalRandom.current().nextInt()),
                ThreadLocalRandom.current().nextInt(100),
                String.class
        );
        String probe = strings[ThreadLocalRandom.current().nextInt(strings.length)];

        Assertions.assertTrue(ArrayUtils.contains(strings, probe));
    }

}