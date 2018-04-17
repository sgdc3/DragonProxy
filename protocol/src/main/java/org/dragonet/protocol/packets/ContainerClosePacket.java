package org.dragonet.protocol.packets;

import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ContainerClosePacket extends PEPacket {

    public int windowId;

    public ContainerClosePacket() {
    }

    public ContainerClosePacket(int windowId) {
        this.windowId = windowId;
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.CONTAINER_CLOSE_PACKET;
    }

    @Override
    public void encodePayload() {
        putByte((byte) (windowId & 0xFF));
    }

    @Override
    public void decodePayload() {
        windowId = getByte();
    }
}
