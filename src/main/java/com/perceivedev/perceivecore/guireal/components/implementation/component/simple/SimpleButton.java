/**
 * 
 */
package com.perceivedev.perceivecore.guireal.components.implementation.component.simple;

import java.util.function.Consumer;

import com.perceivedev.perceivecore.guireal.ClickEvent;
import com.perceivedev.perceivecore.guireal.components.base.component.Component;
import com.perceivedev.perceivecore.guireal.util.Dimension;

/**
 * @author Rayzr
 *
 */
public class SimpleButton extends SimpleLabel {

    /**
     * The code to run when the button is clicked
     */
    protected Consumer<ClickEvent> clickHandler;

    /**
     * Whether or not to close the inventory when the button is clicked
     */
    protected boolean              closeOnClick = false;

    public SimpleButton(Dimension size, DisplayType type, DisplayColor color, String name,
            Consumer<ClickEvent> clickHandler) {
        super(size, type, color, name);
        this.clickHandler = clickHandler;
        setColor(DisplayColor.LIME);
    }

    public SimpleButton(DisplayType type, DisplayColor color, String name, Consumer<ClickEvent> clickHandler) {
        this(Dimension.ONE, type, color, name, clickHandler);
    }

    public SimpleButton(String name) {
        super(name);
    }

    public SimpleButton(Consumer<ClickEvent> clickHandler) {
        this("Button");
        setClickHandler(clickHandler);
    }

    /**
     * @return the clickHandler
     */
    public Consumer<ClickEvent> getClickHandler() {
        return clickHandler;
    }

    /**
     * @param clickHandler the clickHandler to set
     * @return this button (useful for chaining method calls)
     */
    public SimpleButton setClickHandler(Consumer<ClickEvent> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    /**
     * This calls the {@link #clickHandler} if it is present, and if
     * {@link #closeOnClick} is true then it closes the inventory.
     * 
     * @param e the ClickEvent
     * @see Component#onClick(ClickEvent)
     */
    @Override
    public void onClick(ClickEvent e) {
        if (clickHandler != null) {
            clickHandler.accept(e);
        }
        if (closeOnClick) {
            e.getPlayer().closeInventory();
        }
    }

    /**
     * @return Whether or not to {@link #closeOnClick}
     */
    public boolean closeOnClick() {
        return closeOnClick;
    }

    /**
     * @param set if it this button should {@link #closeOnClick}
     * @return this button (useful for chaining method calls)
     */
    public SimpleButton setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
        return this;
    }

}
