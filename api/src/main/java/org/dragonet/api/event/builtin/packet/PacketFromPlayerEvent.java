package org.dragonet.api.event.builtin.packet;

import org.dragonet.api.event.Cancellable;
import org.dragonet.api.event.HandlerList;
import org.dragonet.api.network.UpstreamSession;
import org.dragonet.protocol.PEPacket;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PacketFromPlayerEvent extends PEPacketEvent implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    private final UpstreamSession session;

    public PacketFromPlayerEvent(UpstreamSession session, PEPacket packet) {
        super(packet);
        this.session = session;
    }

    public UpstreamSession getSession() {
        return session;
    }

    private boolean cancelled = false;

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public boolean isCancelled() {
        return cancelled;
    }

}
