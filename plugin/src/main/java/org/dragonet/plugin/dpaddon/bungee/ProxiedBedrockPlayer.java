package org.dragonet.plugin.dpaddon.bungee;

import net.md_5.bungee.api.connection.PendingConnection;
import org.dragonet.plugin.dpaddon.DPAddonBungee;
import org.json.JSONObject;

public class ProxiedBedrockPlayer {

    private final DPAddonBungee plugin;
    private final PendingConnection connection;
    private final String xuid;

    private final JSONObject hybridAccountInfo;

    public ProxiedBedrockPlayer(DPAddonBungee plugin, PendingConnection connection, String xuid, JSONObject hybridAccountInfo) {
        this.plugin = plugin;
        this.connection = connection;
        this.xuid = xuid;
        this.hybridAccountInfo = hybridAccountInfo;
    }

    public PendingConnection getConnection() {
        return connection;
    }

    public String getXUID() {
        return xuid;
    }

    public boolean isMapped() {
        return hybridAccountInfo != null;
    }

    public JSONObject getHybridAccountInfo() {
        return hybridAccountInfo;
    }
}
