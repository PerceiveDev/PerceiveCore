package com.perceivedev.perceivecore.packet;

/**
 * A packet event
 */
public class PacketEvent {

    private Packet              packet;
    private boolean             cancelled;
    private ConnectionDirection direction;

    /**
     * @param packet The packet
     * @param cancelled Whether the event is cancelled
     * @param direction The direction the packet is travelling
     *
     * @throws IllegalStateException if it couldn't find the NMS base class "Packet" (You are screwed)
     * @throws IllegalArgumentException if 'object' isn't a packet.
     */
    protected PacketEvent(Object packet, boolean cancelled, ConnectionDirection direction) {
        this.packet = Packet.createFromObject(packet);
        this.cancelled = cancelled;
        this.direction = direction;
    }

    /**
     * This is not cancelled
     *
     * @param packet The packet
     * @param direction The direction the packet is travelling
     *
     * @see #PacketEvent(Object, boolean, ConnectionDirection)
     */
    protected PacketEvent(Object packet, ConnectionDirection direction) {
        this(packet, false, direction);
    }

    /**
     * Returns the packet
     *
     * @return The Packet
     */
    public Packet getPacket() {
        return packet;
    }

    /**
     * Checks if the event is cancelled
     *
     * @return True if the event is cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the events cancelled status.
     *
     * @param cancelled if true, the event will be cancelled.
     */
    public void setCancelled(boolean cancelled) {
        // should even be atomic
        this.cancelled = cancelled;
    }

    /**
     * Returns the connection direction
     *
     * @return The Direction the packet was travelling
     */
    public ConnectionDirection getDirection() {
        return direction;
    }

    /**
     * The direction the packet was travelling
     */
    public enum ConnectionDirection {
        TO_CLIENT,
        TO_SERVER
    }
}
