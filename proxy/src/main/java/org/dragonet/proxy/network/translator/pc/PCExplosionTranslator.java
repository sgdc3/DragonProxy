package org.dragonet.proxy.network.translator.pc;

import com.github.steveice10.mc.protocol.data.game.world.block.ExplodedBlockRecord;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerExplosionPacket;
import org.dragonet.common.maths.Vector3F;
import org.dragonet.proxy.network.UpstreamSession;
import org.dragonet.api.network.translator.PCPacketTranslator;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.packets.ExplodePacket;


import java.util.ArrayList;
import org.dragonet.common.maths.BlockPosition;

public class PCExplosionTranslator implements PCPacketTranslator<ServerExplosionPacket> {

    @Override
    public PEPacket[] translate(UpstreamSession session, ServerExplosionPacket packet) {

        ExplodePacket pk = new ExplodePacket();

        pk.position = new Vector3F(packet.getX(), packet.getY(), packet.getZ());
        pk.radius = packet.getRadius();
        pk.destroyedBlocks = new ArrayList<>(packet.getExploded().size());

        for (ExplodedBlockRecord record : packet.getExploded())
            pk.destroyedBlocks.add(new BlockPosition(record.getX(), record.getY(), record.getZ()));

        return new PEPacket[]{pk};
    }

}
