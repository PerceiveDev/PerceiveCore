package com.perceivedev.perceivecore.nbt;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagCompound;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * A bad test for Compounds
 */
public class NBTTagCompoundTest {

    private NBTTagCompound compound = new NBTTagCompound();

    private final int AMOUNT = 10000;

    @Test
    public void remove() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            String key = Integer.toString(ThreadLocalRandom.current().nextInt());
            compound.setInt(key, i);
            assertTrue(compound.hasKey(key));
            compound.remove(key);
            assertFalse(compound.hasKey(key));
        }
    }

    @Test
    public void set() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            String key = Integer.toString(ThreadLocalRandom.current().nextInt());
            compound.setInt(key, i);
            assertTrue(compound.hasKey(key));
            compound.remove(key);
        }
    }

    @Test
    public void setByte() throws Exception {
        byte[] buffer = new byte[1];
        for (int i = 0; i < AMOUNT; i++) {
            String key = Integer.toString(ThreadLocalRandom.current().nextInt());
            ThreadLocalRandom.current().nextBytes(buffer);
            compound.setByte(key, buffer[0]);
            assertTrue(compound.hasKey(key));
            assertEquals(buffer[0], compound.getByte(key));
            compound.remove(key);
        }
    }

    @Test
    public void setShort() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            short rand = (short) ThreadLocalRandom.current().nextInt();
            String key = Short.toString(rand);

            compound.setShort(key, rand);

            assertTrue(compound.hasKey(key));

            assertEquals(rand, compound.getShort(key));

            compound.remove(key);
        }
    }

    @Test
    public void setInt() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            int rand = ThreadLocalRandom.current().nextInt();
            String key = Integer.toString(rand);

            compound.setInt(key, rand);

            assertTrue(compound.hasKey(key));

            assertEquals(rand, compound.getInt(key));

            compound.remove(key);
        }
    }

    @Test
    public void setLong() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            long rand = ThreadLocalRandom.current().nextLong();
            String key = Long.toString(rand);

            compound.setLong(key, rand);

            assertTrue(compound.hasKey(key));

            assertEquals(rand, compound.getLong(key));

            compound.remove(key);
        }
    }

    @Test
    public void setFloat() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            float rand = ThreadLocalRandom.current().nextFloat();
            String key = Float.toString(rand);

            compound.setFloat(key, rand);

            assertTrue(compound.hasKey(key));

            assertEquals(rand, compound.getFloat(key), 0.001);

            compound.remove(key);
        }
    }

    @Test
    public void setDouble() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            double rand = ThreadLocalRandom.current().nextDouble();
            String key = Double.toString(rand);

            compound.setDouble(key, rand);

            assertTrue(compound.hasKey(key));

            assertEquals(rand, compound.getDouble(key), 0.001);

            compound.remove(key);
        }
    }

    @Test
    public void setString() throws Exception {

    }

    @Test
    public void setByteArray() throws Exception {

    }

    @Test
    public void setIntArray() throws Exception {

    }

    @Test
    public void setBoolean() throws Exception {

    }

    @Test
    public void hasKey() throws Exception {

    }

    @Test
    public void hasKeyOfType() throws Exception {

    }

    @Test
    public void isEmpty() throws Exception {

    }

    @Test
    public void getAllEntries() throws Exception {

    }

    @Test
    public void toNBT() throws Exception {

    }

    @Test
    public void fromNBT() throws Exception {

    }

}