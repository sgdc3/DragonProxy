package org.dragonet.protocol.packets;

import org.dragonet.common.maths.Vector3F;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

/**
 * Created on 2017/10/21.
 */
public class MoveEntityPacket extends PEPacket {

    public long rtid;
    public Vector3F position;
    public float yaw;
    public float headYaw;
    public float pitch;
    public boolean onGround;
    public boolean teleported;

    public MoveEntityPacket() {

    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.MOVE_ENTITY_PACKET;
    }

    @Override
    public void encodePayload() {
        putUnsignedVarLong(rtid);
        putVector3F(position);
        putByteRotation(pitch);
        putByteRotation(headYaw);
        putByteRotation(yaw);
        putBoolean(onGround);
        putBoolean(teleported);
    }

    @Override
    public void decodePayload() {
        rtid = getUnsignedVarLong();
        position = getVector3F();
        pitch = getByteRotation();
        headYaw = getByteRotation();
        yaw = getByteRotation();
        onGround = getBoolean();
        teleported = getBoolean();
    }
}
