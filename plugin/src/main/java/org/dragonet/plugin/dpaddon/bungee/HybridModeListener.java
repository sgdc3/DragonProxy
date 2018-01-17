package org.dragonet.plugin.dpaddon.bungee;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import org.dragonet.common.utilities.BinaryStream;
import org.dragonet.common.utilities.ReflectionUtils;
import org.dragonet.plugin.dpaddon.DPAddonBungee;

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
                verifiedPlayers.put(event.getConnection(), new ProxiedBedrockPlayer(plugin, event.getConnection(), xuid));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        if (verifiedPlayers.containsKey(event.getConnection())) {
            ProxiedBedrockPlayer bedrockPlayer = verifiedPlayers.get(event.getConnection());
            event.getConnection().setOnlineMode(false);
            event.getConnection().setUniqueId(UUID.nameUUIDFromBytes(
                ("BedrockPlayer:XUID:" + bedrockPlayer.getXUID()).getBytes(StandardCharsets.UTF_8)
            ));
            // verifiedPlayers.remove(event.getConnection());

            String username_mapping = getMapping(bedrockPlayer.getXUID());
            if(username_mapping == null) {
                username_mapping = "XUID_" + bedrockPlayer;
            }
            try {
                Object loginRequest = ReflectionUtils.getFieldValue(event.getConnection(), "loginRequest");
                ReflectionUtils.setFieldValue(loginRequest, "data", username_mapping);
                ReflectionUtils.setFieldValue(event.getConnection(), "name", username_mapping);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onLogout(PlayerDisconnectEvent e) {
        verifiedPlayers.remove(e.getPlayer().getPendingConnection());
    }

    public String getMapping(String xuid) {
        File mappingFile = new File(mappingsDir, xuid + ".mapping");
        if (!mappingFile.exists()) return null;
        try {
            Configuration conf = ConfigurationProvider.getProvider(YamlConfiguration.class).load(mappingFile);
            return conf.getString("username");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setMapping(String xuid, String username) throws Exception {
        File mappingFile = new File(mappingsDir, xuid + ".mapping");
        if (!mappingFile.exists()) mappingFile.delete();
        Configuration conf = new Configuration();
        conf.set("username", username);
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(conf, mappingFile);
    }
}
