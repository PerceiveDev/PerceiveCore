package com.perceivedev.perceivecore.nbt;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagIntArray;

/** A bad test for an IntArray */
public class NBTTagIntArrayTest {

    private NBTTagIntArray array;
    private int[] data = new int[2000];

    private final int AMOUNT = 10000;

    @Before
    public void init() {
        setRandomData();
    }

    @Test
    public void getValue() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setRandomData();
            assertArrayEquals(data, array.getValue());
        }
    }

    private void setRandomData() {
        data = ThreadLocalRandom.current().ints(2000).toArray();
        array = new NBTTagIntArray(data);
    }

    @Test
    public void toNBT() throws Exception {
        array.toNBT();
    }

    @Test
    public void fromNBT() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setRandomData();
            Object nbt = array.toNBT();
            NBTTagIntArray reconstructed = (NBTTagIntArray) NBTTagIntArray.fromNBT(nbt);
            assertEquals(array, reconstructed);
        }
    }

    @Test
    public void equals() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setRandomData();
            int[] otherData = Arrays.copyOf(data, data.length);
            NBTTagIntArray other = new NBTTagIntArray(otherData);
            assertEquals(array, other);
        }
    }

}