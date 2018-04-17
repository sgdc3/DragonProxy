package org.dragonet.protocol.packets;

import org.dragonet.common.data.inventory.Slot;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class InventoryContentPacket extends PEPacket {

    public int windowId;
    public Slot[] items;

    public InventoryContentPacket() {
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.INVENTORY_CONTENT_PACKET;
    }

    @Override
    public void encodePayload() {
        putUnsignedVarInt(windowId);
        if (items != null && items.length > 0) {
            putUnsignedVarInt(items.length);
            for (Slot s : items) {
                putSlot(s);
            }
        } else {
            putUnsignedVarInt(0);
        }
    }

    @Override
    public void decodePayload() {
        windowId = (int) getUnsignedVarInt();
        int count = (int) getUnsignedVarInt();
        items = new Slot[count];
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                items[i] = getSlot();
            }
        }
    }
}
