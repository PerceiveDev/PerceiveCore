package com.perceivedev.perceivecore.packet;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

/**
 * Utilities to ease the working with packets
 */
public class PacketUtilities {

    /**
     * Reads a String
     * <p>
     * Code taken from Mojang's PacketDataSerializer
     *
     * @param maxLength The maximum length of the String. Will be multiplied by 4 to get the bytes, so make sure
     * that
     * number fits into an integer too!
     * @param byteBuf The ByteBuf to read from
     *
     * @return The read String
     */
    @SuppressWarnings("unused")
    public static String readString(int maxLength, ByteBuf byteBuf) {
        int stringByteLength = readVarInt(byteBuf);
        if (stringByteLength > maxLength * 4) {
            throw new DecoderException(
                    "The received encoded string buffer length is longer than maximum allowed (" +
                            stringByteLength + " > " + maxLength * 4 + ")"
            );
        }
        else if (stringByteLength < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird " +
                    "string!");
        }
        else {
            String string = new String(byteBuf.readBytes(stringByteLength).array(), Charsets.UTF_8);
            if (string.length() > maxLength) {
                throw new DecoderException(
                        "The received string length is longer than maximum allowed (" +
                                stringByteLength + " > " + maxLength + ")"
                );
            }
            else {
                return string;
            }
        }
    }

    /**
     * Reads an integer of variable length
     * <p>
     * Code taken from Mojang's PacketDataSerializer
     *
     * @param byteBuf The ByteBuf to read from
     *
     * @return The read integer
     */
    @SuppressWarnings("WeakerAccess")
    public static int readVarInt(ByteBuf byteBuf) {
        int i = 0;
        int j = 0;

        byte b0;
        do {
            b0 = byteBuf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }
}
