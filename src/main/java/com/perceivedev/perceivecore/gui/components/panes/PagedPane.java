package com.perceivedev.perceivecore.gui.components.panes;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.ClickEvent;
import com.perceivedev.perceivecore.gui.base.AbstractPane;
import com.perceivedev.perceivecore.gui.base.Component;
import com.perceivedev.perceivecore.gui.base.FixedPositionPane;
import com.perceivedev.perceivecore.gui.base.FreeformPane;
import com.perceivedev.perceivecore.gui.base.Pane;
import com.perceivedev.perceivecore.gui.components.Button;
import com.perceivedev.perceivecore.gui.components.panes.PagedPane.ItemPagePopulateFunction.ItemPopulateItem;
import com.perceivedev.perceivecore.gui.components.simple.DisplayColor;
import com.perceivedev.perceivecore.gui.components.simple.SimpleLabel;
import com.perceivedev.perceivecore.gui.components.simple.StandardDisplayTypes;
import com.perceivedev.perceivecore.gui.util.Dimension;
import com.perceivedev.perceivecore.language.MessageProvider;
import com.perceivedev.perceivecore.util.ItemFactory;
import com.perceivedev.perceivecore.util.ListUtils;
import com.perceivedev.perceivecore.util.TriFunction;

/**
 * A paged pane.
 * <p>
 * It can contain as many {@link Component}s as you please and
 * will create pages for that
 */
@SuppressWarnings({ "unused", "WeakerAccess" })
public class PagedPane extends AbstractPane implements FixedPositionPane, FreeformPane {

    private Function<PagedPane, AnchorPane>                   pageGenerator;
    private BiConsumer<PagedPane, AnchorPane>                 pagePopulateFunction;
    private TriFunction<PagedPane, Integer, Integer, Boolean> controlPlaceholderPredicate;

    private List<AnchorPane>                                  pages       = new ArrayList<>();
    private int                                               currentPage = 0;

    /**
     * @param width The width of this pane
     * @param height The height of this pane
     * @param inventoryMap The {@link InventoryMap} to use
     *
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if the size of the InventoryMap is
     *             different than the size of this Pane
     */
    public PagedPane(int width, int height, InventoryMap inventoryMap) {
        super(width, height, inventoryMap);

        components.forEach(this::addComponent);

        pageGenerator = pagedPane -> new AnchorPane(getWidth(), getHeight());
        pagePopulateFunction = (pagedPane, anchorPane) -> {
            anchorPane.addComponent(new SimpleLabel(new Dimension(9, 1), StandardDisplayTypes.FLAT, DisplayColor.BLACK, " "), 0, getHeight() - 2);

            {
                Map<Point, ItemPopulateItem> map = new HashMap<>();

                if (pagedPane.getCurrentPageIndex() > 0) {

                    // prev button
                    map.put(new Point(0, getHeight() - 1),
                            new ItemPopulateItem(
                                    ItemFactory.builder(Material.COAL_BLOCK)
                                            .setName("&3&lPage &a&l{PREV_PAGE} &7/ &c&l{MAX_PAGE}")
                                            .setLore("&7Brings you back to the page &c{PREV_PAGE}")
                                            .build(),
                                    -1));
                }

                // current page info
                map.put(new Point(getWidth() / 2, getHeight() - 1),
                        new ItemPopulateItem(
                                ItemFactory.builder(Material.BOOK)
                                        .setName("&3&lPage &a&l{CURRENT_PAGE} &7/ &c&l{MAX_PAGE}")
                                        .addLore("&7You are on page &a{CURRENT_PAGE} &7/ &c{MAX_PAGE}")
                                        .build(),
                                0));

                if (pagedPane.getCurrentPageIndex() < pagedPane.getPageCount() - 1) {
                    // next button
                    map.put(new Point(getWidth() - 1, getHeight() - 1),
                            new ItemPopulateItem(
                                    ItemFactory.builder(Material.IRON_BLOCK)
                                            .setName("&3&lPage &a&l{NEXT_PAGE} &7/ &c&l{MAX_PAGE}")
                                            .addLore("&7Brings you to the page &a{NEXT_PAGE}")
                                            .build(),
                                    1));
                }

                new ItemPagePopulateFunction(map).accept(pagedPane, anchorPane);
            }
        };

        controlPlaceholderPredicate = (pagedPane, x, y) -> y < pagedPane.getHeight() - 2;
    }

    /**
     * An empty pane
     * 
     * @param width The width of this pane
     * @param height The height of this pane
     */
    public PagedPane(int width, int height) {
        this(width, height, new InventoryMap(new Dimension(width, height)));
    }

    /**
     * Sets the Page generator
     * <p>
     * This function generates a new AnchorPane when needed
     *
     * @param pageGenerator The new page generator
     */
    public void setPageGenerator(Function<PagedPane, AnchorPane> pageGenerator) {
        this.pageGenerator = pageGenerator;
    }

    /**
     * Sets the function populating a page just before it is rendered
     * <p>
     * This function should add buttons to navigate the pages as well as some
     * sort of separator between them and the pane contents
     *
     * @param pagePopulateFunction The populate function
     */
    public void setPagePopulateFunction(BiConsumer<PagedPane, AnchorPane> pagePopulateFunction) {
        this.pagePopulateFunction = pagePopulateFunction;
    }

    /**
     * Sets the predicate reserving space for the controls
     * <p>
     * Default is the last two rows
     * 
     * @param controlPlaceholderPredicate The predicate reserving space for the
     *            controls. <br>
     *            {@code <This pane, x coordinate, y coordinate, placement allowed>}
     */
    public void setControlPlaceholderPredicate(TriFunction<PagedPane, Integer, Integer, Boolean> controlPlaceholderPredicate) {
        this.controlPlaceholderPredicate = controlPlaceholderPredicate;
    }

    /**
     * @return The currently selected page index
     */
    public int getCurrentPageIndex() {
        return currentPage;
    }

    /**
     * Changes to the next page, if any
     *
     * @return True if the page was switched
     */
    public boolean nextPage() {
        return selectPage(currentPage + 1);
    }

    /**
     * Changes to the previous page, if any
     *
     * @return True if the page was switched
     */
    public boolean previousPage() {
        return selectPage(currentPage - 1);
    }

    /**
     * Selects the given page, if possible
     *
     * @param pageIndex The <b>index</b> of the page. Will be adjusted to be
     *            within {@code [0 ; pages.size())}
     *
     * @return True if the page was switched. False if the page was already
     *         selected.
     */
    public boolean selectPage(int pageIndex) {
        int newIndex = clamp(0, pages.size() - 1, pageIndex);

        if (currentPage != newIndex) {
            currentPage = newIndex;

            // draw the new page
            requestReRender();

            return true;
        }

        return false;
    }

    /**
     * Returns the amount of pages in this pane
     *
     * @return The amount of pages
     */
    public int getPageCount() {
        return pages.size();
    }

    @Override
    public boolean addComponent(Component component) {
        addComponent(component, -1, -1);

        // it will be a added or a new page will be created
        return true;
    }

    @Override
    public boolean removeComponent(Component component) {
        int counter = 0;

        boolean modifiedOpenedPane = false;

        for (Iterator<AnchorPane> iterator = pages.iterator(); iterator.hasNext();) {
            AnchorPane page = iterator.next();

            if (page.removeComponent(component)) {
                if (page.getChildren().isEmpty() && counter > 1) {
                    iterator.remove();
                    if (currentPage >= counter) {
                        currentPage--;
                    }
                } else if (currentPage == counter) {
                    modifiedOpenedPane = true;
                }

                if (modifiedOpenedPane) {
                    requestReRender();
                }
            }

            counter++;
        }

        return false;
    }

    /**
     * Adds a component to the pane
     * <p>
     * Complexity is quite high
     *
     * @param component The {@link Component} to add
     * @param xPos The x pos to add it to. {@code < 0} for search free
     * @param yPos The y pos to add it to. {@code < 0} for search free
     *
     * @return True if the component was added to an existing page, false if a
     *         new one was created for it.
     */
    public boolean addComponent(Component component, int xPos, int yPos) {

        if (xPos > 0 && yPos > 0) {
            if (!controlPlaceholderPredicate.apply(this, xPos, yPos)) {
                return false;
            }
        }

        for (int i = 0; i < pages.size(); i++) {
            AnchorPane page = pages.get(i);
            if (xPos < 0 || yPos < 0) {
                for (int y = 0; y < page.getHeight(); y++) {
                    for (int x = 0; x < page.getWidth(); x++) {
                        if (!controlPlaceholderPredicate.apply(this, x, y)) {
                            continue;
                        }
                        if (page.addComponent(component, x, y)) {
                            if (i == currentPage) {
                                requestReRender();
                            }
                            return true;
                        }
                    }
                }
            } else {
                if (page.addComponent(component, xPos, yPos)) {
                    if (i == currentPage) {
                        requestReRender();
                    }
                    return true;
                }
            }
        }

        addNewPane();

        if (xPos < 0 || yPos < 0) {
            pages.get(pages.size() - 1).addComponent(component, 0, 0);
        } else {
            pages.get(pages.size() - 1).addComponent(component, xPos, yPos);
        }

        return false;
    }

    @Override
    public boolean removeComponent(int x, int y) {
        Optional<Component> component = getComponentAtPoint(x, y);

        return component.isPresent() && removeComponent(component.get());
    }

    /**
     * Adds a new pane
     * 
     * The page will be appended to the <b>END</b>
     */
    public void addNewPane() {
        pages.add(pageGenerator.apply(this));

        // update buttons (MAX_PAGE) and stuff
        requestReRender();
    }

    /**
     * Returns the pane at the given index
     * 
     * @param index The index of the pane {@code [0; #getPageCount())}
     * @throws IndexOutOfBoundsException if the index is {@code < 0 or >=}
     *             {@link #getPageCount()}
     * @return The pane at the index. Currently an AnchorPane (which is funny,
     *         as you can not add components to that without casting)
     */
    public Pane getPane(int index) {
        if (index < 0 || index >= getPageCount()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + getPageCount());
        }

        return pages.get(index);
    }

    /**
     * Removes a Pane
     * 
     * @param index The index of the pane to remove {@code [0; #getPageCount())}
     * @throws IndexOutOfBoundsException if the index is {@code < 0 or >=}
     *             {@link #getPageCount()}
     */
    public void removePane(int index) {
        if (index < 0 || index >= getPageCount()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + getPageCount());
        }

        pages.remove(index);

        boolean reRender = false;

        if (index == currentPage) {
            // if the last page was deleted
            if (currentPage >= pages.size()) {
                currentPage = pages.size() - 1;
            }

            if (getPageCount() <= 0) {
                addNewPane();
                currentPage = 0;
            }

            reRender = true;
        }

        if (index == currentPage - 1 || index == currentPage + 1) {
            reRender = true;
        }

        if (reRender) {
            requestReRender();
        }
    }

    @Override
    public void render(Inventory inventory, Player player, int x, int y) {
        AnchorPane page = pages.get(currentPage);

        pagePopulateFunction.accept(this, page);

        page.render(inventory, player, x, y);
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        pages.get(currentPage).onClick(clickEvent);
    }

    /**
     * Ensures a value ({@code given}) is inside the given bounds
     *
     * @param min The minimum value
     * @param max The maximum value
     * @param given The given value
     *
     * @return A value in the interval [min;max]
     */
    private static int clamp(int min, int max, int given) {
        if (given < min) {
            return min;
        }
        if (given > max) {
            return max;
        }
        return given;
    }

    // @formatter:off
    /**
     * A page populate function that takes the name, lore one, lore two and
     * material from a given message provider
     * <p>
     * <b>Language keys:</b>
     * <ul>
     *     <li>$baseKey$.name</li>
     *     <li>$baseKey$.lore.one</li>
     *     <li>$baseKey$.lore.two</li>
     *     <li>$baseKey$.material</li>
     * </ul>
     * 
     * <b>Base keys:</b>
     * <ul>
     *     <li>Next Button: {@code "pageable.gui.next.button"}</li>
     *     <li>Back Button: {@code "pageable.gui.back.button"}</li>
     *     <li>Info Button: {@code "pageable.gui.current.button"}</li>
     * </ul>
     */
    // @formatter:on
    public static class TranslatedPagePopulateFunction implements BiConsumer<PagedPane, AnchorPane> {

        private MessageProvider language;

        @Override
        public void accept(PagedPane pagedPane, AnchorPane anchorPane) {
            if (pagedPane.getCurrentPageIndex() > 0) {
                addButton(anchorPane, pagedPane, pagedPane.getCurrentPageIndex(), 0, pagedPane.getHeight() - 1, "pageable.gui.back.button", -1);
            }

            if (pagedPane.getCurrentPageIndex() < pagedPane.getPageCount() - 1) {
                addButton(anchorPane, pagedPane, pagedPane.getCurrentPageIndex(), pagedPane.getWidth() - 1, pagedPane.getHeight() - 1, "pageable.gui.next.button", 1);
            }

            if (pagedPane.getWidth() > 2) {
                int xPosition = pagedPane.getWidth() / 2;
                addButton(anchorPane, pagedPane, pagedPane.getCurrentPageIndex(), xPosition, pagedPane.getHeight() - 1, "pageable.gui.current.label", 0);
            }
        }

        private void addButton(AnchorPane page, PagedPane pagedPane, int index, int x, int y, String baseKey, int pageMod) {
            Optional<Component> originalButton = page.getComponentAtPoint(x, y);

            originalButton.ifPresent(page::removeComponent);

            String buttonName = language.tr(baseKey + ".name", index + pageMod + 1, pagedPane.getPageCount());
            String buttonLoreOne = language.tr(baseKey + ".lore.one", index + pageMod + 1, pagedPane.getPageCount());
            String buttonLoreTwo = language.tr(baseKey + ".lore.two", index + pageMod + 1, pagedPane.getPageCount());
            String materialName = language.tr(baseKey + ".material", index + pageMod + 1, pagedPane.getPageCount());
            Material material = Material.matchMaterial(materialName) == null ? Material.WOOL : Material.matchMaterial(materialName);

            ItemStack itemStack = ItemFactory.builder(material)
                    .setName(buttonName)
                    .addLore(buttonLoreOne, buttonLoreTwo)
                    .build();

            Button button = new Button(itemStack, clickEvent -> pagedPane.selectPage(Math.max(pagedPane.getPageCount() + pageMod, 0)), Dimension.ONE);

            page.addComponent(button, x, y);
        }
    }

    // @formatter:off
    /**
     * A populate function that inserts Buttons based on items
     * <p>
     * <b>Placeholders in the item's name and lore:</b>
     * <ul>
     *     <li>"{@code {CURRENT_PAGE}}" {@code -->} The current page</li>
     *     <li>"{@code {MAX_PAGE}}" {@code -->} The maximum page</li>
     *     <li>"{@code {PREV_PAGE}}" {@code -->} The previous page</li>
     *     <li>"{@code {NEXT_PAGE}}" {@code -->} The next page</li>
     *     <li>"{@code {POINTED_PAGE}}" {@code -->} The page it leads to</li>
     * </ul>
     */
    // @formatter:on
    public static class ItemPagePopulateFunction implements BiConsumer<PagedPane, AnchorPane> {
        private Map<Point, ItemPopulateItem> items = new HashMap<>();

        public ItemPagePopulateFunction(Map<Point, ItemPopulateItem> items) {
            this.items = new HashMap<>(items);
        }

        public ItemPagePopulateFunction() {
            this(Collections.emptyMap());
        }

        /**
         * Adds an item
         * 
         * @param x The x coordinate of the item
         * @param y The y coordinate of the item
         * @param itemStack The ItemStack to add
         */
        public void addItem(int x, int y, ItemPopulateItem itemStack) {
            items.put(new Point(x, y), itemStack);
        }

        @Override
        public void accept(PagedPane pagedPane, AnchorPane anchorPane) {
            for (Entry<Point, ItemPopulateItem> entry : items.entrySet()) {
                addButton(anchorPane, pagedPane, pagedPane.getCurrentPageIndex(), entry.getKey().x, entry.getKey().y,
                        entry.getValue().getPageMod(), entry.getValue().getItemStack());
            }
        }

        private void addButton(AnchorPane page, PagedPane pagedPane, int index, int x, int y, int pageMod, ItemStack item) {
            Optional<Component> originalButton = page.getComponentAtPoint(x, y);

            originalButton.ifPresent(page::removeComponent);

            ItemStack itemStack;

            int pointingTo = clamp(0, pagedPane.getPageCount() - 1, index + pageMod);

            {
                ItemFactory factory = ItemFactory.builder(item);
                List<String> lore = item.hasItemMeta() && item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();
                Function<String, String> replacementFunction = s -> {
                    String replaced = s.replace("{CURRENT_PAGE}", Integer.toString(index + 1));
                    replaced = replaced.replace("{MAX_PAGE}", Integer.toString(pagedPane.getPageCount()));
                    replaced = replaced.replace("{PREV_PAGE}", Integer.toString(Math.max(0, index - 1) + 1));
                    replaced = replaced.replace("{NEXT_PAGE}", Integer.toString(Math.min(pagedPane.getPageCount(), index + 2)));
                    replaced = replaced.replace("{POINTED_PAGE}", Integer.toString(pointingTo));

                    return replaced;
                };
                lore = ListUtils.replaceInAll(lore, replacementFunction);

                factory.setLore(lore);

                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    factory.setName(replacementFunction.apply(item.getItemMeta().getDisplayName()));
                }

                itemStack = factory.build();
            }

            Button button = new Button(itemStack, clickEvent -> pagedPane.selectPage(pointingTo), Dimension.ONE);

            page.addComponent(button, x, y);
        }

        public static class ItemPopulateItem {
            private ItemStack itemStack;
            private int       pageMod;

            public ItemPopulateItem(ItemStack itemStack, int pageMod) {
                this.itemStack = itemStack;
                this.pageMod = pageMod;
            }

            /**
             * @return The modifier for the page
             */
            public int getPageMod() {
                return pageMod;
            }

            /**
             * @return The {@link ItemStack}
             */
            public ItemStack getItemStack() {
                return itemStack.clone();
            }
        }
    }
}
