package com.perceivedev.perceivecore.nbt;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.INBTBase;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagByteArray;

/** A bad test for ByteArrays */
public class NBTTagByteArrayTest {

    private NBTTagByteArray array;
    private byte[]          data = new byte[2000];

    @Before
    public void init() {
        ThreadLocalRandom.current().nextBytes(data);
        array = new NBTTagByteArray(data);
    }

    @Test
    public void getValue() throws Exception {
        byte[] value = array.getValue();
        assertEquals("Length must be the same", data.length, value.length);

        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], value[i]);
        }
    }

    @Test
    public void toNBT() throws Exception {
        // just watch for exceptions
        array.toNBT();
    }

    @Test
    public void fromNBT() throws Exception {
        Object nbt = array.toNBT();
        INBTBase base = NBTTagByteArray.fromNBT(nbt);
        if (!(base instanceof NBTTagByteArray)) {
            fail("Wrong class. Expected instance of NBTTagByteArray, got " + base.getClass().getName());
        }
        assertEquals(array, base);
    }

    @Test
    public void equals() throws Exception {
        NBTTagByteArray second = new NBTTagByteArray(data);
        NBTTagByteArray third = new NBTTagByteArray(data);

        assertTrue(second.equals(array));
        assertTrue(second.equals(third));
        assertTrue(third.equals(array));
    }

}