package org.dragonet.protocol.packets;

import org.dragonet.common.maths.BlockPosition;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

/**
 * Created on 2017/10/21.
 */
public class PlaySoundPacket extends PEPacket {

    public String name;
    public BlockPosition blockPosition;
    public float volume;
    public float pitch;

    public PlaySoundPacket() {

    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.PLAY_SOUND_PACKET;
    }

    @Override
    public void encodePayload() {
        putString(name);
        putBlockPosition(new BlockPosition(blockPosition.x * 8, blockPosition.y * 8, blockPosition.z * 8));
        putLFloat(volume);
        putLFloat(pitch);
    }

    @Override
    public void decodePayload() {
        name = getString();
        blockPosition = getBlockPosition();
        blockPosition.x /= 8;
        blockPosition.y /= 8;
        blockPosition.z /= 8;
        volume = getLFloat();
        pitch = getLFloat();
    }
}
