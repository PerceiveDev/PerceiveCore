package com.perceivedev.perceivecore.nbt;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagLong;

/** A bad test for longs */
public class NBTTagLongTest {

    private NBTTagLong nbtTagLong = new NBTTagLong(1);
    private long       value;

    private final int  AMOUNT     = 10000;

    @Test
    public void set() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();
            assertEquals(value, nbtTagLong.getAsLong());
        }
    }

    private void setToRandom() {
        value = ThreadLocalRandom.current().nextLong();
        nbtTagLong.set(value);
    }

    @Test
    public void toNBT() throws Exception {
        nbtTagLong.toNBT();
    }

    @Test
    public void fromNBT() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();

            Object nbt = nbtTagLong.toNBT();
            NBTTagLong reconstructed = (NBTTagLong) NBTTagLong.fromNBT(nbt);
            assertEquals(nbtTagLong, reconstructed);
        }
    }

    @Test
    public void equals() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();
            NBTTagLong other = new NBTTagLong(value);
            assertEquals(nbtTagLong, other);
        }
    }

}