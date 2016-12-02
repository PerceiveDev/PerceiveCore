package com.perceivedev.perceivecore.nbt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.perceivedev.perceivecore.nbt.NBTWrappers.INBTBase;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagDouble;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagInt;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagList;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagShort;
import com.perceivedev.perceivecore.nbt.NBTWrappers.NBTTagString;

/** A bad test for lists. */
public class NBTTagListTest {

    private NBTTagList list = new NBTTagList();
    private INBTBase type;
    private List<INBTBase> values;

    private final int AMOUNT = 1000;

    @Test
    public void add() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            toRandomType();
            assertEquals(values.size(), list.size());
            assertEquals(values.get(i), list.get(i));
        }
    }

    private void toRandomType() {
        list = new NBTTagList();
        switch (ThreadLocalRandom.current().nextInt(4)) {
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
            default:
                // FindBugs yells at us without this...
                break;
        }

        toType(type);
    }

    private List<INBTBase> getBunchOfType(INBTBase type) {
        List<INBTBase> list = new ArrayList<>();
        for (int i = 0; i < 2000 + ThreadLocalRandom.current().nextInt(1000); i++) {
            if (type instanceof NBTTagInt) {
                list.add(new NBTTagInt(ThreadLocalRandom.current().nextInt()));
            } else if (type instanceof NBTTagString) {
                list.add(new NBTTagString(Integer.toHexString(ThreadLocalRandom.current().nextInt())));
            } else if (type instanceof NBTTagDouble) {
                list.add(new NBTTagDouble(ThreadLocalRandom.current().nextDouble()));
            } else if (type instanceof NBTTagShort) {
                list.add(new NBTTagShort((short) ThreadLocalRandom.current().nextInt()));
            }
        }
        return list;
    }

    private void toType(INBTBase type) {
        values = getBunchOfType(type);

        values.forEach(list::add);
    }

    @Test
    public void isType() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            toRandomType();
            assertTrue(list.isType(type.getClass()));
            assertFalse(list.isType(NBTTagList.class));
        }
    }

    @Test
    public void toNBT() throws Exception {
        list.toNBT();
    }

    @Test
    public void fromNBT() throws Exception {
        List<INBTBase> types = Arrays.asList(new NBTTagInt(1), new NBTTagString(""), new NBTTagDouble(1), new NBTTagShort((short) 1));
        for (int i = 0; i < types.size(); i++) {
            toRandomType();
            Object nbt = list.toNBT();

            NBTTagList reconstructed = (NBTTagList) NBTTagList.fromNBT(nbt);

            assertEquals(list, reconstructed);
        }
    }

    @Test
    public void equals() throws Exception {
        for (int i = 0; i < AMOUNT; i++) {
            toRandomType();
            NBTTagList other = new NBTTagList();
            values.forEach(other::add);

            assertEquals(list, other);
        }
    }

}