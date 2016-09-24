package com.perceivedev.perceivecore.util;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Represents an ItemStack with utility methods to modify it's appearance.
 *
 * @author ZP4RKER
 */
public class ItemFactory {
    private ItemStack itemStack;
    private static final Set<Material> COLOURABLE = EnumSet.of(Material.WOOL, Material.STAINED_CLAY,
              Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET);
    private static final Set<Material> LARMOUR = EnumSet.of(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
              Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);

    private ItemFactory(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    private ItemFactory(Material material) {
        itemStack = new ItemStack(material);
    }

    /**
     * Creates a new ItemFactory builder with the given material as a base.
     *
     * @param type the type (material) of item to create
     *
     * @return The new ItemFactory instance
     */
    public static ItemFactory builder(Material type) {
        return new ItemFactory(type);
    }

    /**
     * Creates a new ItemFactory builder with the given item as a base.
     *
     * @param itemStack the base {@link ItemStack}
     *
     * @return The new ItemFactory instance
     */
    public static ItemFactory builder(ItemStack itemStack) {
        return new ItemFactory(itemStack);
    }

    /**
     * Sets the type (material) of the item.
     *
     * @param type the type to set
     *
     * @return This ItemFactory instance
     */
    public ItemFactory setType(Material type) {
        itemStack = new ItemStack(type);
        return this;
    }

    /**
     * Sets the size of the item (amount).
     *
     * @param size the size to set
     *
     * @return This ItemFactory instance
     */
    public ItemFactory setSize(int size) {
        itemStack.setAmount(size);
        return this;
    }

    /**
     * Sets the amount of the item.
     *
     * @param amount the amount to set
     *
     * @return This ItemFactory instance
     */
    public ItemFactory setAmount(int amount) {
        return setSize(amount);
    }

    /**
     * Sets the name of the item.
     *
     * @param displayName the name to set
     *
     * @return This ItemFactory instance
     */
    public ItemFactory setDisplayName(String displayName) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(TextUtils.colorize(displayName));
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the name of the item.
     *
     * @param name the name to set
     *
     * @return This ItemFactory instance
     */
    public ItemFactory setName(String name) {
        return setDisplayName(TextUtils.colorize(name));
    }

    /**
     * Sets the lore of the item.
     *
     * @param lore the lore to set
     *
     * @return This ItemFactory instance
     */
    public ItemFactory setLore(List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(ListUtils.colorList(lore));
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the lore if the item.
     *
     * @param lore the lore to set
     *
     * @return This ItemFactory instance
     */
    public ItemFactory setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    /**
     * Adds a line of lore to the item.
     *
     * @param line the line to add
     *
     * @return This ItemFactory instance
     */
    public ItemFactory addLore(String line) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(TextUtils.colorize(line));
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Adds multiple lines to the lore of the item.
     *
     * @param lines The lines of lore to add
     * @return This ItemFactory instance
     */
    public ItemFactory addLore(String... lines) {
        for (String line : lines) {
            addLore(line);
        }
        return this;
    }

    /**
     * Sets the durability of an item.
     *
     * @param durability the durability to set
     *
     * @return This ItemFactory instance
     */
    public ItemFactory setDurability(short durability) {
        itemStack.setDurability(durability);
        return this;
    }

    /**
     * Sets the author of a written book ({@link Material#WRITTEN_BOOK}).
     *
     * @param author the author to set
     *
     * @return This ItemFactory instance
     */
    public ItemFactory setAuthor(String author) {
        if (itemStack.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            bookMeta.setAuthor(TextUtils.colorize(author));
            itemStack.setItemMeta(bookMeta);
        } else {
            System.err.println("ItemStack is not a WRITTEN_BOOK!");
        }
        return this;
    }

    /**
     * Sets the owner of a player head.
     *
     * @param name the name of the player
     *
     * @return This ItemFactory instance
     */
    public ItemFactory setSkullOwner(String name) {
        if (itemStack.getType() == Material.SKULL_ITEM) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwner(name);
            itemStack.setItemMeta(skullMeta);
        } else {
            System.err.println("ItemStack is not a SKULL_ITEM!");
        }
        return this;
    }

    /**
     * @see #setSkullOwner(String)
     */
    public ItemFactory setSkullOwner(OfflinePlayer player) {
        return setSkullOwner(player.getName());
    }

    /**
     * Adds an enchantment to the itemstack.
     *
     * @param enchantment The enchantment to be addded
     * @param level The level of the enchantment
     *
     * @return This ItemFactory instance
     */
    public ItemFactory addEnchantment(Enchantment enchantment, int level) {
        if (level <= enchantment.getMaxLevel() && enchantment.canEnchantItem(itemStack)) {
            itemStack.addEnchantment(enchantment, level);
        } else {
            itemStack.addUnsafeEnchantment(enchantment, level);
        }
        return this;
    }

    /**
     * Sets the colour of the item, if it is a colourable item/block.
     *
     * @param colour The color to set the itemstack as
     * @return This ItemFactory instance
     */
    public ItemFactory setColour(DyeColor colour) {
        if (COLOURABLE.contains(itemStack.getType())) {
            itemStack.setDurability(colour.getData());
        } else {
            System.err.println("Itemstack is not colourable!");
        }
        return this;
    }

    /**
     * Sets the colour of a piece of leather armour.
     *
     * @param colour The color to set the armour piece as
     * @return This ItemFactory instance
     */
    public ItemFactory setArmourColour(Color colour) {
        if (LARMOUR.contains(itemStack.getType())) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(colour);
            itemStack.setItemMeta(meta);
        } else {
            System.err.println("Itemstack is not a type of leather armour!");
        }
        return this;
    }

    /**
     * @return The finished ItemStack.
     */
    public ItemStack build() {
        return itemStack;
    }

}
