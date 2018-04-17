package org.dragonet.protocol.packets;

import org.dragonet.common.maths.BlockPosition;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ContainerOpenPacket extends PEPacket {

    public int windowId;
    public int type;
    public BlockPosition position;
    public long eid = -1;

    public ContainerOpenPacket() {
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.CONTAINER_OPEN_PACKET;
    }

    @Override
    public void encodePayload() {
        putByte((byte) (windowId));
        putByte((byte) (type));
        putBlockPosition(position);
        putVarLong(eid);
    }

    @Override
    public void decodePayload() {
        windowId = getByte();
        type = getByte();
        position = getBlockPosition();
        eid = getVarLong();
    }
}
