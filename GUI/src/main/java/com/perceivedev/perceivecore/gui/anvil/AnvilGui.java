package com.perceivedev.perceivecore.gui.anvil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.perceivedev.perceivecore.coreplugin.PerceiveCore;
import com.perceivedev.perceivecore.gui.Gui;
import com.perceivedev.perceivecore.gui.anvil.AnvilClickEvent.AnvilSlot;
import com.perceivedev.perceivecore.gui.components.Button;
import com.perceivedev.perceivecore.gui.components.panes.AnchorPane;
import com.perceivedev.perceivecore.gui.util.Dimension;
import com.perceivedev.perceivecore.packet.PacketManager;
import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.FieldPredicate;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.MethodPredicate;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.Modifier;
import com.perceivedev.perceivecore.utilities.item.ItemFactory;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.NameSpace.NMS;
import static com.perceivedev.perceivecore.reflection.ReflectionUtil.NameSpace.OBC;


/**
 * A Gui taking input by allowing the user to write something in an Anvil
 */
public class AnvilGui extends Gui implements AnvilInputHolder {

    private static AnvilPacketListener listener = new AnvilPacketListener();
    private static AnvilItemRenameListener anvilItemRenameListener = new AnvilItemRenameListener();

    private Consumer<Optional<String>> callback;
    private Consumer<AnvilTypeEvent> anvilTypeEventConsumer = event -> {
    };

    /**
     * It will add a Paper with the name " " as the default item.
     *
     * @param name The name of the Gui
     * @param callback The callback
     */
    @SuppressWarnings("unused")
    public AnvilGui(String name, Consumer<Optional<String>> callback) {
        super(name, 1, new AnchorPane(3, 1));

        setInventory(Bukkit.createInventory(this, InventoryType.ANVIL, name));

        Objects.requireNonNull(callback, "callback cannot be null!");
        this.callback = callback;

        // Set the item to a paper with a space. This makes it actually work as
        // an input (Can be any item)
        setItem(AnvilSlot.INPUT_LEFT,
                ItemFactory.builder(Material.PAPER).setName(" ").build(),
                false);
    }

    /**
     * Called when this gui is displayed to a player
     * <p>
     * {@link #getPlayer()} is already set at this point
     * <p>
     * <br>
     * <strong><em>Must be called by sub classes, or the Gui WILL NOT
     * WORK</em></strong>
     *
     * @param previous The previous Gui that was displayed. {@code null} if this
     * is the first
     */
    @Override
    protected void onDisplay(Gui previous) {
        getPlayer().ifPresent(player -> {
            PacketManager.getInstance().addListener(listener, player);
            PacketManager.getInstance().addListener(anvilItemRenameListener, player);

            // by the time this method is called, the inventory is NOT yet opened.
            new BukkitRunnable() {
                @Override
                public void run() {
                    new LowerVersionFixer().fixMeIfNeeded(AnvilGui.this);
                }
            }.runTask(PerceiveCore.getInstance());
        });
    }

    /**
     * Called when the Gui is closed. You may overwrite it to listen to close
     * events
     * <p>
     * <br>
     * <strong><em>Must be called by sub classes, or the Gui WILL NOT
     * WORK</em></strong>
     */
    @Override
    protected void onClose() {
        getPlayer().ifPresent(player -> {
            PacketManager.getInstance().removeListener(listener, player);
            PacketManager.getInstance().removeListener(anvilItemRenameListener, player);
        });
    }

    /**
     * Adds an item to the Gui
     *
     * @param slot The slot of the item
     * @param itemStack The {@link ItemStack} to add
     * @param movable Whether the item should be movable by the player. The
     * output is NEVER movable
     */
    @SuppressWarnings("WeakerAccess")
    public void setItem(AnvilSlot slot, ItemStack itemStack, @SuppressWarnings("SameParameterValue") boolean movable) {
        Objects.requireNonNull(slot, "slot cannot be null!");

        // remove it, if it is already set
        getRootAsFixedPosition().removeComponent(slot.getSlot(), 0);

        getRootAsFixedPosition().addComponent(new Button(itemStack, clickEvent -> {
            if (movable) {
                clickEvent.setCancelled(false);
            }
        }, Dimension.ONE), slot.getSlot(), 0);
    }

    /**
     * @param anvilTypeEventConsumer The listener for {@link AnvilTypeEvent}s
     */
    @SuppressWarnings("unused")
    public void setAnvilTypeEventConsumer(Consumer<AnvilTypeEvent> anvilTypeEventConsumer) {
        Objects.requireNonNull(anvilTypeEventConsumer, "anvilTypeEventConsumer can not be null!");

        this.anvilTypeEventConsumer = anvilTypeEventConsumer;
    }

    @Override
    public void reactToTyping(AnvilTypeEvent event) {
        anvilTypeEventConsumer.accept(event);
    }

    /**
     * @param event The {@link AnvilClickEvent}
     */
    @Override
    public void reactToClick(AnvilClickEvent event) {
        ItemStack involvedItem = event.getInvolvedItem();
        Optional<String> name;

        if (involvedItem == null || involvedItem.getType() == Material.AIR || !involvedItem.hasItemMeta()
                || !involvedItem.getItemMeta().hasDisplayName()) {
            name = Optional.empty();
        }
        else {
            name = Optional.ofNullable(involvedItem.getItemMeta().getDisplayName());
        }

        close();
        callback.accept(name);
    }

    private static class LowerVersionFixer {

        private static final Class<?> CONTAINER_CLASS = ReflectionUtil.getClass(
                NMS,
                "Container"
        ).orElseThrow(() -> new RuntimeException("Could not find 'Container' class."));
        private static final Class<?> SLOT_CLASS = ReflectionUtil.getClass(
                NMS,
                "Slot"
        ).orElseThrow(() -> new RuntimeException("Could not find 'Slot' class."));
        private static final Class<?> I_INVENTORY_CLASS = ReflectionUtil.getClass(
                NMS,
                "IInventory"
        ).orElseThrow(() -> new RuntimeException("Could not find 'IInventory' class."));
        private static final Class<?> ENTITY_HUMAN_CLASS = ReflectionUtil.getClass(
                NMS,
                "EntityHuman"
        ).orElseThrow(() -> new RuntimeException("Could not find 'EntityHuman' class."));
        private static final Class<?> CRAFT_PLAYER_CLASS = ReflectionUtil.getClass(
                OBC,
                "entity.CraftPlayer"
        ).orElseThrow(() -> new RuntimeException("Could not find 'entity.CraftPlayer' class."));
        private static final Class<?> CRAFT_INVENTORY_CLASS = ReflectionUtil.getClass(
                OBC,
                "inventory.CraftInventory"
        ).orElseThrow(() -> new RuntimeException("Could not find 'inventory.CraftInventory' class."));

        private static final Method ADD_SLOT_METHOD = ReflectionUtil.getMethod(
                CONTAINER_CLASS,
                new MethodPredicate()
                        .withModifiers(Modifier.PROTECTED)
                        .withReturnType(SLOT_CLASS)
                        .withParameters(SLOT_CLASS)
        ).getValueOrThrow("Could not find 'add slot' method in 'Container'");
        private static final Method CRAFT_INVENTORY_GET_INVENTORY = ReflectionUtil.getMethod(
                CRAFT_INVENTORY_CLASS,
                new MethodPredicate().withName("getInventory")
        ).getValueOrThrow("Could not find 'getInventory' method in 'CraftInventory'");
        private static final Method PLAYER_GET_HANDLE_METHOD = ReflectionUtil.getMethod(
                CRAFT_PLAYER_CLASS,
                new MethodPredicate().withName("getHandle")
        ).getValueOrThrow("Could not find 'getHandle' method in 'CraftPlayer'");

        private static final Constructor<?> SLOT_CONSTRUCTOR = ReflectionUtil.getConstructor(
                SLOT_CLASS,
                I_INVENTORY_CLASS, int.class, int.class, int.class
        ).getValueOrThrow("Could not find 'Slot' constructor.");

        private static final Field ACTIVE_CONTAINER_FIELD = ReflectionUtil.getField(
                ENTITY_HUMAN_CLASS,
                new FieldPredicate().withName("activeContainer")
        ).getValueOrThrow("Could not find 'activeContainer' field in EntityHuman");


        /**
         * Fixes the inventory, if needed
         *
         * @param anvilGui The AnvilGui to fix it for
         */
        void fixMeIfNeeded(AnvilGui anvilGui) {
            if (!needsFixing()) {
                return;
            }
            if (!anvilGui.getPlayer().isPresent()) {
                return;
            }
            Object activeContainer = getActiveContainer(getNMSPlayer(anvilGui.getPlayer().get()));
            Object topIInventory = getTopIInventory(anvilGui.getPlayer().get());
            Object bottomIInventory = getBottomIInventory(anvilGui.getPlayer().get());

            // add the Anvil slots
            addToContainer(createSlot(topIInventory, 0, 27, 47), activeContainer);
            addToContainer(createSlot(topIInventory, 1, 76, 47), activeContainer);
            addToContainer(createSlot(topIInventory, 2, 134, 47), activeContainer);

            // add the slots in the Player inventory
            {
                int i;
                for (i = 0; i < 3; ++i) {
                    for (int j = 0; j < 9; ++j) {
                        addToContainer(
                                createSlot(bottomIInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18),
                                activeContainer
                        );
                    }
                }

                for (i = 0; i < 9; ++i) {
                    addToContainer(createSlot(bottomIInventory, i, 8 + i * 18, 142), activeContainer);
                }

            }
//            this.a(new Slot(top, 0, 27, 47));
//            this.a(new Slot(top, 1, 76, 47));
//            this.a(new Slot(top, 2, 134, 47));
//
//            int i;
//            for(i = 0; i < 3; ++i) {
//                for(int j = 0; j < 9; ++j) {
//                    this.a(new Slot(bottom, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
//                }
//            }
//
//            for(i = 0; i < 9; ++i) {
//                this.a(new Slot(bottom, i, 8 + i * 18, 142));
//            }
        }

        private void addToContainer(Object slot, Object container) {
            ReflectionUtil.invokeMethod(ADD_SLOT_METHOD, container, slot);
        }

        private Object createSlot(Object inventory, int first, int second, int third) {
            return ReflectionUtil.instantiate(SLOT_CONSTRUCTOR, inventory, first, second, third)
                    .getValueOrThrow("Could not instantiate 'Slot'");
        }

        private Object getNMSPlayer(Player player) {
            return ReflectionUtil.invokeMethod(PLAYER_GET_HANDLE_METHOD, player)
                    .getValueOrThrow("Could not invoke 'getHandle' method for the Player");
        }

        private Object getActiveContainer(Object nmsPlayer) {
            return ReflectionUtil.getFieldValue(ACTIVE_CONTAINER_FIELD, nmsPlayer)
                    .getValueOrThrow("Could not read 'activeContainer' field for the Player");
        }

        private Object getTopIInventory(Player player) {
            Inventory topInventory = player.getOpenInventory().getTopInventory();
            return getIIInventory(topInventory);
        }

        private Object getBottomIInventory(Player player) {
            Inventory topInventory = player.getOpenInventory().getBottomInventory();
            return getIIInventory(topInventory);
        }

        private Object getIIInventory(Inventory inventory) {
            return ReflectionUtil.invokeMethod(CRAFT_INVENTORY_GET_INVENTORY, inventory)
                    .getValueOrThrow("Could not invoke 'getInventory' method for the Player");
        }

        boolean needsFixing() {
            return ReflectionUtil.getMajorVersion() <= 1 && ReflectionUtil.getMinorVersion() < 10;
        }
    }
}
