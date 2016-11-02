package com.perceivedev.perceivecore.packet;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.$;
import static com.perceivedev.perceivecore.reflection.ReflectionUtil.NameSpace.NMS;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;
import com.perceivedev.perceivecore.util.Converter;
import com.perceivedev.perceivecore.util.Converters;

/**
 * A class which represents a packet.
 *
 * @author Rayzr
 * @see #create(String)
 */
public class Packet {

    private static ConverterMap converters = new ConverterMap();

    static {

        // Load the Converters class. This should work...
        Converters.class.getName();

    }

    /**
     * @param a
     * @param b
     * @param converter
     * @return
     * @see com.perceivedev.perceivecore.packet.ConverterMap#addConverter(java.lang.Class,
     *      java.lang.Class, com.perceivedev.perceivecore.util.Converter)
     */
    public static <A, B> boolean addConverter(Class<?> a, Class<?> b, Converter<A, B> converter) {
        return converters.addConverter(a, b, converter);
    }

    /**
     * @param a
     * @param b
     * @param converter
     * @return
     * @see com.perceivedev.perceivecore.packet.ConverterMap#removeConverter(java.lang.Class,
     *      java.lang.Class, com.perceivedev.perceivecore.util.Converter)
     */
    public static <A, B> boolean removeConverter(Class<A> a, Class<B> b, Converter<A, B> converter) {
        return converters.removeConverter(a, b, converter);
    }

    /**
     * @param a
     * @param b
     * @return
     * @see com.perceivedev.perceivecore.packet.ConverterMap#removeConverter(java.lang.Class,
     *      java.lang.Class)
     */
    public static <A, B> boolean removeConverter(Class<A> a, Class<B> b) {
        return converters.removeConverter(a, b);
    }

    /**
     * @param a
     * @param b
     * @return
     * @see com.perceivedev.perceivecore.packet.ConverterMap#hasConverter(java.lang.Class,
     *      java.lang.Class)
     */
    public static <A, B> boolean hasConverter(Class<A> a, Class<B> b) {
        return converters.hasConverter(a, b);
    }

    /**
     * The net.minecraft.server.Packet class
     * <p>
     * Will be null if not found
     */
    private static final Class<?> NMS_PACKET_CLASS;

    static {

        Optional<Class<?>> packet = ReflectionUtil.getClass(NMS, "Packet");
        if (!packet.isPresent()) {
            PerceiveCore.getInstance().getLogger().log(Level.WARNING, "Can't find NMS Packet base class.");
            NMS_PACKET_CLASS = null;
        } else {
            NMS_PACKET_CLASS = packet.get();
        }

    }

    private Class<?> packetClass;
    private Object   obj;

    private Packet(Class<?> packetClass) throws Exception {
        this(packetClass.getConstructor().newInstance());
        this.packetClass = packetClass;
    }

    private Packet(Object packet) {
        this.obj = packet;
        this.packetClass = packet.getClass();
    }

    /**
     * Creates a new {@link Packet}
     *
     * @param name the packet class name
     *
     * @return a new Packet, or null if something went wrong
     *
     * @throws IllegalArgumentException if it couldn't find the specified packet
     *             class
     */
    public static Packet create(String name) {
        if (!name.startsWith("Packet")) {
            name = "Packet" + name;
        }

        Optional<Class<?>> oClass = $("{nms}." + name);

        if (!oClass.isPresent()) {
            throw new IllegalArgumentException("The packet class '" + name + "' could not be found!");
        }

        try {
            return new Packet(oClass.get());
        } catch (Exception e) {
            PerceiveCore.getInstance().getLogger().log(Level.WARNING, "Failed to create packet!", e);
            return null;
        }
    }

    /**
     * Creates a new Packet
     *
     * @param obj The NMS packet object
     *
     * @return The wrapping Packet
     *
     * @throws IllegalStateException if it couldn't find the NMS base class
     *             "Packet" (You are screwed)
     * @throws IllegalArgumentException if it isn't a packet.
     */
    public static Packet createFromObject(Object obj) {
        Objects.requireNonNull(obj, "obj can not be null");

        if (obj instanceof Packet) {
            return (Packet) obj;
        }

        if (NMS_PACKET_CLASS == null) {
            throw new IllegalStateException("Could not find packet class!");
        }

        if (!NMS_PACKET_CLASS.isAssignableFrom(obj.getClass())) {
            throw new IllegalArgumentException("You must pass a packet object!");
        }

        try {
            return new Packet(obj);
        } catch (Exception e) {
            PerceiveCore.getInstance().getLogger().log(Level.WARNING, "Failed to create packet!", e);
            return null;
        }
    }

    /** @return the NMS packet */
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
     *
     * @return The field
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

    /** @return the packet's class */
    public Class<?> getPacketClass() {
        return packetClass;
    }

}
