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
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerVehicleMovePacket;
import org.dragonet.protocol.PEPacket;

public class PCVehicleMovePacketTranslator implements PCPacketTranslator<ServerVehicleMovePacket> {

    public PEPacket[] translate(UpstreamSession session, ServerVehicleMovePacket packet) {
//        System.out.println(DebugTools.getAllFields(packet));
        return new PEPacket[]{};
    }
}
