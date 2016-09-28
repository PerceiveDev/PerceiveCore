/**
 * 
 */
package com.perceivedev.perceivecore.reflection;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.$;

/**
 * @author Rayzr
 *
 */
public class NMSPlayerWrapper extends ReflectedClass<Object> {

    protected ReflectedClass<Object> connection;

    /**
     * @param playerWrapper
     */
    protected NMSPlayerWrapper(Object nmsPlayer) {
        super(nmsPlayer);
        connection = $(getField("playerConnection").get().getValue());
    }

    public void sendPacket(Packet packet) {
        connection.getMethod("sendPacket").invoke(packet.getNMSPacket());
    }

}
