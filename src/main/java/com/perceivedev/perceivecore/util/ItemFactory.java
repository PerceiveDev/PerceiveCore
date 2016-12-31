package com.perceivedev.perceivecore.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;

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

import com.perceivedev.perceivecore.util.collections.ListUtils;
import com.perceivedev.perceivecore.util.text.TextUtils;

/**
 * Represents an ItemStack with utility methods to modify it's appearance.
 *
 * @author ZP4RKER
 */
@SuppressWarnings("WeakerAccess")
public class ItemFactory implements Cloneable {

    private static final Set<Material> COLOURABLE = EnumSet.of(Material.WOOL, Material.STAINED_CLAY,
            Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET, Material.INK_SACK);
    private static final Set<Material> LEATHER_ARMOUR = EnumSet.of(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);

    private ItemStack itemStack;

    private ItemFactory(ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "itemStack can not be null");

        this.itemStack = itemStack.clone();
    }

    // <editor-fold desc="General Methods">
    // === GENERAL METHODS ===

    /**
     * Sets the type (material) of the item.
     *
     * @param type the type to set
     *
     * @return This ItemFactory instance
     */
    @Nonnull
    public ItemFactory setType(@Nonnull Material type) {
        Objects.requireNonNull(type, "type can not be null");

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
    @Nonnull
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
    @Nonnull
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
    @Nonnull
    public ItemFactory setDisplayName(@Nonnull String displayName) {
        Objects.requireNonNull(displayName, "displayName can not be null");

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
    @Nonnull
    public ItemFactory setName(@Nonnull String name) {
        Objects.requireNonNull(name, "name can not be null");

        return setDisplayName(TextUtils.colorize(name));
    }

    /**
     * Sets the lore of the item.
     *
     * @param lore the lore to set
     *
     * @return This ItemFactory instance
     */
    @Nonnull
    public ItemFactory setLore(@Nonnull List<String> lore) {
        Objects.requireNonNull(lore, "lore can not be null");

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
    @Nonnull
    public ItemFactory setLore(@Nonnull String... lore) {
        Objects.requireNonNull(lore, "lore can not be null");

        return setLore(Arrays.asList(lore));
    }

    /**
     * Adds a line of lore to the item.
     *
     * @param line the line to add
     *
     * @return This ItemFactory instance
     */
    @Nonnull
    public ItemFactory addLore(@Nonnull String line) {
        Objects.requireNonNull(line, "line can not be null");

        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        lore.add(TextUtils.colorize(line));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Adds multiple lines to the lore of the item.
     *
     * @param lines The lines of lore to add
     *
     * @return This ItemFactory instance
     */
    @Nonnull
    public ItemFactory addLore(@Nonnull String... lines) {
        Objects.requireNonNull(lines, "lines can not be null");

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
    @Nonnull
    public ItemFactory setDurability(short durability) {
        itemStack.setDurability(durability);
        return this;
    }

    /**
     * Adds an enchantment to the itemstack.
     *
     * @param enchantment The enchantment to be added
     * @param level The level of the enchantment
     *
     * @return This ItemFactory instance
     */
    @Nonnull
    public ItemFactory addEnchantment(@Nonnull Enchantment enchantment, int level) {
        Objects.requireNonNull(enchantment, "enchantment can not be null");

        if (level <= enchantment.getMaxLevel() && enchantment.canEnchantItem(itemStack)) {
            itemStack.addEnchantment(enchantment, level);
        } else {
            itemStack.addUnsafeEnchantment(enchantment, level);
        }
        return this;
    }

    /**
     * Removes an enchantment from the itemstack.
     *
     * @param enchantment The enchantment to be removed
     *
     * @return This ItemFactory instance
     */
    @Nonnull
    public ItemFactory removeEnchantment(@Nonnull Enchantment enchantment) {
        Objects.requireNonNull(enchantment, "enchantment can not be null");

        if (!itemStack.containsEnchantment(enchantment)) {
            return this;
        }
        itemStack.removeEnchantment(enchantment);
        return this;
    }
    // </editor-fold>

    // <editor-fold desc="Skulls">
    // ==== SKULLS ====

    /**
     * Sets the owner of a player head.
     *
     * @param name The name of the player
     *
     * @return This ItemFactory instance
     *
     * @throws IllegalStateException If the {@link ItemStack} is not a
     *             {@link Material#SKULL_ITEM}
     */
    @Nonnull
    public ItemFactory setSkullOwner(@Nonnull String name) {
        Objects.requireNonNull(name, "name can not be null");

        if (itemStack.getType() == Material.SKULL_ITEM) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwner(name);
            itemStack.setItemMeta(skullMeta);
        } else {
            throw new IllegalStateException("ItemStack is not a SKULL_ITEM!");
        }
        return this;
    }

    /**
     * @param player The player to use for the skull
     * @return This ItemFactory instance
     * @see #setSkullOwner(String)
     */
    @Nonnull
    public ItemFactory setSkullOwner(@Nonnull OfflinePlayer player) {
        Objects.requireNonNull(player, "player can not be null");

        return setSkullOwner(player.getName());
    }

    // ==== COLOURS ====

    /**
     * Sets the colour of the item, if it is a colourable item/block.
     *
     * @param colour The color to set the itemstack as
     *
     * @return This ItemFactory instance
     *
     * @throws IllegalStateException If the {@link ItemStack} is not
     *             colourable
     */
    @Nonnull
    @SuppressWarnings("deprecation")
    public ItemFactory setColour(@Nonnull DyeColor colour) {
        Objects.requireNonNull(colour, "colour can not be null");

        if (COLOURABLE.contains(itemStack.getType())) {
            itemStack.setDurability(colour.getData());
        } else {
            throw new IllegalStateException("Itemstack type is not colourable!");
        }
        return this;
    }

    /**
     * Sets the colour of a piece of leather armour.
     *
     * @param colour The color to set the armour piece as
     *
     * @return This ItemFactory instance
     *
     * @throws IllegalStateException If the {@link ItemStack} is not a type of
     *             leather armour
     */
    @Nonnull
    public ItemFactory setArmourColour(@Nonnull Color colour) {
        Objects.requireNonNull(colour, "colour can not be null");

        if (LEATHER_ARMOUR.contains(itemStack.getType())) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(colour);
            itemStack.setItemMeta(meta);
        } else {
            throw new IllegalStateException("Itemstack is not a type of leather armour!");
        }
        return this;
    }
    // </editor-fold>

    // <editor-fold desc="Books">
    // ==== BOOKS ====

    /**
     * Sets the author of a written book ({@link Material#WRITTEN_BOOK}).
     *
     * @param author the author to set
     *
     * @return This ItemFactory instance
     *
     * @throws IllegalStateException If the {@link ItemStack} is not a
     *             {@link Material#WRITTEN_BOOK}
     */
    @Nonnull
    public ItemFactory setAuthor(@Nonnull String author) {
        Objects.requireNonNull(author, "author can not be null");

        if (itemStack.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            bookMeta.setAuthor(TextUtils.colorize(author));
            itemStack.setItemMeta(bookMeta);
        } else {
            throw new IllegalStateException("ItemStack is not a WRITTEN_BOOK!");
        }
        return this;
    }

    /**
     * Sets the contents of a ({@link Material#WRITTEN_BOOK}).
     *
     * @param pages Lines to set in the book
     *
     * @return This ItemFactory instance
     *
     * @throws IllegalStateException If the {@link ItemStack} is not a
     *             {@link Material#WRITTEN_BOOK}
     */
    @Nonnull
    public ItemFactory setPages(@Nonnull List<String> pages) {
        Objects.requireNonNull(pages, "pages can not be null");

        if (itemStack.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            bookMeta.setPages(pages);
            itemStack.setItemMeta(bookMeta);
        } else {
            throw new IllegalStateException("ItemStack type is not a WRITTEN_BOOK!");
        }
        return this;
    }

    /**
     * Sets the contents of a ({@link Material#WRITTEN_BOOK}).
     *
     * @param pages Lines to set in the book
     *
     * @return This ItemFactory instance
     *
     * @throws IllegalStateException If the {@link ItemStack} is not a
     *             {@link Material#WRITTEN_BOOK}
     * 
     * @see #setPages(List)
     */
    @Nonnull
    public ItemFactory setPages(@Nonnull String... pages) {
        Objects.requireNonNull(pages, "pages can not be null");

        return setPages(Arrays.asList(pages));
    }

    /**
     * Adds a single page to a ({@link Material#WRITTEN_BOOK}).
     *
     * @param page Line to add to book
     *
     * @return This ItemFactory instance
     *
     * @throws IllegalStateException If the {@link ItemStack} is not a
     *             {@link Material#WRITTEN_BOOK}
     */
    @Nonnull
    public ItemFactory addPage(@Nonnull String page) {
        Objects.requireNonNull(page, "page can not be null");

        if (itemStack.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            List<String> pages = bookMeta.getPages();
            pages.add(page);
            bookMeta.setPages(pages);
            itemStack.setItemMeta(bookMeta);
        } else {
            throw new IllegalStateException("ItemStack type is not a WRITTEN_BOOK!");
        }
        return this;
    }

    /**
     * Adds multiple pages to a ({@link Material#WRITTEN_BOOK}).
     *
     * @param pages The pages to add to the book
     *
     * @return This ItemFactory instance
     *
     * @throws IllegalStateException If the {@link ItemStack} is not a
     *             {@link Material#WRITTEN_BOOK}
     */
    @Nonnull
    public ItemFactory addPages(@Nonnull String... pages) {
        Objects.requireNonNull(pages, "pages can not be null");

        if (itemStack.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            List<String> original = bookMeta.getPages();
            original.addAll(Arrays.asList(pages));
            bookMeta.setPages(pages);
            itemStack.setItemMeta(bookMeta);
        } else {
            throw new IllegalStateException("ItemStack type is not a WRITTEN_BOOK!");
        }
        return this;
    }

    /**
     * Sets the title of a ({@link Material#WRITTEN_BOOK}).
     *
     * @param title The title to set to the book
     *
     * @return This ItemFactory instance
     *
     * @throws IllegalStateException If the {@link ItemStack} is not a
     *             {@link Material#WRITTEN_BOOK}
     */
    @Nonnull
    public ItemFactory setTitle(@Nonnull String title) {
        Objects.requireNonNull(title, "title can not be null");

        if (itemStack.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            bookMeta.setTitle(TextUtils.colorize(title));
            itemStack.setItemMeta(bookMeta);
        } else {
            throw new IllegalStateException("ItemStack type is not a WRITTEN_BOOK!");
        }
        return this;
    }
    // </editor-fold>

    // <editor-fold desc="Build and create">
    // === BUILD AND CREATE ====

    /**
     * Clones this factory
     *
     * @return A clones ItemFactory
     */
    @Override
    @Nonnull
    public ItemFactory clone() {
        return new ItemFactory(itemStack.clone());
    }

    /**
     * Creates a new ItemFactory builder with the given item as a base.
     *
     * @param itemStack the base {@link ItemStack}
     *
     * @return The new ItemFactory instance
     */
    @Nonnull
    public static ItemFactory builder(@Nonnull ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "itemStack can not be null");

        return new ItemFactory(itemStack);
    }

    /**
     * Creates a new ItemFactory builder with the given material as a base.
     *
     * @param type the type (material) of item to create
     *
     * @return The new ItemFactory instance
     */
    @Nonnull
    public static ItemFactory builder(@Nonnull Material type) {
        Objects.requireNonNull(type, "type can not be null");

        return new ItemFactory(new ItemStack(type));
    }

    /**
     * @return The finished ItemStack.
     */
    @Nonnull
    public ItemStack build() {
        return itemStack.clone();
    }
    // </editor-fold>

}
