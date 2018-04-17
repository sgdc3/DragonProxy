package org.dragonet.protocol.packets;

import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ChunkRadiusUpdatedPacket extends PEPacket {

    public int radius;

    public ChunkRadiusUpdatedPacket() {
    }

    public ChunkRadiusUpdatedPacket(int radius) {
        this.radius = radius;
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET;
    }

    @Override
    public void encodePayload() {
        putVarInt(radius);
    }

    @Override
    public void decodePayload() {
        radius = getVarInt();
    }
}
