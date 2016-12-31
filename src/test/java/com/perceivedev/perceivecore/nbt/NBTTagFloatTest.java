package com.perceivedev.perceivecore.nbt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.INBTBase;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagFloat;

/** A bad test for floats */
public class NBTTagFloatTest {

    private NBTTagFloat nbtTagFloat = new NBTTagFloat(1);

    private final int AMOUNT = 10000;

    @Test
    public void set() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            float rand = ThreadLocalRandom.current().nextFloat();
            nbtTagFloat.set(rand);
            assertEquals(rand, nbtTagFloat.getAsFloat(), 0.001);
        }
    }

    @Test
    public void toNBT() throws Exception {
        nbtTagFloat.toNBT();
    }

    @Test
    public void fromNBT() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            float rand = ThreadLocalRandom.current().nextFloat();
            NBTTagFloat nbtTagFloat = new NBTTagFloat(rand);
            Object nbt = nbtTagFloat.toNBT();
            INBTBase base = NBTTagFloat.fromNBT(nbt);
            if (!(base instanceof NBTTagFloat)) {
                fail("Wrong class. Expected instance of NBTTagFloat, got " + base.getClass().getName());
            }
            assertEquals(nbtTagFloat, base);
        }
    }

    @Test
    public void equals() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            float rand = ThreadLocalRandom.current().nextFloat();
            NBTTagFloat first = new NBTTagFloat(rand);
            NBTTagFloat second = new NBTTagFloat(rand);
            assertEquals(first, second);
        }
    }

}