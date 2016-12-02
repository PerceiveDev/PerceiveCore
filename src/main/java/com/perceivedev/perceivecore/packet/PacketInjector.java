package com.perceivedev.perceivecore.packet;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.$;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.packet.PacketEvent.ConnectionDirection;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/** A simple packet injector, to modify the packets sent and received */
class PacketInjector extends ChannelDuplexHandler {

    private boolean isClosed;
    private Channel channel;
    private List<PacketListener> packetListeners = new ArrayList<>();
    private WeakReference<Player> playerWeakReference;

    /**
     * Must be detached manually!
     *
     * @param player The player to attach into
     */
    PacketInjector(Player player) {
        attach(player);
        playerWeakReference = new WeakReference<>(player);
    }

    /**
     * Attaches to a player
     *
     * @param player The player to attach to
     */
    private void attach(Player player) {

        // Lengthy way of doing: ( (CraftPlayer) handle
        // ).getHandle().playerConnection.networkManager.channel

        ReflectResponse<Object> playerConnectionResponse = $(player).getHandle().getField("playerConnection").get();
        if (!playerConnectionResponse.isValuePresent()) {
            throw new NullPointerException("Couldn't find playerConnection field");
        }
        Object playerConnection = playerConnectionResponse.getValue();

        ReflectResponse<Object> networkManager = $(playerConnection).getField("networkManager").get();
        if (!networkManager.isValuePresent()) {
            throw new NullPointerException("Couldn't find networkManager field");
        }
        Object manager = networkManager.getValue();

        ReflectResponse<Object> channelResponse = $(manager).getField("channel").get();
        if (!channelResponse.isValuePresent()) {
            throw new NullPointerException("Couldn't find channel field");
        }
        channel = (Channel) channelResponse.getValue();

        // remove old listener, if it wasn't properly cleared up
        if (channel.pipeline().get("perceiveHandler") != null) {
            // remove old
            channel.pipeline().remove("perceiveHandler");
        }

        channel.pipeline().addBefore("packet_handler", "perceiveHandler", this);
    }

    /** Removes this handler */
    void detach() {
        if (isClosed || !channel.isOpen()) {
            return;
        }
        isClosed = true;
        channel.eventLoop().submit(() -> channel.pipeline().remove(this));

        // clear references. Probably not needed, but I am not sure about the
        // channel.
        playerWeakReference.clear();
        packetListeners.clear();
        channel = null;
    }

    /**
     * Checks if this handler is closed
     *
     * @return True if the handler is closed
     */
    boolean isClosed() {
        return isClosed;
    }

    /**
     * Adds a {@link PacketListener}
     *
     * @param packetListener The {@link PacketListener} to add
     *
     * @throws IllegalStateException if the channel is already closed
     */
    void addPacketListener(PacketListener packetListener) {
        Objects.requireNonNull(packetListener, "packetListener can not be null");
        if (isClosed()) {
            throw new IllegalStateException("Channel already closed. Adding of listener invalid");
        }
        packetListeners.add(packetListener);
    }

    /**
     * Removes a {@link PacketListener}
     *
     * @param packetListener The {@link PacketListener} to remove
     */
    void removePacketListener(PacketListener packetListener) {
        packetListeners.remove(packetListener);
    }

    /**
     * Returns the amount of listeners
     *
     * @return The amount of listeners
     */
    int getListenerAmount() {
        return packetListeners.size();
    }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
        PacketEvent event = new PacketEvent(packet, ConnectionDirection.TO_CLIENT, playerWeakReference.get());

        for (PacketListener packetListener : packetListeners) {
            try {
                packetListener.onPacketSend(event);
            } catch (Exception e) {
                PerceiveCore.getInstance().getLogger().log(Level.WARNING,
                        "Error in a Packet Listener (send). This is not the fault of PerceiveCore!", e);
            }
        }

        // let it through
        if (!event.isCancelled()) {
            super.write(channelHandlerContext, packet, channelPromise);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
        PacketEvent event = new PacketEvent(packet, ConnectionDirection.TO_SERVER, playerWeakReference.get());

        for (PacketListener packetListener : packetListeners) {
            try {
                packetListener.onPacketReceived(event);
            } catch (Exception e) {
                PerceiveCore.getInstance().getLogger().log(Level.WARNING,
                        "Error in a Packet Listener (receive). This is not the fault of PerceiveCore!", e);
            }
        }

        // let it through
        if (!event.isCancelled()) {
            super.channelRead(channelHandlerContext, packet);
        }
    }
}
