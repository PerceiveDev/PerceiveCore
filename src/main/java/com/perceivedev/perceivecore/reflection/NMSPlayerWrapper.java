package com.perceivedev.perceivecore.reflection;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.$;

import com.perceivedev.perceivecore.packet.Packet;

/** @author Rayzr */
public class NMSPlayerWrapper extends ReflectedClass<Object> {

    protected ReflectedClass<Object> connection;
    protected ReflectedMethod        sendPacket;

    /** @param nmsPlayer The NMS player */
    protected NMSPlayerWrapper(Object nmsPlayer) {
        super(nmsPlayer);
        connection = $(getConnection());
        sendPacket = connection.getMethod("sendPacket");
    }

    /** @param packet The packet to send */
    public void sendPacket(Packet packet) {
        sendPacket.invoke(packet.getNMSPacket());
    }

    /**
     * Returns the playerConnection field
     *
     * @return The "playerConnection" field
     */
    public Object getConnection() {
        return getField("playerConnection").get().getValue();
    }
}
