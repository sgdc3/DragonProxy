package org.dragonet.proxy.network.translator.pc;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDifficultyPacket;
import org.dragonet.proxy.network.UpstreamSession;
import org.dragonet.api.network.translator.PCPacketTranslator;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.packets.SetDifficultyPacket;

public class PCSetDifficultyTranslator implements PCPacketTranslator<ServerDifficultyPacket> {

    @Override
    public PEPacket[] translate(UpstreamSession session, ServerDifficultyPacket packet) {
        return new PEPacket[]{new SetDifficultyPacket(packet.getDifficulty())};
    }

}
