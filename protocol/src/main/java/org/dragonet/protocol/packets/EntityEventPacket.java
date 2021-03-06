package org.dragonet.protocol.packets;

import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class EntityEventPacket extends PEPacket {

    public static final int HURT_ANIMATION = 2;
    public static final int DEATH_ANIMATION = 3;
    public static final int TAME_FAIL = 6;
    public static final int TAME_SUCCESS = 7;
    public static final int SHAKE_WET = 8;
    public static final int USE_ITEM = 9;
    public static final int EAT_GRASS_ANIMATION = 10;
    public static final int FISH_HOOK_BUBBLE = 11;
    public static final int FISH_HOOK_POSITION = 12;
    public static final int FISH_HOOK_HOOK = 13;
    public static final int FISH_HOOK_TEASE = 14;
    public static final int SQUID_INK_CLOUD = 15;
    public static final int AMBIENT_SOUND = 17;
    public static final int RESPAWN = 18;
    public static final int ENCHANT = 34;
    public static final byte EATING_ITEM = 57;
    public static final byte UNKNOWN1 = 66;

    public long eid;
    public int event;
    public int data;

    public EntityEventPacket() {
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.ENTITY_EVENT_PACKET;
    }

    @Override
    public void encodePayload() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.putByte((byte) this.event);
        this.putVarInt((byte) this.data);
    }

    @Override
    public void decodePayload() {
        this.eid = this.getEntityRuntimeId();
        this.event = this.getByte();
        this.data = this.getVarInt();
    }

}
