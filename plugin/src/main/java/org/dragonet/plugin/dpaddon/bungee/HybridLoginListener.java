package org.dragonet.plugin.dpaddon.bungee;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.dragonet.common.utilities.ReflectionUtils;
import org.dragonet.plugin.dpaddon.DPAddonBungee;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class HybridLoginListener implements Listener {

    private final DPAddonBungee plugin;

    private final Set<String> ips;

    private final Map<PendingConnection, String> verifiedPlayers = new HashMap<>();

    public HybridLoginListener(DPAddonBungee plugin) {
        this.plugin = plugin;

        ips = new HashSet<>(plugin.getConfig().getStringList("hybrid-login.proxy-ips"));
    }

    @EventHandler
    public void onHandshake(PlayerHandshakeEvent e) {
        plugin.getLogger().info("HANDSHAKE HOST=" + e.getHandshake().getHost());
        String addr = e.getConnection().getAddress().getAddress().getHostAddress();
        if (!ips.contains(addr)) return;
        String[] args = e.getHandshake().getHost().split(":");
        String xuid = args[0];
        String host = args[1];
        e.getHandshake().setHost(host);
        plugin.getLogger().info("Detected DragonProxy connection! XUID: " + xuid);
        //e.getConnection().setOnlineMode(false);
        verifiedPlayers.put(e.getConnection(), xuid);
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent e) {
        if(verifiedPlayers.containsKey(e.getConnection())) {
            e.getConnection().setOnlineMode(false);
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        if(verifiedPlayers.containsKey(e.getPlayer().getPendingConnection())) {
            // MCBE
            updateBungeeCordPlayerInfo(e.getPlayer().getPendingConnection(), e.getPlayer().getPendingConnection().getName() + "B",
                UUID.nameUUIDFromBytes(("BedrockPlayer:XUID:" + verifiedPlayers.get(e.getPlayer().getPendingConnection())).getBytes(StandardCharsets.UTF_8)));
        } else {
            // MCJE
            updateBungeeCordPlayerInfo(e.getPlayer().getPendingConnection(), e.getPlayer().getPendingConnection().getName() + "J", e.getPlayer().getUniqueId());
        }
    }

    private boolean updateBungeeCordPlayerInfo(PendingConnection connection, String username, UUID uuid) {
        try {
            Object loginRequest = ReflectionUtils.getFieldValue(connection, "loginRequest");
            ReflectionUtils.setFieldValue(loginRequest, "data", username);
            ReflectionUtils.setFieldValue(connection, "name", username);
            ReflectionUtils.setFieldValue(connection, "uniqueId", uuid);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
