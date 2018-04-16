package org.dragonet.api.event.builtin.packet;

import org.dragonet.protocol.PEPacket;
import org.dragonet.api.event.Event;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class PEPacketEvent extends Event {

    private PEPacket packet;

    public PEPacketEvent(PEPacket packet) {
        this.packet = packet;
    }

    public PEPacket getPacket() {
        return packet;
    }

    public void setPacket(PEPacket packet) {
        this.packet = packet;
    }

}
