package org.dragonet.api.event.builtin.player;

import org.dragonet.api.event.Event;
import org.dragonet.api.network.UpstreamSession;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class PlayerEvent extends Event {

    private final UpstreamSession session;

    public PlayerEvent(UpstreamSession session) {
        this.session = session;
    }

    public UpstreamSession getSession() {
        return session;
    }

}
