package org.dragonet.api.network;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.whirvis.jraknet.session.RakNetClientSession;
import org.dragonet.common.utilities.LoginChainDecoder;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.packets.LoginPacket;
import org.dragonet.proxy.network.IDownstreamSession;
import org.dragonet.proxy.network.PEPacketProcessor;
import org.dragonet.proxy.network.cache.ChunkCache;
import org.dragonet.proxy.network.cache.EntityCache;
import org.dragonet.proxy.network.cache.JukeboxCache;
import org.dragonet.proxy.network.cache.WindowCache;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess"})
public interface UpstreamSession {

    String getRaknetID();

    RakNetClientSession getRaknetClient();

    boolean isLoggedIn();

    boolean isSpawned();

    InetSocketAddress getRemoteAddress();

    PEPacketProcessor getPacketProcessor();

    LoginChainDecoder getProfile();

    String getUsername();

    IDownstreamSession getDownstream();

    Map<String, Object> getDataCache();

    Map<UUID, PlayerListEntry> getPlayerInfoCache();

    EntityCache getEntityCache();

    WindowCache getWindowCache();

    ChunkCache getChunkCache();

    MinecraftProtocol getProtocol();

    JukeboxCache getJukeboxCache();

    void sendPacket(PEPacket packet);

    //if sending a packer before spawn, you should set high_priority to true !
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

    void onTick();

}
