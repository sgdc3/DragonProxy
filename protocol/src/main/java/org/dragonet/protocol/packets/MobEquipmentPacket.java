package org.dragonet.protocol.packets;

import org.dragonet.common.data.inventory.Slot;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

/**
 * Created on 2017/10/22.
 */
public class MobEquipmentPacket extends PEPacket {

    public long rtid;
    public Slot item;
    public int inventorySlot;
    public int hotbarSlot;
    public int windowId;

    @Override
    public int getPacketId() {
        return ProtocolInfo.MOB_EQUIPMENT_PACKET;
    }

    @Override
    public void encodePayload() {
        putUnsignedVarLong(rtid);
        putSlot(item);
        putByte((byte) (inventorySlot & 0xFF));
        putByte((byte) (hotbarSlot & 0xFF));
        putByte((byte) (windowId & 0xFF));
    }

    @Override
    public void decodePayload() {
        rtid = getUnsignedVarLong();
        item = getSlot();
        inventorySlot = getByte();
        hotbarSlot = getByte();
        windowId = getByte();
    }
}
