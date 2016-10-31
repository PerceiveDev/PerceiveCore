/**
 * 
 */
package com.perceivedev.perceivecore.guireal;

import java.util.function.BiFunction;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Rayzr
 *
 */
public class DisplayType {

    public static final DisplayType FLAT  = new DisplayType(Material.STAINED_GLASS_PANE, 15, (base, color) -> {
                                              if (color.isDyeColor()) {
                                                  return colorizer(base, color);
                                              } else {
                                                  base.setType(color.getMat());
                                                  return base;
                                              }
                                          });
    public static final DisplayType CUBE  = new DisplayType(Material.STAINED_CLAY, 5, DisplayType::colorizer);
    public static final DisplayType WOOL  = new DisplayType(Material.WOOL, 5, DisplayType::colorizer);
    public static final DisplayType DYE   = new DisplayType(Material.INK_SACK, 8, (base, color) -> {
                                              if (color.isDyeColor()) {
                                                  base.setDurability((short) (15 - color.getDataValue()));
                                              }
                                              return base;
                                          });

    public static final DisplayType EMPTY = new DisplayType(Material.AIR, 0, null);

    private static ItemStack colorizer(ItemStack base, DisplayColor color) {
        if (color.isDyeColor()) {
            base.setDurability(color.getDataValue());
        }
        return base;
    }

    private Material                                       material;
    private int                                            defaultDataValue;

    private BiFunction<ItemStack, DisplayColor, ItemStack> colorer;

    private DisplayType(Material material, int defaultDataValue, BiFunction<ItemStack, DisplayColor, ItemStack> colorer) {
        this.material = material;
        this.colorer = colorer;
        this.defaultDataValue = defaultDataValue;
    }

    public static DisplayType custom(Material material, int defaultDataValue, int amount) {
        return new DisplayType(material, defaultDataValue, (base, color) -> {
            base.setAmount(amount);
            return base;
        });
    }

    public static DisplayType custom(ItemStack item) {
        return new DisplayType(item.getType(), item.getDurability(), (base, color) -> item);
    }

    private ItemStack baseItem() {
        return new ItemStack(material, 1, (short) defaultDataValue);
    }

    /**
     * Gets the item with the specified color
     * 
     * @param color the {@link DisplayColor} to use
     * @return The ItemStack
     */
    public ItemStack getItem(DisplayColor color) {
        return colorer != null ? colorer.apply(baseItem(), color) : baseItem();
    }

}
