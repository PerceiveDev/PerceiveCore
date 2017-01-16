package me.ialistannen.bukkitpluginutilities.gui.components.panes.tree;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.ialistannen.bukkitpluginutilities.gui.ClickEvent;
import me.ialistannen.bukkitpluginutilities.gui.base.AbstractPane;
import me.ialistannen.bukkitpluginutilities.gui.base.Pane;

/**
 * A Pane that structures it viewers by a tree
 */
public class TreePane extends AbstractPane {

    private TreePaneNode root;
    private TreePaneNode selected;
    private Pane currentPane;
    private boolean dirty;

    /**
     * Creates a pane with the given components
     * <p>
     * Automatically calls addComponent for each
     *
     * @param width The width of this pane
     * @param height The height of this pane
     * @param inventoryMap The {@link InventoryMap} to use
     *
     * @throws NullPointerException     if any parameter is null
     * @throws IllegalArgumentException if the size of the InventoryMap is
     *                                  different than the size of this Pane
     */
    @SuppressWarnings("unused")
    public TreePane(int width, int height, InventoryMap inventoryMap) {
        super(width, height, inventoryMap);
    }

    /**
     * An empty Pane
     *
     * @param width The width of this pane
     * @param height The height of this pane
     */
    @SuppressWarnings("unused")
    public TreePane(int width, int height) {
        super(width, height);
    }

    /**
     * Changes the root node
     *
     * @param root The new root
     */
    @SuppressWarnings("unused")
    public void setRoot(TreePaneNode root) {
        this.root = root;
        root.setOwner(this);

        select(root);
    }

    /**
     * @return The current root node
     */
    @SuppressWarnings("unused")
    public TreePaneNode getRoot() {
        return root;
    }

    /**
     * @return The currently selected {@link TreePaneNode}
     */
    @SuppressWarnings("unused")
    public Optional<TreePaneNode> getSelected() {
        return Optional.ofNullable(selected);
    }

    /**
     * Selects and renders a given node
     *
     * @param node The node to render
     */
    @SuppressWarnings("WeakerAccess")
    public void select(TreePaneNode node) {
        if (selected != null && selected.equals(node)) {
            return;
        }
        selected = node;
        dirty = true;

        if (getGui() != null) {
            requestReRender();
        }
    }

    @Override
    public void render(Inventory inventory, Player player, int x, int y) {
        if (selected == null) {
            return;
        }
        if (dirty) {
            currentPane = selected.getPane();
            updateComponentHierarchy(currentPane);
        }

        currentPane.render(inventory, player, x, y);
        dirty = false;
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        if (selected == null && currentPane != null) {
            return;
        }

        clickEvent.setLastPane(this);

        currentPane.onClick(clickEvent);
    }

    @Override
    public TreePane deepClone() {
        TreePane clone = (TreePane) super.deepClone();
        clone.currentPane = currentPane.deepClone();
        clone.root = root.clone();
        clone.selected = selected.clone();
        clone.ownerGui = ownerGui;

        return clone;
    }
}
