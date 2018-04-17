package org.dragonet.protocol.packets;

import org.dragonet.common.maths.BlockPosition;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class AddPaintingPacket extends PEPacket {

    public long eid;
    public long rtid;
    public BlockPosition pos;
    public int direction;
    public String title;

    public AddPaintingPacket(long eid, long rtid, BlockPosition pos, int direction, String title) {
        this.eid = eid;
        this.rtid = rtid;
        this.pos = pos;
        this.direction = direction;
        this.title = title;
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.ADD_PAINTING_PACKET;
    }

    @Override
    public void encodePayload() {
        this.reset();
        this.putEntityUniqueId(this.eid);
        this.putEntityRuntimeId(this.rtid);
        this.putBlockPosition(this.pos);
        this.putVarInt(this.direction);
        this.putString(this.title);
    }

    @Override
    public void decodePayload() {
    }
}
