package com.perceivedev.perceivecore.nbt;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.INBTBase;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagByte;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagCompound;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagDouble;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagInt;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagShort;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagString;

/** A bad test for Compounds */
public class NBTTagCompoundTest {

    private NBTTagCompound compound = new NBTTagCompound();

    private final int      AMOUNT   = 10000;

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
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] buf = new byte[60];
        for (int i = 0; i < AMOUNT; i++) {
            ThreadLocalRandom.current().nextBytes(buf);
            String value = encoder.encodeToString(buf);
            String key = Double.toString(i);

            compound.setString(key, value);

            assertTrue(compound.hasKey(key));

            assertEquals(value, compound.getString(key));

            compound.remove(key);
        }
    }

    @Test
    public void setByteArray() throws Exception {
        byte[] buf = new byte[60];
        for (int i = 0; i < AMOUNT; i++) {
            ThreadLocalRandom.current().nextBytes(buf);
            String key = Double.toString(i);

            compound.setByteArray(key, buf);

            assertTrue(compound.hasKey(key));

            assertArrayEquals(buf, compound.getByteArray(key));

            compound.remove(key);
        }
    }

    @Test
    public void setIntArray() throws Exception {
        int[] array;
        for (int i = 0; i < AMOUNT; i++) {
            array = ThreadLocalRandom.current().ints(40).toArray();
            String key = Double.toString(i);

            compound.setIntArray(key, array);

            assertTrue(compound.hasKey(key));

            assertArrayEquals(array, compound.getIntArray(key));

            compound.remove(key);
        }
    }

    @Test
    public void setBoolean() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            boolean value = ThreadLocalRandom.current().nextBoolean();
            String key = Double.toString(i);

            compound.setBoolean(key, value);

            assertTrue(compound.hasKey(key));

            assertEquals(value, compound.getBoolean(key));

            compound.remove(key);
        }
    }

    @Test
    public void hasKey() throws Exception {
        compound.setBoolean("test", false);

        assertTrue(compound.hasKey("test"));

        compound.remove("test");
    }

    @Test
    public void hasKeyOfType() throws Exception {
        compound.setString("test", "something");

        assertTrue(compound.hasKeyOfType("test", NBTTagString.class));

        assertFalse(compound.hasKeyOfType("test", NBTTagInt.class));

        compound.remove("test");
    }

    @Test
    public void isEmpty() throws Exception {
        assertTrue(compound.isEmpty());

        compound.setString("test", "something");

        assertFalse(compound.isEmpty());

        compound.remove("test");
    }

    @Test
    public void getAllEntries() throws Exception {
        for (int i = 0; i < 20; i++) {
            String key = Integer.toString(i);
            compound.setString(key, key);
        }
        assertEquals(20, compound.getAllEntries().size());
        for (Entry<String, INBTBase> entry : compound.getAllEntries().entrySet()) {
            assertTrue(entry.getValue() instanceof NBTTagString);
            Integer.parseInt(entry.getKey());
            Integer.parseInt(((NBTTagString) entry.getValue()).getString());
        }
    }

    private void setRandomValues() {
        INBTBase type = new NBTTagInt(0);
        switch (ThreadLocalRandom.current().nextInt(5)) {
            case 0:
                type = new NBTTagInt(1);
                break;
            case 1:
                type = new NBTTagString("");
                break;
            case 2:
                type = new NBTTagDouble(20);
                break;
            case 3:
                type = new NBTTagShort((short) 0);
                break;
            case 4:
                type = new NBTTagByte((byte) 0);
                break;
            default:
                // This is to stop FindBugs from being angry
                break;
        }

        compound = new NBTTagCompound();
        for (Entry<String, INBTBase> entry : getBunchOfType(type).entrySet()) {
            compound.set(entry.getKey(), entry.getValue());
        }
    }

    private Map<String, INBTBase> getBunchOfType(INBTBase type) {
        Map<String, INBTBase> map = new HashMap<>();
        for (int i = 0; i < 2000 + ThreadLocalRandom.current().nextInt(1000); i++) {
            String key = Integer.toString(i);

            if (type instanceof NBTTagInt) {
                map.put(key, new NBTTagInt(ThreadLocalRandom.current().nextInt()));
            } else if (type instanceof NBTTagString) {
                map.put(key, new NBTTagString(Integer.toHexString(ThreadLocalRandom.current().nextInt())));
            } else if (type instanceof NBTTagDouble) {
                map.put(key, new NBTTagDouble(ThreadLocalRandom.current().nextDouble()));
            } else if (type instanceof NBTTagShort) {
                map.put(key, new NBTTagShort((short) ThreadLocalRandom.current().nextInt()));
            } else if (type instanceof NBTTagByte) {
                map.put(key, new NBTTagByte((byte) ThreadLocalRandom.current().nextInt()));
            }
        }
        return map;
    }

    @Test
    public void toNBT() throws Exception {
        for (int i = 0; i < 10; i++) {
            setRandomValues();
            Object nbt = compound.toNBT();
            NBTTagCompound fromNBT = (NBTTagCompound) NBTTagCompound.fromNBT(nbt);
            assertEquals(compound, fromNBT);
        }
    }

}