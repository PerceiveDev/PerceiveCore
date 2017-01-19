package com.perceivedev.perceivecore.nbt;

import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.MethodPredicate;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.Modifier;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.NameSpace.OBC;


/**
 * A Util to save NBT data to ItemStacks
 */
public class ItemNBTUtil {

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static final Class<?> CRAFT_ITEM_STACK_CLASS = ReflectionUtil.getClass(OBC, "inventory.CraftItemStack")
            .get();

    /**
     * @param itemStack The {@link ItemStack} to convert
     *
     * @return The NMS Item stack
     */
    private static Object asNMSCopy(ItemStack itemStack) {
        return ReflectionUtil.invokeMethod(CRAFT_ITEM_STACK_CLASS, new MethodPredicate()
                        .withName("asNMSCopy")
                        .withParameters(ItemStack.class),
                null, itemStack).getValue();
    }

    /**
     * Only pass a NMS Itemstack!
     *
     * @param nmsItem The NMS item to convert
     *
     * @return The converted Item
     */
    private static ItemStack asBukkitCopy(Object nmsItem) {
        return (ItemStack) ReflectionUtil.invokeMethod(CRAFT_ITEM_STACK_CLASS, new MethodPredicate()
                        .withName("asBukkitCopy").withModifiers(Modifier.PUBLIC, Modifier.STATIC),
                null, nmsItem).getValue();
    }

    /**
     * Sets the NBT tag of an item
     *
     * @param tag The new tag
     * @param itemStack The ItemStack
     *
     * @return The modified itemStack
     */
    @SuppressWarnings("WeakerAccess")
    public static ItemStack setNBTTag(NBTWrappers.NBTTagCompound tag, ItemStack itemStack) {
        Object nbtTag = tag.toNBT();
        Object nmsItem = asNMSCopy(itemStack);
        ReflectionUtil.invokeMethod(nmsItem.getClass(), new MethodPredicate()
                .withName("setTag")
                .withModifiers(Modifier.PUBLIC), nmsItem, nbtTag);

        return asBukkitCopy(nmsItem);
    }

    /**
     * Gets the NBTTag of an item. In case of any error it returns a blank one.
     *
     * @param itemStack The ItemStack to get the tag for
     *
     * @return The NBTTagCompound of the ItemStack or a new one if it had none
     * or an error occurred
     */
    @SuppressWarnings("WeakerAccess")
    public static NBTWrappers.NBTTagCompound getTag(ItemStack itemStack) {
        Object nmsItem = asNMSCopy(itemStack);
        Object tag = ReflectionUtil.invokeMethod(nmsItem.getClass(), new MethodPredicate()
                .withName("getTag").withParameters(), nmsItem).getValue();
        if (tag == null) {
            return new NBTWrappers.NBTTagCompound();
        }
        NBTWrappers.INBTBase base = NBTWrappers.INBTBase.fromNBT(tag);
        if (base == null || base.getClass() != NBTWrappers.NBTTagCompound.class) {
            return new NBTWrappers.NBTTagCompound();
        }

        return (NBTWrappers.NBTTagCompound) base;
    }
}
