package com.perceivedev.perceivecore.gui.components.implementation.component.simple;

import java.util.function.Consumer;

import com.perceivedev.perceivecore.gui.ClickEvent;
import com.perceivedev.perceivecore.gui.components.base.component.Component;
import com.perceivedev.perceivecore.gui.util.Dimension;

/**
 * @author Rayzr
 */
public class SimpleButton extends SimpleLabel {

    /**
     * The code to run when the button is clicked
     */
    private Consumer<ClickEvent> clickHandler;

    /**
     * Whether or not to close the inventory when the button is clicked
     */
    private boolean              closeOnClick = false;

    public SimpleButton(Dimension size, DisplayType type, DisplayColor color, String name,
            Consumer<ClickEvent> clickHandler) {
        super(size, type, color, name);
        this.clickHandler = clickHandler;
    }

    public SimpleButton(DisplayType type, DisplayColor color, String name, Consumer<ClickEvent> clickHandler) {
        this(Dimension.ONE, type, color, name, clickHandler);
    }

    public SimpleButton(String name) {
        super(name);
    }

    public SimpleButton(String name, Consumer<ClickEvent> clickHandler) {
        this(name);
        setClickHandler(clickHandler);
    }

    public SimpleButton(Consumer<ClickEvent> clickHandler) {
        this("Button", clickHandler);
    }

    /**
     * @return the clickHandler
     */
    public Consumer<ClickEvent> getClickHandler() {
        return clickHandler;
    }

    /**
     * @param clickHandler the clickHandler to set
     *
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
     * @param event the ClickEvent
     *
     * @see Component#onClick(ClickEvent)
     */
    @Override
    public void onClick(ClickEvent event) {
        if (clickHandler != null) {
            clickHandler.accept(event);
        }
        if (closeOnClick) {
            ownerGui.close();
        }
    }

    /**
     * @return Whether or not to {@link #closeOnClick}
     */
    public boolean isCloseOnClick() {
        return closeOnClick;
    }

    /**
     * @param closeOnClick if it this button should {@link #closeOnClick}
     *
     * @return this button (useful for chaining method calls)
     */
    public SimpleButton setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
        return this;
    }

}
