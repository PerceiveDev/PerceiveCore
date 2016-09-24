package com.perceivedev.perceivecore.util;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemFactory {
    private ItemStack itemStack;

    public ItemFactory builder(Material type) {
        itemStack = new ItemStack(type);
        return this;
    }

    public ItemFactory builder(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public ItemFactory setType(Material type) {
        itemStack = new ItemStack(type);
        return this;
    }

    public ItemFactory setSize(int size) {
        itemStack.setAmount(size);
        return this;
    }

    public ItemFactory setAmount(int amount) {
        return setSize(amount);
    }

    public ItemFactory setDisplayName(String displayName) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(TextUtils.colorize(displayName));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemFactory setName(String name) {
        return setDisplayName(TextUtils.colorize(name));
    }

    public ItemFactory setLore(List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemFactory setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemFactory addLore(String line) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(TextUtils.colorize(line));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemFactory setDurability(short durability) {
        itemStack.setDurability(durability);
        return this;
    }

    public ItemFactory setAuthor(String author) {
        if (itemStack.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            bookMeta.setAuthor(TextUtils.colorize(author));
            itemStack.setItemMeta(bookMeta);
        } else {
            Logger.getLogger("minecraft").warning("ItemStack is not a WRITTEN_BOOK!");
        }
        return this;
    }

    public ItemFactory setSkullOwner(String name) {
        if (itemStack.getType() == Material.SKULL_ITEM) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwner(name);
            itemStack.setItemMeta(skullMeta);
        } else {
            Logger.getLogger("minecraft").warning("ItemStack is not a SKULL_ITEM!");
        }
        return this;
    }

    public ItemFactory setSkullOwner(OfflinePlayer player) {
        return setSkullOwner(player.getName());
    }

    public ItemStack build() {
        return itemStack;
    }

}
