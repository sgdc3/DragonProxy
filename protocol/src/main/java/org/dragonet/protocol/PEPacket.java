package org.dragonet.protocol;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class PEPacket extends PEBinaryStream {

    private boolean encoded;
    private boolean decoded;

    public PEPacket() {
        super();
    }

    public boolean isEncoded() {
        return encoded;
    }

    public boolean isDecoded() {
        return decoded;
    }

    public void encode() {
        reset();
        encodeHeader();
        encodePayload();
        encoded = true;
    }

    public void decode() {
        decodeHeader();
        decodePayload();
        decoded = true;
    }

    public void encodeHeader() {
        // putUnsignedVarInt(getPacketId());
        putByte((byte) (getPacketId() & 0xFF));
        putByte((byte) 0x00);
        putByte((byte) 0x00);
    }

    public void decodeHeader() {
        getByte(); // getUnsignedVarInt();
        get(2);
    }

    public abstract int getPacketId();

    public abstract void encodePayload();

    public abstract void decodePayload();

}
