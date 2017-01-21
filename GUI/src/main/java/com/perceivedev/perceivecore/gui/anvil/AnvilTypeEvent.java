package com.perceivedev.perceivecore.gui.anvil;

/**
 * An event that is called when the player types anything in the Anvil gui
 */
public class AnvilTypeEvent {

    private AnvilInputHolder gui;
    private String newText;

    /**
     * @param gui The Gui it occurred in
     * @param newText The new text
     */
    @SuppressWarnings("WeakerAccess")
    public AnvilTypeEvent(AnvilInputHolder gui, String newText) {
        this.gui = gui;
        this.newText = newText;
    }

    /**
     * @return The {@link AnvilInputHolder} it occurred in. Currently {@link AnvilGui}, but you may change that :)
     */
    public AnvilInputHolder getGui() {
        return gui;
    }

    /**
     * @return The text newText the player typed something
     */
    @SuppressWarnings("unused")
    public String getNewText() {
        return newText;
    }
}
