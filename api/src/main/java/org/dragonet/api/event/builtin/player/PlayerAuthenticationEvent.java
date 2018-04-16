package org.dragonet.api.event.builtin.player;

import org.dragonet.api.event.Cancellable;
import org.dragonet.api.event.HandlerList;
import org.dragonet.api.network.UpstreamSession;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PlayerAuthenticationEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public PlayerAuthenticationEvent(UpstreamSession session) {
        super(session);
    }

    private boolean cancelled = false;

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public boolean isCancelled() {
        return cancelled;
    }

}
