/**
 * 
 */
package com.perceivedev.perceivecore.guireal.components.implementation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.perceivedev.perceivecore.guireal.ClickEvent;
import com.perceivedev.perceivecore.guireal.DisplayColor;
import com.perceivedev.perceivecore.guireal.DisplayType;
import com.perceivedev.perceivecore.guireal.components.AbstractComponent;
import com.perceivedev.perceivecore.guireal.components.Component;
import com.perceivedev.perceivecore.guireal.util.Dimension;
import com.perceivedev.perceivecore.util.ItemFactory;
import com.perceivedev.perceivecore.util.ListUtils;
import com.perceivedev.perceivecore.util.TextUtils;

/**
 * @author Rayzr
 *
 */
public class SimpleLabel extends AbstractComponent {

    protected DisplayType  displayType;
    protected DisplayColor color;

    protected String       name = "Label";
    protected List<String> lore = Collections.emptyList();

    public SimpleLabel(Dimension size, DisplayType type, DisplayColor color, String name, String... lore) {
        super(size);
        setDisplayType(type);
        setColor(color);
        setName(name);
        setLore(lore);
    }

    public SimpleLabel(Dimension size, DisplayType type, String name, String... lore) {
        this(size, type, DisplayColor.WHITE, name, lore);
    }
    
    public SimpleLabel(Dimension size, DisplayColor color, String name, String... lore) {
        this(size, DisplayType.FLAT, color, name, lore);
    }

    public SimpleLabel(String name, DisplayType type, String... lore) {
        this(Dimension.ONE, type, name, lore);
    }
    
    public SimpleLabel(String name, DisplayColor color, String... lore) {
        this(Dimension.ONE, color, name, lore);
    }
    
    public SimpleLabel(String name, String... lore) {
        this(Dimension.ONE, DisplayColor.WHITE, name, lore);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     * @return this label (useful for chaining method calls)
     */
    public void setName(String name) {
        this.name = TextUtils.colorize(name);
    }

    /**
     * @return the lore
     */
    public List<String> getLore() {
        return lore;
    }

    /**
     * @param lore the lore to set
     * @return this label (useful for chaining method calls)
     */
    public void setLore(List<String> lore) {
        this.lore = ListUtils.colorList(lore);
    }

    /**
     * @param lore the lore to set
     * @return this label (useful for chaining method calls)
     */
    public void setLore(String... lore) {
        setLore(Arrays.asList(lore));
    }

    /**
     * @return the displayType
     */
    public DisplayType getDisplayType() {
        return displayType;
    }

    /**
     * Sets the {@link DisplayType} of this component
     * 
     * @param displayType the displayType to set
     */
    public void setDisplayType(DisplayType displayType) {
        this.displayType = displayType;
    }

    /**
     * Sets the {@link DisplayColor} of this component
     * 
     * @return the color
     */
    public DisplayColor getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(DisplayColor color) {
        this.color = color;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.perceivedev.perceivecore.guireal.components.Component#render(org.
     * bukkit.inventory.Inventory, org.bukkit.entity.Player, int, int)
     */
    @Override
    public void render(Inventory inventory, Player player, int offsetX, int offsetY) {
        iterateOver2DRange(0, getSize().getWidth(), 0, getSize().getHeight(), (x, y) -> {
            int slot = gridToSlot(x + offsetX, y + offsetY);
            if (slot < 0 || slot >= inventory.getSize()) {
                // can't happen *normally*
                System.err.println("Button: An item was placed outside the inventory size. Size: " + inventory.getSize()
                        + " Slot: " + slot);
            } else {
                inventory.setItem(slot,
                        ItemFactory.builder(displayType.getItem(color)).setName(name).setLore(lore).build());
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.perceivedev.perceivecore.guireal.components.Component#onClick(com.
     * perceivedev.perceivecore.guireal.ClickEvent)
     */
    @Override
    public void onClick(ClickEvent clickEvent) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.perceivedev.perceivecore.guireal.components.Component#deepClone()
     */
    @Override
    public Component deepClone() {
        return new SimpleLabel(getSize(), displayType, name, lore.toArray(new String[0]));
    }

}
