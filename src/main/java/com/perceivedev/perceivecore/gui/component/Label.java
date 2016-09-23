/**
 * 
 */
package com.perceivedev.perceivecore.gui.component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.DisplayColor;
import com.perceivedev.perceivecore.gui.GUIHolder;
import com.perceivedev.perceivecore.util.ItemUtils;
import com.perceivedev.perceivecore.util.ListUtils;
import com.perceivedev.perceivecore.util.TextUtils;

/**
 * @author Rayzr
 *
 */
public class Label extends Component {

    protected String	   name	= "Label";
    protected List<String> lore	= Collections.emptyList();

    public Label(int x, int y, int width, int height, String name, String... lore) {
	super(x, y, width, height);
	setName(name);
	setLore(lore);
	setColor(DisplayColor.WHITE);
    }

    public Label(int x, int y, String name, String... lore) {
	this(x, y, 1, 1, name, lore);
    }

    public Label(String name, String... lore) {
	this(0, 0, name, lore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.rayzr522.anvilinput.gui.component.Component#render(com.rayzr522.
     * anvilinput.gui.GUIHolder, int, int)
     */
    @Override
    protected ItemStack render(GUIHolder holder, int posX, int posY) {
	return ItemUtils.setDisplay(super.render(holder, posX, posY), name, lore);
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the name to set
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
     * @param lore
     *            the lore to set
     */
    public void setLore(List<String> lore) {
	this.lore = ListUtils.colorList(lore);
    }

    /**
     * @param lore
     *            the lore to set
     */
    public void setLore(String... lore) {
	setLore(Arrays.asList(lore));
    }

}
