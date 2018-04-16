package org.dragonet.api.event.builtin.player;

import org.dragonet.api.event.HandlerList;
import org.dragonet.api.network.UpstreamSession;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PlayerLoginEvent extends PlayerEvent {

    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public PlayerLoginEvent(UpstreamSession session) {
        super(session);
    }

}
