package com.perceivedev.bukkitpluginutilities.gui.components.simple;

import java.util.List;
import java.util.function.Consumer;

import com.perceivedev.bukkitpluginutilities.gui.ClickEvent;
import com.perceivedev.bukkitpluginutilities.gui.util.Dimension;
import com.perceivedev.bukkitpluginutilities.utilities.item.DisplayColor;

/**
 * A Simpler button
 */
public class SimplerButton extends SimplerLabel {

    private Consumer<ClickEvent> action;

    /**
     * Constructs a Label
     *
     * @param text The text of the Label
     * @param type The {@link DisplayType}
     * @param color The {@link DisplayColor}
     * @param size The size of this component
     * @param lore The lore
     * @param action The action when the button is clicked
     *
     * @throws NullPointerException if any parameter is null
     */
    @SuppressWarnings("WeakerAccess")
    public SimplerButton(String text, DisplayType type, DisplayColor color, Dimension size, List<String> lore,
                         Consumer<ClickEvent> action) {
        super(text, type, color, size, lore);

        this.action = action;
    }

    /**
     * Sets the action of this button
     * <p>
     * The {@link ClickEvent} is cancelled by default
     *
     * @param action The action when the button is clicked
     */
    @SuppressWarnings("unused")
    public void setAction(Consumer<ClickEvent> action) {
        this.action = action;
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        clickEvent.setCancelled(true);
        action.accept(clickEvent);
    }

    /**
     * @return Returns a Builder for this {@link SimplerButton}
     */
    @SuppressWarnings("unused")
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A Builder for a {@link SimplerButton}
     */
    public static class Builder extends SimplerLabel.Builder {
        private Consumer<ClickEvent> action = event -> {
        };

        private Builder() {

        }

        @Override
        public Builder setColor(DisplayColor color) {
            return (Builder) super.setColor(color);
        }

        @Override
        public Builder setLore(List<String> lore) {
            return (Builder) super.setLore(lore);
        }

        @Override
        public Builder setLore(String... lore) {
            return (Builder) super.setLore(lore);
        }

        @Override
        public Builder setSize(Dimension size) {
            return (Builder) super.setSize(size);
        }

        @Override
        public Builder setText(String text) {
            return (Builder) super.setText(text);
        }

        @Override
        public Builder setType(DisplayType type) {
            return (Builder) super.setType(type);
        }

        @Override
        public Builder setSize(int width, int height) {
            return (Builder) super.setSize(width, height);
        }

        /**
         * Sets the action of the button
         *
         * @param action The action when the button is clicked
         */
        @SuppressWarnings({"unused", "WeakerAccess"})
        public void setAction(Consumer<ClickEvent> action) {
            this.action = action;
        }

        @Override
        public SimplerButton build() {
            return new SimplerButton(text, type, color, size, lore, action);
        }
    }
}
