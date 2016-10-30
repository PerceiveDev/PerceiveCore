package com.perceivedev.perceivecore.nbt;

import static junit.framework.Assert.assertEquals;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagShort;

/**
 * A small test for shorts
 */
public class NBTTagShortTest {

    private NBTTagShort nbtTagShort = new NBTTagShort((short) 1);
    private short value;

    private final int AMOUNT = 10000;

    @Test
    public void set() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();
            assertEquals(value, nbtTagShort.getAsShort());
        }
    }

    private void setToRandom() {
        value = (short) ThreadLocalRandom.current().nextInt();
        nbtTagShort.set(value);
    }

    @Test
    public void toNBT() throws Exception {
        nbtTagShort.toNBT();
    }

    @Test
    public void fromNBT() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();

            Object nbt = nbtTagShort.toNBT();
            NBTTagShort reconstructed = (NBTTagShort) NBTTagShort.fromNBT(nbt);
            assertEquals(nbtTagShort, reconstructed);
        }
    }

    @Test
    public void equals() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();
            NBTTagShort other = new NBTTagShort(value);
            assertEquals(nbtTagShort, other);
        }
    }

}