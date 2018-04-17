package org.dragonet.api.network;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import org.dragonet.common.utilities.LoginChainDecoder;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.packets.LoginPacket;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess"})
public interface UpstreamSession {

    boolean isLoggedIn();

    boolean isSpawned();

    InetSocketAddress getRemoteAddress();

    LoginChainDecoder getProfile();

    String getUsername();

    DownstreamSession getDownstream();

    Map<String, Object> getDataCache();

    Map<UUID, PlayerListEntry> getPlayerInfoCache();

    MinecraftProtocol getProtocol();

    void sendPacket(PEPacket packet);

    /**
     * Sends a packet to the upstream client.
     *
     * @param packet        the packet
     * @param high_priority required to be true if the packet is sent before spawn
     */
    void sendPacket(PEPacket packet, boolean high_priority);

    void sendAllPackets(PEPacket[] packets, boolean high_priority);

    void connectToServer(String address, int port);

    void onConnected();

    void disconnect(String reason);

    void onDisconnect(String reason);

    void authenticate(String email, String password, Proxy authProxy);

    void onLogin(LoginPacket packet);

    void postLogin();

    void setSpawned();

    void sendChat(String chat);

    void sendFakeBlock(int x, int y, int z, int id, int meta);

    void sendCreativeInventory();

    void handlePacketBinary(byte[] packet);

    void putCachePacket(PEPacket packet);

}
