package org.dragonet.protocol.packets;

import org.dragonet.common.maths.BlockPosition;
import org.dragonet.common.maths.Vector3F;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ExplodePacket extends PEPacket {

    public Vector3F position;
    public float radius;
    public List<BlockPosition> destroyedBlocks;

    public ExplodePacket() {
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.EXPLODE_PACKET;
    }

    @Override
    public void encodePayload() {
        putVector3F(this.position);
        putFloat(this.radius);
        putUnsignedVarInt(this.destroyedBlocks.size());

        for (BlockPosition position : this.destroyedBlocks)
            putBlockPosition(position);
    }

    @Override
    public void decodePayload() {
        this.position = getVector3F();
        this.radius = getFloat();

        int entries = (int) getUnsignedVarInt();
        this.destroyedBlocks = new ArrayList<>(entries);

        for (int i = 0; i < entries; i++) {
            this.destroyedBlocks.add(getBlockPosition());
        }
    }
}
