package org.dragonet.protocol.packets;

import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class BlockEventPacket extends PEPacket {

    public int x;
    public int y;
    public int z;
    public int case1;
    public int case2;

    public BlockEventPacket(int x, int y, int z, int case1, int case2) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.case1 = case1;
        this.case2 = case2;
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.BLOCK_EVENT_PACKET;
    }

    @Override
    public void encodePayload() {
        this.reset();
        this.putBlockPosition(this.x, this.y, this.z);
        this.putVarInt(this.case1);
        this.putVarInt(this.case2);
    }

    @Override
    public void decodePayload() {
    }
}
