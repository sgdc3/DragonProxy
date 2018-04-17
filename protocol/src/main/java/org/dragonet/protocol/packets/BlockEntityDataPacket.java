package org.dragonet.protocol.packets;

import org.dragonet.common.data.nbt.NBTIO;
import org.dragonet.common.data.nbt.tag.CompoundTag;
import org.dragonet.common.maths.BlockPosition;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

import java.nio.ByteOrder;

@SuppressWarnings({"unused", "WeakerAccess"})
public class BlockEntityDataPacket extends PEPacket {

    public BlockPosition blockPosition;
    public CompoundTag tag;

    public BlockEntityDataPacket(BlockPosition blockPosition, CompoundTag tag) {
        this.blockPosition = blockPosition;
        this.tag = tag;
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.BLOCK_ENTITY_DATA_PACKET;
    }

    @Override
    public void encodePayload() {
        putBlockPosition(blockPosition);
        if (tag != null) {
            byte[] bytes = new byte[]{};
            try {
                bytes = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            put(bytes);
        }
    }

    @Override
    public void decodePayload() {
        blockPosition = getBlockPosition();
        try {
            tag = (CompoundTag) NBTIO.read(get(), ByteOrder.LITTLE_ENDIAN, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
