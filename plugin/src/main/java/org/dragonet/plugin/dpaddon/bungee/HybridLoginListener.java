package org.dragonet.plugin.dpaddon.bungee;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.dragonet.common.utilities.ReflectionUtils;
import org.dragonet.plugin.dpaddon.DPAddonBungee;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HybridLoginListener implements Listener {

    private final DPAddonBungee plugin;

    private final Set<String> ips;

    private final Map<InetSocketAddress, String> verifiedPlayers = new HashMap<>();

    public HybridLoginListener(DPAddonBungee plugin) {
        this.plugin = plugin;

        ips = new HashSet<>(plugin.getConfig().getStringList("hybrid-login.proxy-ips"));
    }

    @EventHandler
    public void onHandshake(PlayerHandshakeEvent e) {
        String addr = e.getConnection().getAddress().getAddress().getHostAddress();
        System.out.println("ADDR = " + addr);
        if (!ips.contains(addr)) return;
        if(BungeeCord.InitialHandler.isAssignableFrom(e.getConnection().getClass())) {
            try {
                plugin.getLogger().info("HANDSHAKE HOST=" + e.getHandshake().getHost());
                Object handler = e.getConnection();
                String xuid = ((String) ReflectionUtils.invoke(handler, "getExtraDataInHandshake", new Class[0], new Object[0])).replace("\0", "");
                plugin.getLogger().info("Detected DragonProxy connection! XUID: " + xuid);
                verifiedPlayers.put(e.getConnection().getAddress(), xuid);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent e) {
        if(verifiedPlayers.containsKey(e.getConnection().getAddress())) {
            e.getConnection().setOnlineMode(false);
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        boolean success;
        if(verifiedPlayers.containsKey(e.getPlayer().getPendingConnection().getAddress())) {
            // MCBE
            success = updateBungeeCordPlayerInfo(e.getPlayer().getPendingConnection(), e.getPlayer().getName() + "B",
                UUID.nameUUIDFromBytes(("BedrockPlayer:XUID:" + verifiedPlayers.get(e.getPlayer().getPendingConnection().getAddress())).getBytes(StandardCharsets.UTF_8)));
            verifiedPlayers.remove(e.getPlayer().getPendingConnection().getAddress());
        } else {
            // MCJE
            success = updateBungeeCordPlayerInfo(e.getPlayer().getPendingConnection(), e.getPlayer().getName() + "J", e.getPlayer().getUniqueId());
        }
        if(!success) {
            e.getPlayer().disconnect(new TextComponent("Failed to apply hybrid authentications! "));
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
