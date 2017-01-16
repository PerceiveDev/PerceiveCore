// @formatter:off
/**
 * The {@code gui} package
 * <p>
 * This package contains utilities to make dealing with inventory menus more
 * pleasant.
 * <p>
 *     
 * <br>The main "attack point" for you is the {@link com.perceivedev.bukkitpluginutilities.gui.Gui Gui} class.
 * <br>You can add {@link com.perceivedev.bukkitpluginutilities.gui.base.Component Component}s to the Gui, to have them displayed.
 * <p>
 *
 * <br>Upon calling {@link com.perceivedev.bukkitpluginutilities.gui.Gui#open(org.bukkit.entity.Player) Gui#open(Player)}
 *     (or related methods in {@link com.perceivedev.bukkitpluginutilities.gui.Gui Gui} or {@link com.perceivedev.bukkitpluginutilities.gui.GuiManager GuiManager})
 *     the {@link com.perceivedev.bukkitpluginutilities.gui.Gui Gui} is added to a Stack like structure,
 *     the {@link com.perceivedev.bukkitpluginutilities.gui.GuiManager GuiManager}.
 *     Please keep this in mind as you can do some cool stuff, e.g. a "flow" of Guis or a "Dialog", with it.
 *     
 * <p>
 * <br><u>There are a few things to keep in mind though:</u>
 * <ul>
 *     <li>Most display methods are actually delayed by a tick. This is documented
 *         for all methods where it is true
 *     </li>
 *     <li>
 *         <b><i><u>ALL</u></i></b> code must be assumed to be not thread safe. Some
 *         things are, but critical things like creating a {@link com.perceivedev.bukkitpluginutilities.gui.Gui Gui}
 *         or {@link com.perceivedev.bukkitpluginutilities.gui.base.Component Component} are NOT
 *     </li>
 * </ul>
 */
// @formatter:on
package com.perceivedev.bukkitpluginutilities.gui;