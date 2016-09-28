/**
 * 
 */
package com.perceivedev.perceivecore.reflection;

import org.bukkit.entity.Player;

import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

/**
 * @author Rayzr
 *
 */
public class PlayerWrapper extends ReflectedClass<Player> {

    protected PlayerWrapper(Player player) {
        super(player);

    }

    /**
     * Gets the NMS player
     * 
     * @return a new NMSPlayerWrapper
     */
    public NMSPlayerWrapper getHandle() {
        ReflectResponse<Object> nmsPlayer = getMethod("getHandle").invoke();
        if (!nmsPlayer.isSuccessful() || !nmsPlayer.isValuePresent()) {
            return null;
        }
        return new NMSPlayerWrapper(nmsPlayer);
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
