package com.perceivedev.perceivecore.gui.anvil;

import java.lang.reflect.Field;

import org.bukkit.inventory.InventoryHolder;

import com.perceivedev.perceivecore.packet.PacketAdapter;
import com.perceivedev.perceivecore.packet.PacketEvent;
import com.perceivedev.perceivecore.packet.PacketUtilities;
import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.reflection.ReflectionUtil.FieldPredicate;

import io.netty.buffer.ByteBuf;

import static com.perceivedev.perceivecore.reflection.ReflectionUtil.NameSpace.NMS;

/**
 * A PacketListener for the PacketPlayInPayload for anvil item renames
 */
class AnvilItemRenameListener extends PacketAdapter {
    private static final Class<?> PACKET_PLAY_IN_CUSTOM_PAYLOAD_CLASS = ReflectionUtil.getClass(
            NMS,
            "PacketPlayInCustomPayload"
    ).orElseThrow(() -> new RuntimeException("Could not find the 'PacketPlayInCustomPayload' class!"));

    @Override
    public void onPacketReceived(PacketEvent packetEvent) {
        if (packetEvent.getPacket().getPacketClass() != PACKET_PLAY_IN_CUSTOM_PAYLOAD_CLASS) {
            return;
        }
        PacketPlayInCustomPayloadWrapper customPayloadWrapper = new PacketPlayInCustomPayloadWrapper(
                packetEvent.getPacket().getNMSPacket()
        );

        String channel = customPayloadWrapper.getChannel();

        // we only want renames!
        if (!channel.equals("MC|ItemName")) {
            return;
        }

        InventoryHolder holder = packetEvent.getPlayer().getOpenInventory().getTopInventory().getHolder();
        if (!(holder instanceof AnvilInputHolder)) {
            return;
        }

        AnvilInputHolder anvilInputHolder = (AnvilInputHolder) holder;

        AnvilTypeEvent anvilTypeEvent = new AnvilTypeEvent(
                anvilInputHolder,
                customPayloadWrapper.getStringPayload()
        );
        anvilInputHolder.reactToTyping(anvilTypeEvent);
    }

    private static class PacketPlayInCustomPayloadWrapper {
        private static final Class<?> PACKET_DATA_SERIALIZER_CLASS = ReflectionUtil.getClass(
                NMS,
                "PacketDataSerializer"
        ).orElseThrow(() -> new RuntimeException("Could not find the 'PacketPlayInCustomPayload' class!"));
        private static final Field CHANNEL_FIELD = ReflectionUtil.getField(
                PACKET_PLAY_IN_CUSTOM_PAYLOAD_CLASS,
                new FieldPredicate(String.class)
        ).getValueOrThrow("Could not find the 'channel' field. I did not search by name, but type");
        private static final Field PACKET_DATA_SERIALIZER_FIELD = ReflectionUtil.getField(
                PACKET_PLAY_IN_CUSTOM_PAYLOAD_CLASS,
                new FieldPredicate(PACKET_DATA_SERIALIZER_CLASS)
        ).getValueOrThrow("Could not find the 'data serializer' field. I did not search by name, but type");


        private Object raw;

        /**
         * @param raw The NMS packet
         */
        PacketPlayInCustomPayloadWrapper(Object raw) {
            this.raw = raw;
        }

        /**
         * @return The channel
         */
        String getChannel() {
            return (String) ReflectionUtil.getFieldValue(CHANNEL_FIELD, raw)
                    .getValueOrThrow("Could not read 'channel' field!");
        }

        /**
         * Returns the payload for the channels "MC|ItemName" and "MC|Brand".
         *
         * @return The payload. Only works for packets with just a String as payload
         */
        String getStringPayload() {
            ByteBuf dataSerializer = (ByteBuf) ReflectionUtil.getFieldValue(PACKET_DATA_SERIALIZER_FIELD, raw)
                    .getValueOrThrow("Could not read 'data serializer' field!");

            dataSerializer = dataSerializer.copy();

            int readableBytes = (int) ReflectionUtil.invokeInstanceMethod(
                    dataSerializer,
                    "readableBytes",
                    new Class[0]
            ).getValueOrThrow("Could not invoke the 'readableBytes' method!");

            ByteBuf byteBuffer = (ByteBuf) ReflectionUtil.invokeInstanceMethod(
                    dataSerializer,
                    "readBytes",
                    new Class[]{int.class},
                    readableBytes
            ).getValueOrThrow("Could not invoke the 'readBytes' method!");

            return PacketUtilities.readString(Integer.MAX_VALUE / 4 - 1, byteBuffer);
        }
    }
}
