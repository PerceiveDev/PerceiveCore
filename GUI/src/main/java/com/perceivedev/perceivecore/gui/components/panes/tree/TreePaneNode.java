package com.perceivedev.perceivecore.gui.components.panes.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import com.perceivedev.perceivecore.gui.base.Pane;

/**
 * A {@link TreePane} node
 */
public abstract class TreePaneNode implements Cloneable {

    private static int counter;

    /**
     * An unique ID for equals and hashcode
     */
    private int ID = counter++;

    private TreePaneNode parent;
    private List<TreePaneNode> children;
    private TreePane owner;

    /**
     * Creates a new {@link TreePaneNode} with the given parent and children
     *
     * @param parent The parent node
     * @param children The child nodes
     */
    @SuppressWarnings("WeakerAccess")
    public TreePaneNode(TreePaneNode parent, List<TreePaneNode> children) {
        this.parent = parent;
        this.children = new ArrayList<>(children);
    }

    /**
     * Creates a new {@link TreePaneNode} with the given parent and no children
     *
     * @param parent The parent node
     *
     * @see #TreePaneNode(TreePaneNode, List)
     */
    @SuppressWarnings("unused")
    public TreePaneNode(TreePaneNode parent) {
        this(parent, Collections.emptyList());
    }

    /**
     * @return All the children of this node. Unmodifiable
     */
    @SuppressWarnings("WeakerAccess")
    public List<TreePaneNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Adds a child
     *
     * @param child The child to add
     */
    @SuppressWarnings("unused")
    public void addChild(TreePaneNode child) {
        children.add(child);
    }

    /**
     * @return The Parent of this node
     */
    @SuppressWarnings("unused")
    public TreePaneNode getParent() {
        return parent;
    }

    /**
     * Sets the new parent
     *
     * @param parent The new parent
     */
    @SuppressWarnings("unused")
    public void setParent(TreePaneNode parent) {
        this.parent = parent;
    }

    /**
     * Checks if this Node has the given node under him in the tree
     *
     * @param node The child node that is being searched
     *
     * @return True if the node is a child or a child of a child or so on of
     * this node
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public boolean hasChild(TreePaneNode node) {
        if (children.contains(node)) {
            return true;
        }
        for (TreePaneNode child : children) {
            if (hasChild(child)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a TreePaneNode
     *
     * @param predicate The predicate to use
     *
     * @return The found {@link TreePaneNode}, if any
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public Optional<TreePaneNode> find(Predicate<TreePaneNode> predicate) {
        for (TreePaneNode child : children) {
            if (predicate.test(child)) {
                return Optional.ofNullable(child);
            }
        }

        for (TreePaneNode child : children) {
            Optional<TreePaneNode> found = child.find(predicate);

            if (found.isPresent()) {
                return found;
            }
        }

        return Optional.empty();
    }

    /**
     * Returns the owner, if this node has been added to a Pane
     *
     * @return The Owner pane, if any
     */
    @SuppressWarnings("unused")
    public Optional<TreePane> getOwner() {
        return Optional.ofNullable(owner);
    }

    /**
     * Sets the owning {@link TreePane} for this node and all child nodes
     *
     * @param owner The owning {@link TreePane}
     */
    @SuppressWarnings("WeakerAccess")
    public void setOwner(TreePane owner) {
        setOwnerRecursive(owner);
    }

    /**
     * Sets the owning {@link TreePane} for this node and all child nodes
     *
     * @param owner The owning {@link TreePane}
     */
    private void setOwnerRecursive(TreePane owner) {
        this.owner = owner;

        for (TreePaneNode treePaneNode : getChildren()) {
            treePaneNode.setOwnerRecursive(owner);
        }
    }

    /**
     * Returns the Pane to display for that node
     *
     * @return The pane for the node
     */
    public abstract Pane getPane();

    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    @Override
    public TreePaneNode clone() {
        try {
            TreePaneNode clone = (TreePaneNode) super.clone();
            clone.ID = ID + 1;
            clone.parent = parent;
            clone.children = new ArrayList<>(children);
            clone.owner = owner;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TreePaneNode)) {
            return false;
        }
        TreePaneNode that = (TreePaneNode) o;
        return ID == that.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
