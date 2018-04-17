package org.dragonet.proxy.network.translator.pe;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.dragonet.proxy.network.UpstreamSession;
import org.dragonet.api.network.translator.PEPacketTranslator;
import org.dragonet.protocol.packets.CommandRequestPacket;

/**
 * Created on 2017/11/15.
 */
public class PECommandRequestPacketTranslator implements PEPacketTranslator<CommandRequestPacket> {

    public Packet[] translate(UpstreamSession session, CommandRequestPacket packet) {
        return new Packet[]{new ClientChatPacket(packet.command)};
    }
}
