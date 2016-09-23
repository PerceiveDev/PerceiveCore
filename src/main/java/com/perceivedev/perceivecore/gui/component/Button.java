/**
 * 
 */
package com.perceivedev.perceivecore.gui.component;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

import com.perceivedev.perceivecore.gui.DisplayColor;

/**
 * @author Rayzr
 *
 */
public class Button extends Label {

    protected Consumer<ClickEvent> clickHandler;
    protected boolean              closeOnClick = true;

    public Button(int x, int y, int width, int height, String name, Consumer<ClickEvent> clickHandler) {
        super(x, y, width, height, name, new String[0]);
        this.clickHandler = clickHandler;
        setColor(DisplayColor.LIME);
    }

    public Button(int x, int y, String name, Consumer<ClickEvent> clickHandler) {
        this(x, y, 1, 1, name, clickHandler);
    }

    public Button(int x, int y, String name) {
        this(x, y, name, null);
    }

    public Button(String name) {
        this(0, 0, name);
    }

    public Button(int x, int y, Consumer<ClickEvent> clickHandler) {
        this(x, y, "Button", clickHandler);
    }

    public Button(Consumer<ClickEvent> clickHandler) {
        this(0, 0, clickHandler);
    }

    public Button(String name, Consumer<ClickEvent> clickHandler) {
        this(0, 0, "Button", clickHandler);
    }

    /**
     * @return the clickHandler
     */
    public Consumer<ClickEvent> getClickHandler() {
        return clickHandler;
    }

    /**
     * @param clickHandler the clickHandler to set
     */
    public void setClickHandler(Consumer<ClickEvent> clickHandler) {
        this.clickHandler = clickHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.rayzr522.anvilinput.gui.component.Component#onClick(org.bukkit.entity
     * .Player, int, int)
     */
    @Override
    protected boolean onClick(Player player, int offX, int offY) {
        if (clickHandler != null) {
            clickHandler.accept(new ClickEvent(player, offX, offY));
        }
        if (closeOnClick) {
            player.closeInventory();
        }
        return false;
    }

    /**
     * @return the value ofcloseOnClick
     */
    public boolean closeOnClick() {
        return closeOnClick;
    }

    /**
     * @param closeOnClick the value of closeOnClick to set
     */
    public void setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
    }

    public class ClickEvent {

        private Player player;
        private int    offX;
        private int    offY;

        public ClickEvent(Player player, int offX, int offY) {
            this.player = player;
            this.offX = offX;
            this.offY = offY;
        }

        /**
         * @return the player
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * @return the offX
         */
        public int getOffX() {
            return offX;
        }

        /**
         * @return the offY
         */
        public int getOffY() {
            return offY;
        }

    }

}
