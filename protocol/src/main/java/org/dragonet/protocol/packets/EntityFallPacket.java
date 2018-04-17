package org.dragonet.protocol.packets;

import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class EntityFallPacket extends PEPacket {

    public long rtid;
    public float fallDistance;
    public boolean unk1;

    public EntityFallPacket() {
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.ENTITY_FALL_PACKET;
    }

    @Override
    public void encodePayload() {
        putUnsignedVarLong(rtid);
        putFloat(fallDistance);
        putBoolean(unk1);
    }

    @Override
    public void decodePayload() {
        rtid = getUnsignedVarLong();
        fallDistance = getFloat();
        unk1 = getBoolean();
    }
}
