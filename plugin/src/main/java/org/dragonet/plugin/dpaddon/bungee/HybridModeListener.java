package org.dragonet.plugin.dpaddon.bungee;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import org.dragonet.common.utilities.BinaryStream;
import org.dragonet.common.utilities.HybridAuth;
import org.dragonet.common.utilities.ReflectionUtils;
import org.dragonet.plugin.dpaddon.DPAddonBungee;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HybridModeListener implements Listener {

    private final DPAddonBungee plugin;

    private final Set<String> ips;

    private final File mappingsDir;

    private final Map<PendingConnection, ProxiedBedrockPlayer> verifiedPlayers = new HashMap<>();

    public HybridModeListener(DPAddonBungee plugin) {
        this.plugin = plugin;

        mappingsDir = new File(plugin.getDataFolder(), "mappings");

        ips = new HashSet<>(plugin.getConfig().getStringList("hybrid-login.proxy-ips"));
    }

    @EventHandler
    public void onHandshake(PlayerHandshakeEvent event) {
        if (!ips.contains(event.getConnection().getAddress().getAddress().getHostAddress()))
            return;
        if (BungeeCord.InitialHandler.isAssignableFrom(event.getConnection().getClass())) {
            try {
                plugin.getLogger().info("HANDSHAKE HOST=" + event.getHandshake().getHost());
                Object handler = event.getConnection();
                String xuid = ((String) ReflectionUtils.invoke(handler, "getExtraDataInHandshake", new Class[0], new Object[0])).replace("\0", "");
                plugin.getLogger().info("Detected DragonProxy connection! XUID: " + xuid);
                verifiedPlayers.put(event.getConnection(), new ProxiedBedrockPlayer(plugin, event.getConnection(), xuid, HybridAuth.getMappingForBedrock(xuid)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        if (verifiedPlayers.containsKey(event.getConnection())) {
            // set to offline mode before authenticating
            event.getConnection().setOnlineMode(false);
            event.getConnection().setUniqueId(UUID.nameUUIDFromBytes("NotAuthenticated".getBytes(StandardCharsets.UTF_8)));
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        if (verifiedPlayers.containsKey(event.getPlayer().getPendingConnection())) {
            /* ==== Minecraft: Bedrock Edition players ==== */

            PendingConnection connection = event.getPlayer().getPendingConnection();
            ProxiedBedrockPlayer bedrockPlayer = verifiedPlayers.get(connection);

            if(bedrockPlayer.isMapped()) {
                if(!updateBungeeCordPlayerInfo(bedrockPlayer.getConnection(), bedrockPlayer.getHybridAccountInfo())) {
                    event.getPlayer().disconnect(new TextComponent("Server error! (failed to update hybrid account mapping)"));
                    return;
                }
            } else {
                // send to authentication server
                event.getPlayer().disconnect(new TextComponent("Error! Please create your account via DragonProxy! "));
                return;
            }
        } else {
            /* ==== Minecraft: Java Edition players ==== */

            UUID originalUniqueId = event.getPlayer().getUniqueId();
            JSONObject hybridAccountInfo = HybridAuth.getMappingForJava(originalUniqueId);
            if(hybridAccountInfo != null) {
                if(!updateBungeeCordPlayerInfo(event.getPlayer().getPendingConnection(), hybridAccountInfo)) {
                    event.getPlayer().disconnect(new TextComponent("Server error! (failed to update hybrid account mapping)"));
                    return;
                }
            } else {
                // send to authentication server
                event.getPlayer().setReconnectServer(plugin.getProxy().getServerInfo(plugin.getConfig().getString("hybrid-login.java-edition-auth-server")));
            }
        }
    }

    private boolean updateBungeeCordPlayerInfo(PendingConnection connection, JSONObject hybridAccountInfo) {
        try {
            Object loginRequest = ReflectionUtils.getFieldValue(connection, "loginRequest");
            String username = hybridAccountInfo.getString("username");
            ReflectionUtils.setFieldValue(loginRequest, "data", username);
            ReflectionUtils.setFieldValue(connection, "name", username);
            ReflectionUtils.setFieldValue(connection, "uniqueId", UUID.fromString(hybridAccountInfo.getString("uuid")));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @EventHandler
    public void onLogout(PlayerDisconnectEvent e) {
        verifiedPlayers.remove(e.getPlayer().getPendingConnection());
    }


}
