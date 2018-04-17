package org.dragonet.protocol.packets;

import org.dragonet.common.data.inventory.Slot;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

/**
 * Created on 2017/10/21.
 */
public class InventorySlotPacket extends PEPacket {

    public int windowId;
    public int slotId;
    public Slot item;

    public InventorySlotPacket() {

    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.INVENTORY_SLOT_PACKET;
    }

    @Override
    public void encodePayload() {
        putUnsignedVarInt(windowId);
        putUnsignedVarInt(slotId);
        putSlot(item);
    }

    @Override
    public void decodePayload() {
        windowId = (int) getUnsignedVarInt();
        slotId = (int) getUnsignedVarInt();
        item = getSlot();
    }
}
