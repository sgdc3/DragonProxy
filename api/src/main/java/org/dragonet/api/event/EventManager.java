package org.dragonet.api.event;

import org.pf4j.Plugin;

@SuppressWarnings({"unused", "WeakerAccess"})
public interface EventManager {

    void registerEvents(Plugin plugin, Listener listener);

    void unregisterEvents(Listener listener);

    void unregisterEvents(Plugin plugin);

    void callEvent(Event event);

}
