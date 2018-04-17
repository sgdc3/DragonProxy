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
package org.dragonet.api.network.translator;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.dragonet.api.network.UpstreamSession;
import org.dragonet.protocol.PEPacket;

import java.util.Optional;

public interface PCPacketTranslator<P extends MinecraftPacket> {

    Optional<PEPacket[]> translate(UpstreamSession session, P packet) throws PacketTranslateException;

}
