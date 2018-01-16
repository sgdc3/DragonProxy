package org.dragonet.plugin.dpaddon.bungee;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.dragonet.plugin.dpaddon.DPAddonBungee;

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
    public void onHandshake(PlayerHandshakeEvent event) {
        plugin.getLogger().info("HANDSHAKE HOST=" + event.getHandshake().getHost());
        String addr = event.getConnection().getAddress().getAddress().getHostAddress();
        if (!ips.contains(addr) && !event.getHandshake().getHost().contains(":"))
            return;
        String[] args = event.getHandshake().getHost().split(":");
        String xuid = args[0];
        String host = args[1];
        event.getHandshake().setHost(host);
        plugin.getLogger().info("Detected DragonProxy connection! XUID: " + xuid);
        verifiedPlayers.put(event.getConnection(), xuid);
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        if (verifiedPlayers.containsKey(event.getConnection())) {
            event.getConnection().setOnlineMode(false);
            event.getConnection().setUniqueId(UUID.nameUUIDFromBytes(
                    ("BedrockPlayer:XUID:" + verifiedPlayers.get(event.getConnection())).getBytes(StandardCharsets.UTF_8)
            ));
            verifiedPlayers.remove(event.getConnection());
        }
    }
}
