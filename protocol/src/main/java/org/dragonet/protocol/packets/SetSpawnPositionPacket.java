package org.dragonet.protocol.packets;

import org.dragonet.common.maths.BlockPosition;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class SetSpawnPositionPacket extends PEPacket {

    public static final int TYPE_PLAYER_SPAWN = 0;
    public static final int TYPE_WORLD_SPAWN = 1;

    public int type;
    public BlockPosition position;
    public boolean forced;

    public SetSpawnPositionPacket() {
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.SET_SPAWN_POSITION_PACKET;
    }

    @Override
    public void encodePayload() {
        putVarInt(type);
        putBlockPosition(position);
        putBoolean(forced);
    }

    @Override
    public void decodePayload() {
        type = getVarInt();
        position = getBlockPosition();
        forced = getBoolean();
    }
}
