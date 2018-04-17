package org.dragonet.proxy.network.translator;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.google.common.base.Preconditions;
import org.dragonet.api.network.UpstreamSession;
import org.dragonet.api.network.translator.PCPacketTranslator;
import org.dragonet.api.network.translator.PEPacketTranslator;
import org.dragonet.api.network.translator.PacketTranslateException;
import org.dragonet.api.network.translator.PacketTranslatorRegister;
import org.dragonet.common.utilities.OptionalUtils;
import org.dragonet.protocol.PEPacket;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Optional;

public class PacketTranslator {

    private final Logger logger;
    private final PacketTranslatorRegister translatorRegister;

    @Inject
    PacketTranslator(Logger logger, PacketTranslatorRegister translatorRegister) {
        this.logger = logger;
        this.translatorRegister = translatorRegister;
    }

    public Optional<PEPacket[]> translateToPE(UpstreamSession session, MinecraftPacket packet) {
        Preconditions.checkNotNull(session, packet);
        return OptionalUtils.handle(translatorRegister.getPCTranslator(packet.getClass()), (translator -> {
            try {
                return ((PCPacketTranslator<MinecraftPacket>) translator).translate(session, packet);
            } catch (PacketTranslateException e) {
                logger.warn("An error occurred while translating a packet! (PC -> PE)", e);
            }
            return Optional.empty();
        }), Optional::empty);
    }

    public Optional<MinecraftPacket[]> translateToPC(UpstreamSession session, PEPacket packet) {
        Preconditions.checkNotNull(session, packet);
        return OptionalUtils.handle(translatorRegister.getPETranslator(packet.getClass()), (translator -> {
            try {
                return ((PEPacketTranslator<PEPacket>) translator).translate(session, packet);
            } catch (PacketTranslateException e) {
                logger.warn("An error occurred while translating a packet! (PE -> PC)", e);
            }
            return Optional.empty();
        }), Optional::empty);
    }

}
