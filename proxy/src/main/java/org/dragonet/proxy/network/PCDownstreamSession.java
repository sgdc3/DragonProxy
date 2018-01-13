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
package org.dragonet.proxy.network;

import com.github.steveice10.mc.protocol.ClientListener;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import org.dragonet.protocol.PEPacket;
import org.dragonet.proxy.DesktopServer;
import org.dragonet.proxy.DragonProxy;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.InvalidCredentialsException;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.exception.request.ServiceUnavailableException;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.handshake.HandshakeIntent;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerSetCompressionPacket;
import com.github.steveice10.mc.protocol.packet.login.client.EncryptionResponsePacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.mc.protocol.packet.login.server.EncryptionRequestPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSetCompressionPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusPingPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusQueryPacket;
import com.github.steveice10.mc.protocol.packet.status.server.StatusPongPacket;
import com.github.steveice10.mc.protocol.packet.status.server.StatusResponsePacket;
import com.github.steveice10.mc.protocol.util.CryptUtil;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import org.dragonet.proxy.configuration.Lang;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.Proxy;
import java.security.Key;
import java.util.List;

/**
 * Maintaince the connection between the proxy and remote Minecraft server.
 */
public class PCDownstreamSession implements IDownstreamSession<Packet> {

    public MinecraftProtocol protocol;

    private final DragonProxy proxy;
    private final UpstreamSession upstream;
    private DesktopServer serverInfo;
    private Client remoteClient;

    public PCDownstreamSession(DragonProxy proxy, UpstreamSession upstream) {
        this.proxy = proxy;
        this.upstream = upstream;
    }

    public void connect(DesktopServer serverInfo) {
        this.serverInfo = serverInfo;
        connect(serverInfo.remote_addr, serverInfo.remote_port);
    }

    public void connect(String addr, int port) {
        if (this.protocol == null) {
            upstream.onConnected(); // Clear the flags
            upstream.disconnect("ERROR! ");
            return;
        }
        remoteClient = new Client(addr, port, protocol, new TcpSessionFactory());
        remoteClient.getSession().setConnectTimeout(5);
        remoteClient.getSession().setReadTimeout(5);
        remoteClient.getSession().setWriteTimeout(5);
        // HACK HERE
        try {
            java.lang.reflect.Field f = com.github.steveice10.packetlib.Session.class.getDeclaredField("listeners");
            List<SessionListener> listeners = (List<SessionListener>) f.get(remoteClient.getSession());
            listeners.clear();
        } catch (Exception e){
            e.printStackTrace();
        }
        remoteClient.getSession().addListener(new ClientListener() {
            public void connected(ConnectedEvent event) {
                // HACKY STUFF
                String host = proxy.getAuthMode().equalsIgnoreCase("hybrid") ? makeHybridHostString() : event.getSession().getHost();
                MinecraftProtocol protocol = (MinecraftProtocol) event.getSession().getPacketProtocol();
                try {
                    java.lang.reflect.Method setSubProtocol = MinecraftProtocol.class.getDeclaredMethod("setSubProtocol", SubProtocol.class, boolean.class, Session.class);

                    if (protocol.getSubProtocol() == SubProtocol.LOGIN) {
                        GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
                        setSubProtocol.invoke(protocol, SubProtocol.HANDSHAKE, true, event.getSession());
                        event.getSession().send(new HandshakePacket(MinecraftConstants.PROTOCOL_VERSION, host, event.getSession().getPort(), HandshakeIntent.LOGIN));
                        setSubProtocol.invoke(protocol, SubProtocol.LOGIN, true, event.getSession());
                        event.getSession().send(new LoginStartPacket(profile != null ? profile.getName() : ""));
                    } else if (protocol.getSubProtocol() == SubProtocol.STATUS) {
                        setSubProtocol.invoke(protocol, SubProtocol.HANDSHAKE, true, event.getSession());
                        event.getSession().send(new HandshakePacket(MinecraftConstants.PROTOCOL_VERSION, host, event.getSession().getPort(), HandshakeIntent.STATUS));
                        setSubProtocol.invoke(protocol, SubProtocol.STATUS, true, event.getSession());
                        event.getSession().send(new StatusQueryPacket());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                proxy.getLogger().info(proxy.getLang().get(Lang.MESSAGE_REMOTE_CONNECTED, upstream.getUsername(),
                    upstream.getRemoteAddress()));

                // Notify the server
//                BinaryStream bis = new BinaryStream();
//                bis.putString("Notification"); // command
//                ClientPluginMessagePacket pluginMessage = new ClientPluginMessagePacket("DragonProxy", bis.get());
//                send(pluginMessage);

                upstream.onConnected();
            }

            public void disconnected(DisconnectedEvent event) {
                upstream.disconnect(proxy.getLang().get(event.getReason()));
            }
            public void disconnecting(DisconnectingEvent event) {
                upstream.disconnect(proxy.getLang().get(event.getReason()));
            }

            public void packetReceived(PacketReceivedEvent event) {
                super.packetReceived(event);

                /* == DragonProxy code == */
                if(!((MinecraftProtocol)remoteClient.getSession().getPacketProtocol()).getSubProtocol().equals(SubProtocol.GAME)) return;
                // Handle the packet
                try {
                    PEPacket[] packets = PacketTranslatorRegister.translateToPE(upstream, event.getPacket());
                    if (packets == null) {
                        return;
                    }
                    if (packets.length <= 0) {
                        return;
                    }
                    if (packets.length == 1) {
                        upstream.sendPacket(packets[0]);
                    } else {
                        upstream.sendAllPackets(packets, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        });
        remoteClient.getSession().connect();
    }

    public String makeHybridHostString() {
        return upstream.getProfile().xuid + ":" + remoteClient.getSession().getHost();
    }

    public void disconnect() {
        if (remoteClient != null && remoteClient.getSession().isConnected()) {
            remoteClient.getSession().disconnect("Disconnect");
        }
    }

    public boolean isConnected() {
        return remoteClient != null && remoteClient.getSession().isConnected();
    }

    public void send(Packet... packets) {
        for (Packet p : packets) {
            send(p);
        }
    }

    public void send(Packet packet) {
        if (packet == null) {
            return;
        }
        remoteClient.getSession().send(packet);
    }

    public void sendChat(String chat) {
        remoteClient.getSession().send(new ClientChatPacket(chat));
    }

    public void onTick() {

    }

    public DragonProxy getProxy() {
        return proxy;
    }

    public UpstreamSession getUpstream() {
        return upstream;
    }

    public DesktopServer getServerInfo() {
        return serverInfo;
    }
}
