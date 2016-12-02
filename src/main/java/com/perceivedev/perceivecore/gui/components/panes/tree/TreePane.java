package com.perceivedev.perceivecore.gui.components.panes.tree;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.perceivedev.perceivecore.gui.ClickEvent;
import com.perceivedev.perceivecore.gui.base.AbstractPane;
import com.perceivedev.perceivecore.gui.base.Pane;

/**
 * A Pane that structures it viewers by a tree
 */
public class TreePane extends AbstractPane {

    private TreePaneNode root;
    private TreePaneNode selected;
    private Pane         currentPane;

    /**
     * Creates a pane with the given components
     * <p>
     * Automatically calls addComponent for each
     *
     * @param width The width of this pane
     * @param height The height of this pane
     * @param inventoryMap The {@link InventoryMap} to use
     *
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if the size of the InventoryMap is
     *             different than the size of this Pane
     */
    public TreePane(int width, int height, InventoryMap inventoryMap) {
        super(width, height, inventoryMap);
    }

    /**
     * An empty Pane
     *
     * @param width The width of this pane
     * @param height The height of this pane
     */
    public TreePane(int width, int height) {
        super(width, height);
    }

    /**
     * Changes the root node
     * 
     * @param root The new root
     */
    public void setRoot(TreePaneNode root) {
        this.root = root;
        root.setOwner(this);

        select(root);
    }

    /**
     * @return The current root node
     */
    public TreePaneNode getRoot() {
        return root;
    }

    /**
     * @return The currently selected {@link TreePaneNode}
     */
    public Optional<TreePaneNode> getSelected() {
        return Optional.ofNullable(selected);
    }

    /**
     * Selects and renders a given node
     * 
     * @param node The node to render
     */
    public void select(TreePaneNode node) {
        // if (!node.equals(root) && !root.hasChild(node)) {
        // throw new IllegalArgumentException("The node is not a child of the
        // root node!");
        // }
        selected = node;

        if (getGui() != null) {
            requestReRender();
        }
    }

    @Override
    public void render(Inventory inventory, Player player, int x, int y) {
        if (selected == null) {
            return;
        }
        currentPane = selected.getPane();
        updateComponentHierarchy(currentPane);

        currentPane.render(inventory, player, x, y);
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        if (selected == null && currentPane != null) {
            return;
        }

        clickEvent.setLastPane(this);

        currentPane.onClick(clickEvent);
    }
}
