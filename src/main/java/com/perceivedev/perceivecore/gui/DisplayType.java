/**
 * 
 */
package com.perceivedev.perceivecore.gui;

import java.util.function.BiFunction;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Rayzr
 *
 */
public class DisplayType {

    public static final DisplayType FLAT = new DisplayType(Material.STAINED_GLASS_PANE, 15, (base, color) -> {
					     if (color.isDyeColor()) {
						 return colorizer(base, color);
					     } else {
						 base.setType(color.getMat());
						 return base;
					     }
					 });
    public static final DisplayType CUBE = new DisplayType(Material.STAINED_CLAY, 5, DisplayType::colorizer);
    public static final DisplayType WOOL = new DisplayType(Material.WOOL, 5, DisplayType::colorizer);
    public static final DisplayType DYE	 = new DisplayType(Material.INK_SACK, 8, (base, color) -> {
					     if (color.isDyeColor()) {
						 base.setDurability((short) (15 - color.getDataValue()));
					     }
					     return base;
					 });

    private static ItemStack colorizer(ItemStack base, DisplayColor color) {
	if (color.isDyeColor()) {
	    base.setDurability(color.getDataValue());
	}
	return base;
    }

    private Material					   material;
    private int						   defaultDataValue;

    private BiFunction<ItemStack, DisplayColor, ItemStack> colorer;

    public DisplayType(Material material, int defaultDataValue, BiFunction<ItemStack, DisplayColor, ItemStack> colorer) {
	this.material = material;
	this.colorer = colorer;
	this.defaultDataValue = defaultDataValue;
    }

    private ItemStack baseItem() {
	return new ItemStack(material, 1, (short) defaultDataValue);
    }

    public ItemStack getItem(DisplayColor color) {
	return colorer.apply(baseItem(), color);
    }

}
