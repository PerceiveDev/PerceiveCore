/**
 *
 */
package com.perceivedev.perceivecore.reflection;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.perceivedev.perceivecore.packet.Packet;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

/** @author Rayzr */
public class PlayerWrapper extends ReflectedClass<Player> {

    private static Method GET_HANDLE_METHOD;

    /**
     * Wraps a player
     *
     * @param player The Player to wrap
     */
    protected PlayerWrapper(Player player) {
        super(player);
    }

    /**
     * Gets the NMS player
     *
     * @return a new NMSPlayerWrapper
     */
    public NMSPlayerWrapper getHandle() {
        if (GET_HANDLE_METHOD == null) {
            GET_HANDLE_METHOD = getMethod("getHandle").getMethod();
        }
        ReflectResponse<Object> nmsPlayer = ReflectionUtil.invokeMethod(GET_HANDLE_METHOD, instance);
        if (!nmsPlayer.isSuccessful() || !nmsPlayer.isValuePresent()) {
            return null;
        }
        return new NMSPlayerWrapper(nmsPlayer.getValue());
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
