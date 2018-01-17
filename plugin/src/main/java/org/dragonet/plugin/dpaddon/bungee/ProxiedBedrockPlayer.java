package org.dragonet.plugin.dpaddon.bungee;

import net.md_5.bungee.api.connection.PendingConnection;
import org.dragonet.plugin.dpaddon.DPAddonBungee;

public class ProxiedBedrockPlayer {

    private final DPAddonBungee plugin;
    private final PendingConnection connection;
    private final String xuid;

    public ProxiedBedrockPlayer(DPAddonBungee plugin, PendingConnection connection, String xuid) {
        this.plugin = plugin;
        this.connection = connection;
        this.xuid = xuid;
    }

    public PendingConnection getConnection() {
        return connection;
    }

    public String getUsername() {
        return connection.getName();
    }

    public String getXUID() {
        return xuid;
    }
}
