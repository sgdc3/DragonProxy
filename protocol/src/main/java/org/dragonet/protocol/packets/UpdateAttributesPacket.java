package org.dragonet.protocol.packets;

import org.dragonet.common.data.entity.PEEntityAttribute;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

import java.util.Collection;

@SuppressWarnings({"unused", "WeakerAccess"})
public class UpdateAttributesPacket extends PEPacket {

    public long rtid;
    public Collection<PEEntityAttribute> entries;

    public UpdateAttributesPacket() {
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.UPDATE_ATTRIBUTES_PACKET;
    }

    @Override
    public void encodePayload() {
        putUnsignedVarLong(rtid);
        if (entries != null && entries.size() > 0) {
            putUnsignedVarInt(entries.size());
            for (PEEntityAttribute attribute : entries) {
                putLFloat(attribute.min);
                putLFloat(attribute.max);
                putLFloat(attribute.currentValue);
                putLFloat(attribute.defaultValue);
                putString(attribute.name);
            }
        } else {
            putUnsignedVarInt(0);
        }
    }

    @Override
    public void decodePayload() {
    }
}
