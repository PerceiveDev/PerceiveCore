package com.perceivedev.perceivecore.nbt;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagInt;

import static junit.framework.Assert.assertEquals;

/**
 * A bad test for Ints
 */
public class NBTTagIntTest {

    private NBTTagInt nbtTagInt = new NBTTagInt(1);
    private int value;

    private final int AMOUNT = 10000;

    @Test
    public void set() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();
            assertEquals(value, nbtTagInt.getAsInt());
        }
    }

    private void setToRandom() {
        value = ThreadLocalRandom.current().nextInt();
        nbtTagInt.set(value);
    }

    @Test
    public void toNBT() throws Exception {
        nbtTagInt.toNBT();
    }

    @Test
    public void fromNBT() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();

            Object nbt = nbtTagInt.toNBT();
            NBTTagInt reconstructed = (NBTTagInt) NBTTagInt.fromNBT(nbt);
            assertEquals(nbtTagInt, reconstructed);
        }
    }

    @Test
    public void equals() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();
            NBTTagInt other = new NBTTagInt(value);
            assertEquals(nbtTagInt, other);
        }
    }

}