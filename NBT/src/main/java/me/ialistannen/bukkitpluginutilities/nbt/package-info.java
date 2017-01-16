// @formatter:off
/**
 * This package handles the modification of NBT data.
 * <p>
 * <br><b>Basics:</b>
 * <br>I recreated the normal NBT classes quite closely, so there is not a big difference. All wrapper classes can be found in the
 * {@link me.ialistannen.bukkitpluginutilities.nbt.NBTWrappers NBTWrappers} class.
 * <br>
 * <p><b>Example:</b>
 * <br>{@code NBTTagString string = new NBTTagString("Hey");}
 * <br>{@code NBTTagCompound compound = new NBTTagCompound();}
 * <br>{@code compound.setString("Key", string);}
 * <p>
 * <br><b>Editing:</b>
 * <ul>
 *     <li>
 *         <b>Item NBT:</b>
 *         <ul>
 *             <li>
 *                 <b>Utility</b>:
 *                 <br>{@link me.ialistannen.bukkitpluginutilities.nbt.ItemNBTUtil ItemNBTUtil}
 *             </li>
 *             <li>
 *                 <b>Getting the tag:</b>
 *                 <br>{@link me.ialistannen.bukkitpluginutilities.nbt.ItemNBTUtil#getTag(org.bukkit.inventory.ItemStack)
 *                     ItemNBTUtil#getTag(ItemStack)}
 *             </li>
 *             <li>
 *                 <b>Setting the tag:</b>
 *                 <br>{@link me.ialistannen.bukkitpluginutilities.nbt.ItemNBTUtil#setNBTTag(me.ialistannen.bukkitpluginutilities.nbt.NBTWrappers.NBTTagCompound, org.bukkit.inventory.ItemStack) 
 *                     ItemNBTUtil#setTag(NBTTagCompound, ItemStack)}
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>Entity NBT:</b>
 *         <ul>
 *             <li>
 *                 <b>Utility:</b>
 *                 <br>{@link me.ialistannen.bukkitpluginutilities.nbt.EntityNBTUtil EntityNBTUtil}
 *             </li>
 *             <li>
 *                 <b>Getting the tag:</b>
 *                 <br>{@link me.ialistannen.bukkitpluginutilities.nbt.EntityNBTUtil#getNbtTag(org.bukkit.entity.Entity)
 *                      EntityNBTUtil#getTag(Entity)}
 *             </li>
 *             <li>
 *                 <b>Setting the tag</b>
 *                 <br>{@link me.ialistannen.bukkitpluginutilities.nbt.EntityNBTUtil#setNbtTag(org.bukkit.entity.Entity, me.ialistannen.bukkitpluginutilities.nbt.NBTWrappers.NBTTagCompound)
 *                      EntityNBTUtil#setTag(Entity, NBTTagCompound}
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>TileEntity NBT:</b>
 *         <ul>
 *             <li>
 *                 <b>Utility:</b>
 *                 <br>{@link me.ialistannen.bukkitpluginutilities.nbt.TileEntityNBTUtil}
 *             </li>
 *             <li>
 *                 <b>Getting the tag:</b>
 *                 <br>{@link me.ialistannen.bukkitpluginutilities.nbt.TileEntityNBTUtil#getNbtTag(org.bukkit.block.BlockState)
  *                     TileEntityNBTUtil#getNbtTag(BlockState)}
 *             </li>
 *             <li>
 *                 <b>Setting the tag:</b>
 *                 <br>{@link me.ialistannen.bukkitpluginutilities.nbt.TileEntityNBTUtil#setNbtTag(org.bukkit.block.BlockState, me.ialistannen.bukkitpluginutilities.nbt.NBTWrappers.NBTTagCompound)
 *                      TileEntityNBTUtil#setNbtTag(BlockState, NBTTagCompund)}
 *             </li>
 *         </ul>
 *     </li>
 * </ul>
 */
// @formatter:on
package me.ialistannen.bukkitpluginutilities.nbt;