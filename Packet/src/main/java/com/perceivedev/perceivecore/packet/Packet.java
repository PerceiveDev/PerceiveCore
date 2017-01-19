package com.perceivedev.perceivecore.packet;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import org.bukkit.entity.Player;

import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.NameSpace.NMS;

/**
 * A class which represents a packet.
 *
 * @see #create(String)
 */
public class Packet {

    /**
     * The net.minecraft.server.Packet class
     * <p>
     * Will be null if not found
     */
    private static final Class<?> NMS_PACKET_CLASS = ReflectionUtil.getClass(NMS, "Packet")
            .orElseThrow(() -> new RuntimeException("Can't find NMS Packet base class."));

    private Class<?> packetClass;
    private Object rawPacket;

    /**
     * Creates a packet
     *
     * @param packetClass The class of the packet
     *
     * @throws NoSuchMethodException     if the packet has no default constructor
     * @throws IllegalAccessException    if I have no rights to use reflection
     * @throws InvocationTargetException if the packet constructor threw an
     *                                   error
     * @throws InstantiationException    if an error occurred finding something to
     *                                   instantiate
     */
    private Packet(Class<?> packetClass) throws NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        this(packetClass.getConstructor().newInstance());
    }

    /**
     * @param packet The raw NMS packet
     */
    private Packet(Object packet) {
        this.rawPacket = packet;
        this.packetClass = packet.getClass();
    }

    /**
     * Creates a new {@link Packet}
     *
     * @param name the packet class name. Can be in two forms: "PacketXXX" or
     * "XXX" (e.g. "PacketPlayOutPosition" or "PlayOutPosition"
     *
     * @return a new Packet, or null if something went wrong
     *
     * @throws IllegalArgumentException if it couldn't find the specified packet
     *                                  class
     * @throws RuntimeException         wrapping any of the exceptions occurring due to
     *                                  Reflection (i.e. {@link NoSuchMethodException},
     *                                  {@link IllegalAccessException},
     *                                  {@link InvocationTargetException},
     *                                  {@link InstantiationException})
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public static Packet create(String name) {
        String packetName = name;
        if (!packetName.startsWith("Packet")) {
            packetName = "Packet" + packetName;
        }

        Class<?> packetClass = ReflectionUtil.getClass(NMS, packetName)
                .orElseThrow(() -> new IllegalArgumentException("The packet class '" + name + "' could not be found!"));

        try {
            return new Packet(packetClass);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException
                e) {
            String message = "Failed to create packet!"
                    + String.format("Name: %s, Replaced, %s Class: %s", name, packetName, packetClass);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Creates a new Packet
     *
     * @param nmsPacket The NMS packet object
     *
     * @return The wrapping Packet
     *
     * @throws IllegalStateException    if it couldn't find the NMS base class
     *                                  "Packet" (You are screwed)
     * @throws IllegalArgumentException if it isn't a packet.
     * @throws RuntimeException         if <i>some</i> error occurred instantiating the
     *                                  {@link Packet}
     */
    @SuppressWarnings("WeakerAccess")
    public static Packet createFromNMSPacket(Object nmsPacket) {
        Objects.requireNonNull(nmsPacket, "nmsPacket can not be null");

        if (NMS_PACKET_CLASS == null) {
            throw new IllegalStateException("Could not find packet class! Therefore this class is broken.");
        }

        if (!ReflectionUtil.inheritsFrom(nmsPacket.getClass(), NMS_PACKET_CLASS)) {
            throw new IllegalArgumentException("You must pass a 'Packet' object!");
        }

        try {
            return new Packet(nmsPacket);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create packet!", e);
        }
    }

    /**
     * @return the NMS packet
     */
    @SuppressWarnings("WeakerAccess")
    public Object getNMSPacket() {
        return rawPacket;
    }

    /**
     * Sets one of the fields of the packet
     *
     * @param field the field to set
     * @param value the value to set
     *
     * @return The {@link ReflectResponse} detailing if it was successful
     */
    @SuppressWarnings("unused")
    public ReflectResponse<Void> set(String field, Object value) {
        return ReflectionUtil.setFieldValue(field, packetClass, rawPacket, value);
    }

    /**
     * Gets the value of one of the fields
     *
     * @param field the field name
     *
     * @return The field
     */
    @SuppressWarnings("unused")
    public ReflectResponse<Object> get(String field) {
        return ReflectionUtil.getFieldValue(field, packetClass, rawPacket);
    }

    /**
     * Sends this packet to the given players
     *
     * @param players the players to send it to
     */
    @SuppressWarnings("unused")
    public void send(Player... players) {
        for (Player player : players) {
            PacketSender.getInstance().sendPacket(this, player);
        }
    }

    /**
     * @return the packet's class
     */
    @SuppressWarnings("unused")
    public Class<?> getPacketClass() {
        return packetClass;
    }
}
