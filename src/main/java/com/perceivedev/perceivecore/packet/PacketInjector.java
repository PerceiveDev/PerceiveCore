package com.perceivedev.perceivecore.packet;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.$;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.perceivedev.perceivecore.PerceiveCore;
import com.perceivedev.perceivecore.packet.PacketEvent.ConnectionDirection;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.ReflectResponse;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * A simple packet injector, to modify the packets sent and received
 */
public class PacketInjector extends ChannelDuplexHandler {

    private boolean isClosed;
    private Channel channel;
    private List<PacketListener> packetListeners = new ArrayList<>();

    /**
     * @param player The player to attach into
     */
    public PacketInjector(Player player) {
        attach(player);

        // Detach on onDisable
        PerceiveCore.getInstance().getDisableManager().addListener(this::detach);
    }

    /**
     * Attaches to a player
     *
     * @param player The player to attach to
     */
    private void attach(Player player) {

        // Lengthy way of doing: ( (CraftPlayer) handle ).getHandle().playerConnection.networkManager.channel

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

    /**
     * Removes this handler
     */
    public void detach() {
        if (isClosed || !channel.isOpen()) {
            return;
        }
        isClosed = true;
        getChannel().eventLoop().submit(() -> getChannel().pipeline().remove(this));
    }

    /**
     * Checks if this handler is closed
     *
     * @return True if the handler is closed
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Returns the channel
     *
     * @return The channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Adds a {@link PacketListener}
     *
     * @param packetListener The {@link PacketListener} to add
     */
    public void addPacketListener(PacketListener packetListener) {
        packetListeners.add(packetListener);
    }

    /**
     * Removes a {@link PacketListener}
     *
     * @param packetListener The {@link PacketListener} to remove
     */
    public void removePacketListener(PacketListener packetListener) {
        packetListeners.remove(packetListener);
    }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
        PacketEvent event = new PacketEvent(packet, ConnectionDirection.TO_CLIENT);

        packetListeners.forEach((packetListener) -> packetListener.onPacketSend(event));

        // let it through
        if (!event.isCancelled()) {
            super.write(channelHandlerContext, packet, channelPromise);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
        PacketEvent event = new PacketEvent(packet, ConnectionDirection.TO_SERVER);

        packetListeners.forEach(packetListener -> packetListener.onPacketReceived(event));

        // let it through
        if (!event.isCancelled()) {
            super.channelRead(channelHandlerContext, packet);
        }
    }
}
