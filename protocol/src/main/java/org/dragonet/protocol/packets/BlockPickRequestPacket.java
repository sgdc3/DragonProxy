package org.dragonet.protocol.packets;

import org.dragonet.common.maths.BlockPosition;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class BlockPickRequestPacket extends PEPacket {

    public int x;
    public int y;
    public int z;
    public boolean addUserData;
    public int selectedSlot;

    public BlockPickRequestPacket() {
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.BLOCK_PICK_REQUEST_PACKET;
    }

    @Override
    public void decodePayload() {
        BlockPosition v = this.getSignedBlockPosition();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.putBoolean(this.addUserData);
        this.selectedSlot = this.getByte();
    }

    @Override
    public void encodePayload() {
    }
}
