/**
 * 
 */
package com.perceivedev.perceivecore.util;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.$;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.packet.Packet;

/** @author Rayzr */
public class Converters {

    private static Class<?> OBC_ITEM;
    private static Class<?> NMS_ITEM;

    private static Method   ITEM_TO_NMS;
    private static Method   ITEM_TO_BUKK;
    private static boolean  SETUP;

    static {

        try {

            OBC_ITEM = $("{obc}.inventory.CraftItemStack").get();
            NMS_ITEM = $("{nms}.ItemStack").get();

            ITEM_TO_NMS = OBC_ITEM.getMethod("asNMSCopy", ItemStack.class);
            ITEM_TO_BUKK = OBC_ITEM.getMethod("asBukkitCopy", NMS_ITEM);

            SETUP = true;

        } catch (Exception e) {

            SETUP = false;

            System.err.println("Failed to load Converters class! Please report this to the Perceive team:");
            System.err.println("https://github.com/PerceiveDev/PerceiveCore/issues/new");

            e.printStackTrace();

        }

    }

    public static final Converter<ItemStack, Object> ITEM_CONVERTER;

    static {

        if (SETUP) {

            ITEM_CONVERTER = new Converter<ItemStack, Object>() {

                @Override
                public Object convert(ItemStack input) {
                    try {
                        return ITEM_TO_NMS.invoke(null, input);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public ItemStack reverse(Object input) {
                    try {
                        return (ItemStack) ITEM_TO_BUKK.invoke(null, input);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

            };

            Packet.addConverter(ItemStack.class, NMS_ITEM, ITEM_CONVERTER);

        } else {

            ITEM_CONVERTER = null;

        }

    }

}
