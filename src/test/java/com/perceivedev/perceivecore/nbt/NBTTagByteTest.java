package com.perceivedev.perceivecore.nbt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.INBTBase;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagByte;

/** A bad test for bytes */
public class NBTTagByteTest {

    private NBTTagByte aByte  = new NBTTagByte((byte) 1);

    private final int  AMOUNT = 10000;

    @Test
    public void set() throws Exception {
        byte[] buffer = new byte[1];
        for (int i = 0; i < AMOUNT; i++) {
            ThreadLocalRandom.current().nextBytes(buffer);
            aByte.set(buffer[0]);
            assertEquals(buffer[0], aByte.getAsByte());
        }
    }

    @Test
    public void toNBT() throws Exception {
        aByte.toNBT();
    }

    @Test
    public void fromNBT() throws Exception {
        byte[] buffer = new byte[1];
        for (int i = 0; i < AMOUNT; i++) {
            ThreadLocalRandom.current().nextBytes(buffer);
            NBTTagByte nbtByte = new NBTTagByte(buffer[0]);
            Object nbt = nbtByte.toNBT();
            INBTBase base = NBTTagByte.fromNBT(nbt);
            if (!(base instanceof NBTWrappers.NBTTagByte)) {
                fail("Wrong class. Expected instance of NBTTagByte, got " + base.getClass().getName());
            }
            assertEquals(nbtByte, NBTTagByte.fromNBT(nbt));
        }
    }

    @Test
    public void equals() throws Exception {
        byte[] buffer = new byte[1];
        for (int i = 0; i < AMOUNT; i++) {
            ThreadLocalRandom.current().nextBytes(buffer);
            aByte.set(buffer[0]);
            assertEquals(new NBTTagByte(buffer[0]), aByte);
        }
    }

}