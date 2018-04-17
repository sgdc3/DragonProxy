package org.dragonet.protocol.packets;

import org.dragonet.common.data.entity.meta.EntityMetaData;
import org.dragonet.common.data.inventory.Slot;
import org.dragonet.common.maths.Vector3F;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class AddItemEntityPacket extends PEPacket {

    public long rtid;
    public long eid;
    public Slot item;
    public Vector3F position;
    public Vector3F motion;
    public EntityMetaData metadata;
    public boolean isFromFishing;

    public AddItemEntityPacket(long rtid, long eid, Slot item, Vector3F position, Vector3F motion,
                               EntityMetaData metadata, boolean isFromFishing) {
        this.rtid = rtid;
        this.eid = eid;
        this.item = item;
        this.position = position;
        this.motion = motion;
        this.metadata = metadata;
        this.isFromFishing = isFromFishing;
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.ADD_ITEM_ENTITY_PACKET;
    }

    @Override
    public void encodePayload() {
        putVarLong(rtid);
        putUnsignedVarLong(eid);
        putSlot(item);
        putVector3F(position);
        putVector3F(motion);
        if (metadata != null) {
            metadata.encode();
            put(metadata.getBuffer());
        } else {
            putUnsignedVarInt(0);
        }
        putBoolean(isFromFishing);
    }

    @Override
    public void decodePayload() {
        rtid = getVarLong();
        eid = getUnsignedVarLong();
        item = getSlot();
        position = getVector3F();
        motion = getVector3F();
        metadata = EntityMetaData.from(this);
        isFromFishing = getBoolean();
    }
}
