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

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.ClientListener;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import org.dragonet.protocol.PEPacket;
import org.dragonet.proxy.DesktopServer;
import org.dragonet.proxy.DragonProxy;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import org.dragonet.proxy.configuration.Lang;


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
        remoteClient.getSession().addListener(new SessionAdapter() {

            @Override
            public void packetSending(PacketSendingEvent event) { //Intercept packets and change data
                if (proxy.getAuthMode().equalsIgnoreCase("hybrid")){
                    if (protocol.getSubProtocol() == SubProtocol.HANDSHAKE && event.getPacket() instanceof HandshakePacket) {
                        HandshakePacket packet = (HandshakePacket) event.getPacket();
                        String host = remoteClient.getSession().getHost() + "\0" + upstream.getProfile().xuid;
                        packet = new HandshakePacket(packet.getProtocolVersion(), host, packet.getPort(), packet.getIntent());
                        event.setPacket(packet);
                        System.out.println("HandshakePacket----------------------------------------");
                    }
                    //Why do that, i think it's OK
//                    if (protocol.getSubProtocol() == SubProtocol.LOGIN && event.getPacket() instanceof LoginStartPacket) {
//                        GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
//                        LoginStartPacket packet = new LoginStartPacket(profile.getName());
//                        event.setPacket(packet);
//                        System.out.println("LoginStartPacket----------------------------------------");
//                    }
                }
            }

            public void connected(ConnectedEvent event) {
                proxy.getLogger().info(proxy.getLang().get(Lang.MESSAGE_REMOTE_CONNECTED, upstream.getUsername(),
                        upstream.getRemoteAddress()));

                // Notify the server
//                BinaryStream bis = new BinaryStream();
//                bis.putString("Notification"); // command
//                ClientPluginMessagePacket pluginMessage = new ClientPluginMessagePacket("DragonProxy", bis.get());
//                send(pluginMessage);
                upstream.onConnected();
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                upstream.disconnect(proxy.getLang().get(event.getReason()));
            }

            @Override
            public void disconnecting(DisconnectingEvent event) {
                upstream.disconnect(proxy.getLang().get(event.getReason()));
            }

            @Override
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
