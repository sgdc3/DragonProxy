package org.dragonet.api;

import org.pf4j.PluginManager;
import org.slf4j.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
public interface Proxy {

    PluginManager getPluginManager();

    Logger getLogger();

}
