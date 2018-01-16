/*
 * GNU LESSER GENERAL PUBLIC LICENSE
 *                       Version 3, 29 June 2007
 *
 * Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 *
 * You can view LICENCE file for details.
 *
 * @author The Dragonet Team
 */
package org.dragonet.plugin.dpaddon;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import org.dragonet.common.utilities.BinaryStream;
import org.dragonet.plugin.dpaddon.bungee.HybridLoginListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DPAddonBungee extends Plugin implements Listener {

    private Configuration config;

    public final Set<ProxiedPlayer> recognisedBedrockPlayers = Collections.synchronizedSet(new HashSet<>());
    private HybridLoginListener hybridLoginListener;

    public Configuration getConfig() {
        return config;
    }

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        File conf = new File(getDataFolder(), "bungee-config.yml");
        try {
            if(!conf.exists()) {
                FileOutputStream fos = new FileOutputStream(conf);
                InputStream ins = getClass().getResourceAsStream("/bungee-config.yml");
                byte[] buff = new byte[256];
                int read;
                while((read = ins.read(buff)) != -1) {
                    fos.write(buff, 0, read);
                }
                ins.close();
                fos.close();
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(conf);
        } catch (Exception e) {
            getLogger().warning("Failed to load configuration file! ");
            return;
        }

        getProxy().getPluginManager().registerListener(this, this);

        if(config.getBoolean("enable-hybrid-login")) {
            getLogger().info("Beep! Enabling hybrid login for Mojang and xbox accounts... ");
            hybridLoginListener = new HybridLoginListener(this);
            getProxy().getPluginManager().registerListener(this, hybridLoginListener);
        }
    }

    @EventHandler
    public void onPlayerConnect(PluginMessageEvent e) {
        if(!e.getTag().equals("DragonProxy")) return;
        if(ProxiedPlayer.class.isAssignableFrom(e.getSender().getClass())) {
            BinaryStream bis = new BinaryStream(e.getData());
            String command = bis.getString();

            if(command.equals("Notification")) {
                recognisedBedrockPlayers.add((ProxiedPlayer) e.getSender());
                e.setCancelled(true); // we block the message
            }
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent e) {
        if(!recognisedBedrockPlayers.contains(e.getPlayer())) return;
        // forward the DragonProxy Notification message!
        getProxy().getScheduler().schedule(this, () -> {
            BinaryStream bis = new BinaryStream();
            bis.putString("Notification");
            e.getPlayer().sendData("DragonProxy", bis.getBuffer());
        }, 2000L, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        if(!recognisedBedrockPlayers.contains(e.getPlayer())) return;
        // We don't know that another server supports forwarding or not so we disable forwarding for now!
        BinaryStream bis = new BinaryStream();
        bis.putString("PacketFoward");
        bis.putBoolean(false);
        e.getPlayer().sendData("DragonProxy", bis.getBuffer());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        if(recognisedBedrockPlayers.contains(e.getPlayer())) {
            recognisedBedrockPlayers.remove(e.getPlayer());
        }
    }
}
