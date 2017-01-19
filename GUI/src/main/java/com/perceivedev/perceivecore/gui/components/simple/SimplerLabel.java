package com.perceivedev.perceivecore.gui.components.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.components.Label;
import com.perceivedev.perceivecore.gui.util.Dimension;
import com.perceivedev.perceivecore.utilities.item.DisplayColor;
import com.perceivedev.perceivecore.utilities.item.ItemFactory;

/**
 * A Label, just simpler
 */
public class SimplerLabel extends Label {

    private DisplayType type;
    private DisplayColor color;
    private String text;
    private List<String> lore;

    /**
     * Constructs a Label
     *
     * @param text The text of the Label
     * @param type The {@link DisplayType}
     * @param color The {@link DisplayColor}
     * @param size The size of this component
     * @param lore The lore
     *
     * @throws NullPointerException if any parameter is null
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public SimplerLabel(String text, DisplayType type, DisplayColor color, Dimension size, List<String> lore) {
        super(generateItemStack(type, color, text), size);

        this.type = type;
        this.color = color;
        this.text = text;
        this.lore = new ArrayList<>(lore);
    }

    /**
     * Sets the text for this label
     *
     * @param newText The new text for this label
     */
    @SuppressWarnings("unused")
    public void setText(String newText) {
        Objects.requireNonNull(newText, "newText can not be null!");

        text = newText;
        regenerateItem();
    }

    /**
     * Sets the {@link DisplayColor}
     *
     * @param color The new {@link DisplayColor}
     */
    @SuppressWarnings("unused")
    public void setColor(DisplayColor color) {
        this.color = color;
        regenerateItem();
    }

    /**
     * Sets the {@link DisplayType}
     *
     * @param type The new {@link DisplayType}
     */
    @SuppressWarnings("unused")
    public void setType(DisplayType type) {
        this.type = type;
        regenerateItem();
    }

    /**
     * Sets the new lore
     *
     * @param lore The new lore
     */
    @SuppressWarnings("unused")
    public void setLore(String... lore) {
        setLore(Arrays.asList(lore));
    }

    /**
     * Sets the new lore
     *
     * @param lore The new lore
     */
    @SuppressWarnings("WeakerAccess")
    public void setLore(List<String> lore) {
        this.lore = new ArrayList<>(lore);

        regenerateItem();
    }

    /**
     * Adds a line to the lore
     *
     * @param line The line to add
     */
    @SuppressWarnings("unused")
    public void addToLore(String line) {
        this.lore.add(line);

        regenerateItem();
    }

    /**
     * Regenerates the item for this label
     */
    private void regenerateItem() {
        ItemFactory itemFactory = type.getColouredItem(color).setName(text).setLore(lore);
        setItemStack(itemFactory.build());
    }

    /**
     * @param type The {@link DisplayType}
     * @param color The {@link DisplayColor}
     * @param text The text
     *
     * @return The built {@link ItemStack}
     */
    private static ItemStack generateItemStack(DisplayType type, DisplayColor color, String text) {
        return type.getColouredItem(color).setDisplayName(text).build();
    }

    /**
     * @return A Builder for the label
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A Builder for the {@link SimplerLabel}
     */
    public static class Builder {
        DisplayType type = StandardDisplayTypes.FLAT;
        DisplayColor color = DisplayColor.BLACK;
        String text = "";
        List<String> lore = new ArrayList<>();
        protected Dimension size = Dimension.ONE;

        /**
         * Constructs a new Builder using the defaults
         */
        Builder() {
        }

        /**
         * Sets the {@link DisplayType}
         * <p>
         * Default is {@link StandardDisplayTypes#FLAT}
         *
         * @param type The {@link DisplayType}
         *
         * @return This builder
         */
        public Builder setType(DisplayType type) {
            Objects.requireNonNull(type, "type can not be null!");

            this.type = type;
            return this;
        }

        /**
         * Sets the color
         * <p>
         * Default is {@link DisplayColor#BLACK}
         *
         * @param color The new {@link DisplayColor}
         *
         * @return This builder
         */
        @SuppressWarnings("unused")
        public Builder setColor(DisplayColor color) {
            Objects.requireNonNull(color, "color can not be null!");

            this.color = color;
            return this;
        }

        /**
         * Sets the text of the label
         * <p>
         * Default is {@code " "} (a space)
         *
         * @param text The text of the label
         *
         * @return This builder
         */
        @SuppressWarnings("WeakerAccess")
        public Builder setText(String text) {
            Objects.requireNonNull(text, "text can not be null!");

            this.text = text;
            return this;
        }

        /**
         * Sets the size of this Label.
         * <p>
         * Default is {@link Dimension#ONE}
         *
         * @param size The size of the label
         *
         * @return This builder
         */
        public Builder setSize(Dimension size) {
            Objects.requireNonNull(size, "size can not be null!");

            this.size = size;
            return this;
        }

        /**
         * Sets the size of this Label.
         * <p>
         * Default is {@link Dimension#ONE}
         *
         * @param width The width of the label
         * @param height The height of the label
         *
         * @return This builder
         */
        public Builder setSize(int width, int height) {
            return setSize(new Dimension(width, height));
        }

        /**
         * Sets the lore
         *
         * @param lore The new lore
         *
         * @return This Builder
         */
        @SuppressWarnings({"unused", "WeakerAccess"})
        public Builder setLore(List<String> lore) {
            Objects.requireNonNull(lore, "lore can not be null!");

            this.lore = new ArrayList<>(lore);

            return this;
        }

        /**
         * Sets the lore
         *
         * @param lore The new lore
         *
         * @return This Builder
         */
        @SuppressWarnings("unused")
        public Builder setLore(String... lore) {
            return setLore(Arrays.asList(lore));
        }

        /**
         * Adds a line to the lore
         *
         * @param line The line to add
         *
         * @return This builder
         */
        @SuppressWarnings("unused")
        public Builder addLore(String line) {
            Objects.requireNonNull(line, "line can not be null!");

            lore.add(line);

            return this;
        }

        /**
         * Constructs the {@link SimplerLabel}
         *
         * @return The resulting SimplerLabel
         */
        @SuppressWarnings("unused")
        public SimplerLabel build() {
            return new SimplerLabel(text, type, color, size, lore);
        }
    }
}
