package com.perceivedev.perceivecore.utilities.random;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

/**
 * A test for The {@link RandomPicker}
 */
class RandomPickerTest {

    private RandomPicker<SimpleWeightedObject<Integer>> randomPicker;
    private int objectAmount = 10;
    private final int CALL_AMOUNT = 100_000_000;

    {
        initRandomPicker();
    }

    private void initRandomPicker() {
        randomPicker = new RandomPicker<>();
        for (int i = 0; i < objectAmount; i++) {
            randomPicker.add(new SimpleWeightedObject<>(i, 1.0 / objectAmount));
        }
    }

    /**
     * Just checks if the outliers are in the range of {@code CALL_AMOUNT * 0.0001}
     */
    @Test
    public void testRandom10Objects() {
        Map<SimpleWeightedObject<Integer>, Integer> outliers = getOutliers(
                countTypes(
                        () -> randomPicker.getRandom(), CALL_AMOUNT
                ),
                CALL_AMOUNT
        );
        for (Integer integer : outliers.values()) {
            if (Math.abs(integer) > CALL_AMOUNT * 0.0001) {
                throw new RuntimeException("Too many outliers!");
            }
        }
    }

    /**
     * Just checks if the outliers are in the range of {@code CALL_AMOUNT * 0.0001}
     */
    @Test
    public void testRandom100Objects() {
        objectAmount = 100;
        initRandomPicker();

        Map<SimpleWeightedObject<Integer>, Integer> outliers = getOutliers(
                countTypes(
                        () -> randomPicker.getRandom(), CALL_AMOUNT
                ),
                CALL_AMOUNT
        );
        for (Integer integer : outliers.values()) {
            if (Math.abs(integer) > CALL_AMOUNT * 0.0001) {
                throw new RuntimeException("Too many outliers!");
            }
        }
    }

    private <Y> Map<Y, Integer> countTypes(Supplier<Y> supplier, int callAmount) {
        Map<Y, Integer> map = new HashMap<>();

        for (int i = 0; i < callAmount; i++) {
            Y object = supplier.get();
            map.putIfAbsent(object, 0);
            map.compute(object, (t, integer) -> integer + 1);
        }

        return map;
    }

    private <T> Map<T, Integer> getOutliers(Map<T, Integer> map, int callAmount) {
        int expectedAmountPerObject = (int) Math.round((double) callAmount / map.size());

        Map<T, Integer> outlier = new HashMap<>();

        for (Map.Entry<T, Integer> entry : map.entrySet()) {
            int difference = expectedAmountPerObject - entry.getValue();
            outlier.put(entry.getKey(), difference);
        }

        return outlier;
    }
}