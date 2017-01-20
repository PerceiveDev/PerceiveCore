package com.perceivedev.perceivecore.utilities.random;

import java.util.Objects;

/**
 * A simple {@link WeightedObject} wrapping some value.
 * <p>
 * {@link #hashCode()} and {@link #equals(Object)} are based on the weight and the respective methods of the wrapped
 * value
 */
public class SimpleWeightedObject <T> implements WeightedObject {

    private T value;
    private double weight;

    /**
     * @param value The value
     * @param weight The weight of the Object
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public SimpleWeightedObject(T value, double weight) {
        Objects.requireNonNull(value, "value can not be null!");

        this.value = value;
        this.weight = weight;
    }

    /**
     * Returns the wrapped value
     *
     * @return The value
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns the weight of the object
     *
     * @return The Weight
     */
    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleWeightedObject)) {
            return false;
        }
        SimpleWeightedObject<?> that = (SimpleWeightedObject<?>) o;
        return Double.compare(that.weight, weight) == 0 &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, weight);
    }

    @Override
    public String toString() {
        return "SimpleWeightedObject{" +
                "value=" + value +
                ", weight=" + weight +
                '}';
    }
}
