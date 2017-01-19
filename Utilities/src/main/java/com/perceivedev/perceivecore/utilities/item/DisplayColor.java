package com.perceivedev.perceivecore.utilities.item;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Material;

/**
 * The different colours you can display
 */
public enum DisplayColor {

    // follows glass + glass panes + + stained clay + wool ids + carpet ids. Dye
    // (INK_SACK) is (15 - dataValue).
    @SuppressWarnings("unused")
    WHITE(true, Material.STAINED_GLASS_PANE, 0),
    @SuppressWarnings("unused")
    ORANGE(true, Material.STAINED_GLASS_PANE, 1),
    @SuppressWarnings("unused")
    MAGENTA(true, Material.STAINED_GLASS_PANE, 2),
    @SuppressWarnings("unused")
    LIGHT_BLUE(true, Material.STAINED_GLASS_PANE, 3),
    @SuppressWarnings("unused")
    YELLOW(true, Material.STAINED_GLASS_PANE, 4),
    @SuppressWarnings("unused")
    LIME(true, Material.STAINED_GLASS_PANE, 5),
    @SuppressWarnings("unused")
    PINK(true, Material.STAINED_GLASS_PANE, 6),
    @SuppressWarnings("unused")
    DARK_GRAY(true, Material.STAINED_GLASS_PANE, 7),
    @SuppressWarnings("unused")
    LIGHT_GRAY(true, Material.STAINED_GLASS_PANE, 8),
    @SuppressWarnings("unused")
    CYAN(true, Material.STAINED_GLASS_PANE, 9),
    @SuppressWarnings("unused")
    PURPLE(true, Material.STAINED_GLASS_PANE, 10),
    @SuppressWarnings("unused")
    BLUE(true, Material.STAINED_GLASS_PANE, 11),
    @SuppressWarnings("unused")
    BROWN(true, Material.STAINED_GLASS_PANE, 12),
    @SuppressWarnings("unused")
    GREEN(true, Material.STAINED_GLASS_PANE, 13),
    @SuppressWarnings("unused")
    RED(true, Material.STAINED_GLASS_PANE, 14),
    @SuppressWarnings("unused")
    BLACK(true, Material.STAINED_GLASS_PANE, 15),
    @SuppressWarnings("unused")
    IRON(false, Material.IRON_FENCE),
    @SuppressWarnings("unused")
    CLEAR(false, Material.THIN_GLASS);

    private static Set<Material> COLOURABLE = EnumSet.of(Material.STAINED_CLAY,
            Material.STAINED_GLASS, Material.STAINED_GLASS_PANE,
            Material.WOOL,
            Material.INK_SACK,
            Material.CARPET);

    private boolean isDyeColor;
    private Material material;
    private short dataValue;

    /**
     * @param isDyeColor Whether this is a dye color
     * @param material A base material that can be coloured with this
     * @param dataValue The data value. Has the range of a short. This
     * constructor is only for convenience.
     */
    DisplayColor(boolean isDyeColor, Material material, int dataValue) {
        this.isDyeColor = isDyeColor;
        this.material = material;
        this.dataValue = (short) dataValue;
    }

    /**
     * @param isDyeColor Whether this is a dye color
     * @param material A base material that can be coloured with this
     *
     * @see #DisplayColor(boolean, Material, int)
     */
    DisplayColor(boolean isDyeColor, Material material) {
        this(isDyeColor, material, 0);
    }

    /**
     * @return whether this is a dye color
     */
    public boolean isDyeColor() {
        return isDyeColor;
    }

    /**
     * @return the material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * @return the data value
     */
    public short getDataValue() {
        return dataValue;
    }

    /**
     * Returns an ItemFactory with this colour
     *
     * @return An ItemFactory that produces an item with the display color
     */
    public ItemFactory getBaseItemFactory() {
        ItemFactory builder = ItemFactory.builder(getMaterial());
        if (isDyeColor()) {
            builder.setDurability(getDataValue());
        }
        return builder;
    }

    /**
     * Creates a coloured Item factory
     *
     * @param type The type of the item you want coloured
     *
     * @return The {@link ItemFactory}. If {@link #isDyeColor()} is false, it
     * will return the {@link #getBaseItemFactory()}
     *
     * @throws IllegalArgumentException If type is not colourable and
     *                                  {@link #isDyeColor()} returns true
     */
    @SuppressWarnings("unused")
    public ItemFactory getItemFactory(Material type) {
        if (!isDyeColor()) {
            return getBaseItemFactory();
        }
        if (!COLOURABLE.contains(type)) {
            throw new IllegalArgumentException("Item not colourable: " + type);
        }
        return ItemFactory.builder(type).setDurability(getDataValue());
    }

}
