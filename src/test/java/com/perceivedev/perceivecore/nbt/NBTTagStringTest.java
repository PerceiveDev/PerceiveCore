package com.perceivedev.perceivecore.nbt;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagString;

import static junit.framework.Assert.assertEquals;

/**
 * A bad tes for strings
 */
public class NBTTagStringTest {

    private NBTTagString nbtTagString = new NBTTagString("");
    private String value;

    private final int AMOUNT = 10000;

    @Test
    public void setString() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();
            assertEquals(value, nbtTagString.getString());
        }
    }

    private void setToRandom() {
        value = Integer.toHexString(ThreadLocalRandom.current().nextInt());
        nbtTagString.setString(value);
    }

    @Test
    public void toNBT() throws Exception {
        nbtTagString.toNBT();
    }

    @Test
    public void fromNBT() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();
            Object nbt = nbtTagString.toNBT();

            NBTTagString reconstructed = (NBTTagString) NBTTagString.fromNBT(nbt);

            assertEquals(nbtTagString, reconstructed);
        }
    }

    @Test
    public void equals() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            setToRandom();
            NBTTagString other = new NBTTagString(value);

            assertEquals(nbtTagString, other);
        }
    }

}