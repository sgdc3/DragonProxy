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
package org.dragonet.api.network;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;

@SuppressWarnings({"unused", "WeakerAccess"})
public interface DownstreamSession {

    void connect(String address, int port);

    void disconnect();

    boolean isConnected();

    void send(MinecraftPacket packet);

    void send(MinecraftPacket... packets);

    void sendChat(String chat);

    void onTick();

}
