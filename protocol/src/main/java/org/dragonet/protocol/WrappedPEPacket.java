package org.dragonet.protocol;

/**
 * A wrapper for PEPacket.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class WrappedPEPacket extends PEPacket {

    public WrappedPEPacket(byte[] buffer) {
        setBuffer(buffer);
    }

    @Override
    public int getPacketId() {
        return 0;
    }

    @Override
    public void encode() {
    }

    @Override
    public void encodePayload() {
    }

    @Override
    public void decodePayload() {
    }

    @Override
    public boolean isEncoded() {
        return true;
    }

}
