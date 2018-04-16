package org.dragonet.api.event;

@SuppressWarnings({"unused", "WeakerAccess"})
public interface Cancellable {

    void setCancelled(boolean cancel);

    boolean isCancelled();

}
