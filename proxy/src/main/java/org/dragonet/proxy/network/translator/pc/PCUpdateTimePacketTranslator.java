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
package org.dragonet.proxy.network.translator.pc;

import org.dragonet.proxy.network.UpstreamSession;
import org.dragonet.api.network.translator.PCPacketTranslator;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateTimePacket;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.packets.SetTimePacket;

public class PCUpdateTimePacketTranslator implements PCPacketTranslator<ServerUpdateTimePacket> {

    public PEPacket[] translate(UpstreamSession session, ServerUpdateTimePacket packet) {
        SetTimePacket pk = new SetTimePacket();
        pk.time = (int) Math.abs(packet.getTime());
        return new PEPacket[]{pk};
    }
}
