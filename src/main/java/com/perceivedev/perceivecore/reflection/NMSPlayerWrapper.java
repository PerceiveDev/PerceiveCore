/**
 * 
 */
package com.perceivedev.perceivecore.reflection;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.$;

import org.bukkit.entity.Player;

/**
 * @author Rayzr
 *
 */
public class NMSPlayerWrapper extends ReflectedClass<Object> {

    protected Player player;
    protected ReflectedClass<Object> connection;

    /**
     * @param playerWrapper
     */
    protected NMSPlayerWrapper(PlayerWrapper player) {
        super(player.getMethod("getHandle").invoke().get());
        this.player = player.getInstance();
        connection = $(getField("playerConnection").get().getValue());
    }

    public void sendPacket(Packet packet) {
        connection.getMethod("sendPacket").invoke(packet.getNMSPacket());
    }

}
