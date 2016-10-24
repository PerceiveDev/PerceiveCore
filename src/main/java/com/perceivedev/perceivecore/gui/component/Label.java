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
import com.perceivedev.perceivecore.util.ItemFactory;
import com.perceivedev.perceivecore.util.ListUtils;
import com.perceivedev.perceivecore.util.TextUtils;

/**
 * @author Rayzr
 *
 */
public class Label extends Component {

    protected String       name = "Label";
    protected List<String> lore = Collections.emptyList();

    public Label(String name, String... lore) {
        super(DisplayColor.WHITE);
        setName(name);
        setLore(lore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.rayzr522.anvilinput.gui.component.Component#render(com.rayzr522.
     * anvilinput.gui.GUIHolder, int, int)
     */
    @Override
    protected ItemStack render(GUIHolder holder, int posX, int posY) {
        ItemFactory factory = ItemFactory.builder(super.render(holder, posX, posY));
        if (name != null) {
            factory.setName(name);
        }
        if (lore != null && lore.size() > 0) {
            factory.setLore(lore);
        }
        return factory.build();
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
    public Label setName(String name) {
        this.name = TextUtils.colorize(name);
        return this;
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
    public Label setLore(List<String> lore) {
        this.lore = ListUtils.colorList(lore);
        return this;
    }

    /**
     * @param lore the lore to set
     * @return this label (useful for chaining method calls)
     */
    public Label setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

}
