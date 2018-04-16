package org.dragonet.api.event;

import org.pf4j.Plugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class HandlerList {

    private List<WrappedListener> handlerList = new ArrayList<>();

    public void registerListener(WrappedListener listener) {
        handlerList.add(listener);
    }

    public void unregisterListener(Listener listener) {
        handlerList.removeIf(currentListener -> currentListener.getListener().equals(listener));
    }

    public void unregisterListener(Plugin plugin) {
        handlerList.removeIf(currentListener -> currentListener.getPlugin().equals(plugin));
    }

    public List<WrappedListener> getHandlerList() {
        return handlerList;
    }

}
