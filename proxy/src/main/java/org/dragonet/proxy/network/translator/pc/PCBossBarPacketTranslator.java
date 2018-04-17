package org.dragonet.proxy.network.translator.pc;

import org.dragonet.proxy.network.UpstreamSession;
import org.dragonet.api.network.translator.PCPacketTranslator;
import org.dragonet.protocol.PEPacket;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerBossBarPacket;

public class PCBossBarPacketTranslator implements PCPacketTranslator<ServerBossBarPacket> {

    @Override
    public PEPacket[] translate(UpstreamSession session, ServerBossBarPacket packet) {

        return new PEPacket[]{};
    }

}
