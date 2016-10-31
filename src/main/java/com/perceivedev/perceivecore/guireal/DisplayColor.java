/**
 * 
 */
package com.perceivedev.perceivecore.guireal;

import org.bukkit.Material;

/**
 * @author Rayzr
 *
 */
public enum DisplayColor {

    WHITE(true, Material.STAINED_GLASS_PANE, 0),
    ORANGE(true, Material.STAINED_GLASS_PANE, 1),
    MAGENTA(true, Material.STAINED_GLASS_PANE, 2),
    LIGHT_BLUE(true, Material.STAINED_GLASS_PANE, 3),
    YELLOW(true, Material.STAINED_GLASS_PANE, 4),
    LIME(true, Material.STAINED_GLASS_PANE, 5),
    PINK(true, Material.STAINED_GLASS_PANE, 6),
    DARK_GRAY(true, Material.STAINED_GLASS_PANE, 7),
    LIGHT_GRAY(true, Material.STAINED_GLASS_PANE, 8),
    CYAN(true, Material.STAINED_GLASS_PANE, 9),
    PURPLE(true, Material.STAINED_GLASS_PANE, 10),
    BLUE(true, Material.STAINED_GLASS_PANE, 11),
    BROWN(true, Material.STAINED_GLASS_PANE, 12),
    GREEN(true, Material.STAINED_GLASS_PANE, 13),
    RED(true, Material.STAINED_GLASS_PANE, 14),
    BLACK(true, Material.STAINED_GLASS_PANE, 15),
    IRON(false, Material.IRON_FENCE),
    CLEAR(false, Material.THIN_GLASS);

    private boolean  isDyeColor;
    private Material mat;
    private int      dataValue;

    DisplayColor(boolean isDyeColor, Material mat, int dataValue) {
        this.isDyeColor = isDyeColor;
        this.mat = mat;
        this.dataValue = dataValue;
    }

    DisplayColor(boolean isDyeColor, Material mat) {
        this(isDyeColor, mat, 0);
    }

    /**
     * @return if this is a dye color
     */
    public boolean isDyeColor() {
        return isDyeColor;
    }

    /**
     * @return the mat
     */
    public Material getMat() {
        return mat;
    }

    /**
     * @return the data value
     */
    public short getDataValue() {
        return (short) dataValue;
    }

}
