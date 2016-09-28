/**
 * 
 */
package com.perceivedev.perceivecore.reflection;

import org.bukkit.entity.Player;

/**
 * @author Rayzr
 *
 */
public class PlayerWrapper extends ReflectedClass<Player> {

    protected PlayerWrapper(Player player) {
        super(player);

    }

    public NMSPlayerWrapper getHandle() {
        return new NMSPlayerWrapper(this);
    }

    /**
     * Sends a packet to the player
     * 
     * @param packet the packet to send
     */
    public void sendPacket(Packet packet) {
        getHandle().sendPacket(packet);
    }

}
