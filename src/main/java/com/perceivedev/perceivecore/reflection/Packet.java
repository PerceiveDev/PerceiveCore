/**
 * 
 */
package com.perceivedev.perceivecore.reflection;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.$;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

/**
 * A class which represents a packet.
 * 
 * @author Rayzr
 * 
 * @see #create(String)
 */
public class Packet {

    private Class<?> packetClass;
    private Object   obj;

    private Packet(Class<?> packetClass) throws Exception {

        this.packetClass = packetClass;
        obj = packetClass.getConstructor().newInstance();

    }

    /**
     * Creates a new {@link Packet}
     * 
     * @param name the packet class name
     * @return a new Packet, or null if something went wrong
     */
    public static Packet create(String name) {
        if (!name.startsWith("Packet")) {
            name = "Packet" + name;
        }

        Optional<Class<?>> oClass = $("{nms}." + name);

        if (!oClass.isPresent()) {
            System.err.println("The packet class '" + name + "' could not be found!");
            return null;
        }

        try {
            return new Packet(oClass.get());
        } catch (Exception e) {
            System.err.println("Failed to create Packet!");
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @return the NMS packet
     */
    public Object getNMSPacket() {
        return obj;
    }

    /**
     * Sets one of the fields of the packet
     * 
     * @param field the field to set
     * @param value the value to set
     */
    public void set(String field, Object value) {
        ReflectionUtil.setFieldValue(field, packetClass, obj, value);
    }

    /**
     * Gets the value of one of the fields
     * 
     * @param field the field name
     * @return
     */
    public ReflectResponse<Object> get(String field) {
        return ReflectionUtil.getFieldValue(field, packetClass, obj);
    }

    /**
     * Sends this packet to the given players
     * 
     * @param players the players to send it to
     */
    public void send(Player... players) {
        for (Player player : players) {
            $(player).sendPacket(this);
        }
    }

}
