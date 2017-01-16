package me.ialistannen.bukkitpluginutilities.packet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import me.ialistannen.bukkitpluginutilities.reflection.ReflectionUtil;
import me.ialistannen.bukkitpluginutilities.reflection.ReflectionUtil.MethodPredicate;

import static me.ialistannen.bukkitpluginutilities.reflection.ReflectionUtil.NameSpace.NMS;
import static me.ialistannen.bukkitpluginutilities.reflection.ReflectionUtil.NameSpace.OBC;

/**
 * A Packet sender
 */
class PacketSender {

    private static final Class<?> CRAFT_PLAYER = ReflectionUtil
            .getClass(OBC, "entity.CraftPlayer")
            .orElseThrow(() -> new RuntimeException("Couldn't find CraftPlayer class!"));
    private static final Class<?> PLAYER_CONNECTION = ReflectionUtil.getClass(
            NMS,
            "PlayerConnection"
    ).orElseThrow(() -> new RuntimeException("Couldn't find PlayerConnection class!"));
    private static final Class<?> ENTITY_PLAYER = ReflectionUtil.getClass(
            NMS,
            "EntityPlayer"
    ).orElseThrow(() -> new RuntimeException("Couldn't find EntityPlayer class!"));


    private static final Method GET_HANDLE = ReflectionUtil.getMethod(
            CRAFT_PLAYER,
            new MethodPredicate().withName("getHandle")
    ).getValueOrThrow("Couldn't find getHandle method");
    private static final Method SEND_PACKET = ReflectionUtil.getMethod(
            PLAYER_CONNECTION,
            new MethodPredicate().withName("sendPacket")
    ).getValueOrThrow("Couldn't find sendPacket method");

    private static final Field PLAYER_CONNECTION_FIELD = ReflectionUtil.getField(
            ENTITY_PLAYER,
            new ReflectionUtil.FieldPredicate().withName("playerConnection")
    ).getValueOrThrow("Couldn't find playerConnection field");

    private static final PacketSender instance = new PacketSender();

    private PacketSender() {
    }

    /**
     * Sends a packet to a Player
     *
     * @param packet The {@link Packet} to send
     * @param player The Player to send it to
     */
    void sendPacket(Packet packet, Player player) {
        sendPacket(packet.getNMSPacket(), getConnection(player));
    }

    /**
     * @return The Instance of the PacketSender
     */
    static PacketSender getInstance() {
        return instance;
    }

    private void sendPacket(Object nmsPacket, Object playerConnection) {
        ReflectionUtil.invokeMethod(SEND_PACKET, playerConnection, nmsPacket);
    }

    /**
     * Returns the Player's PlayerConnection
     *
     * @param player The Player to get the Connection for
     *
     * @return The Player's connection
     */
    Object getConnection(Player player) {
        Object handle = ReflectionUtil.invokeMethod(GET_HANDLE, player)
                .getValueOrThrow("Couldn't get the handle");

        return ReflectionUtil.getFieldValue(PLAYER_CONNECTION_FIELD, handle)
                .getValueOrThrow("Couldn't obtain PlayerConnection");
    }
}
