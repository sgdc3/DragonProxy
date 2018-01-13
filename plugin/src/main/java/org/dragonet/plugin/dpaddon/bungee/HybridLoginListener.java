package org.dragonet.plugin.dpaddon.bungee;

import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.dragonet.plugin.dpaddon.DPAddonBungee;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HybridLoginListener implements Listener {

    private final DPAddonBungee plugin;

    private final Set<String> ips;

    public HybridLoginListener(DPAddonBungee plugin) {
        this.plugin = plugin;

        ips = new HashSet<>(plugin.getConfig().getStringList("hybrid-login-ips"));
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent e) {
        String addr = e.getConnection().getAddress().getAddress().getHostAddress();
        plugin.getLogger().info("Connection from " + addr);
        if(ips.contains(addr)) {
            plugin.getLogger().info("Detected DragonProxy connection! ");
            plugin.getLogger().info("NAME = " + e.getConnection().getName());
            e.getConnection().setOnlineMode(false);
            // e.getConnection().setUniqueId(new UUID(0L, 0xFF112233L));
        }
    }

    @EventHandler
    public void post(PlayerHandshakeEvent e) {
        plugin.getLogger().info("HANDSHAKE HOST=" + e.getHandshake().getHost());
        //plugin.getLogger().info("UUID = " + e.getPlayer().getUniqueId().toString());
    }
}
