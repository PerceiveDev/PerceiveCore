package com.perceivedev.perceivecore.nbt;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.INBTBase;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagDouble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/** A bad test for doubles */
public class NBTTagDoubleTest {

    private NBTTagDouble nbtTagDouble = new NBTTagDouble(1);

    private final int    AMOUNT       = 10000;

    @Test
    public void set() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            double rand = ThreadLocalRandom.current().nextDouble();
            nbtTagDouble.set(rand);
            assertEquals(rand, nbtTagDouble.getAsDouble(), 0.001);
        }
    }

    @Test
    public void toNBT() throws Exception {
        nbtTagDouble.toNBT();
    }

    @Test
    public void fromNBT() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            double rand = ThreadLocalRandom.current().nextDouble();
            NBTTagDouble nbtTagDouble = new NBTTagDouble(rand);
            Object nbt = nbtTagDouble.toNBT();
            INBTBase base = NBTTagDouble.fromNBT(nbt);
            if (!(base instanceof NBTWrappers.NBTTagDouble)) {
                fail("Wrong class. Expected instance of NBTTagByte, got " + base.getClass().getName());
            }
            assertEquals(nbtTagDouble, base);
        }
    }

    @Test
    public void equals() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            double rand = ThreadLocalRandom.current().nextDouble();
            NBTTagDouble first = new NBTTagDouble(rand);
            NBTTagDouble second = new NBTTagDouble(rand);
            assertEquals(first, second);
        }
    }

}