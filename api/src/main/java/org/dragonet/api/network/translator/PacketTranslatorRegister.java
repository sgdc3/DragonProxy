package org.dragonet.api.network.translator;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import org.dragonet.protocol.PEPacket;

import java.util.Optional;

public interface PacketTranslatorRegister {

    <P extends MinecraftPacket> void registerPCTranslator(Class<P> packet, PCPacketTranslator<P> translator);

    <P extends PEPacket> void registerPETranslator(Class<P> packet, PEPacketTranslator<P> translator);

    void unregisterPCTranslator(Class<? extends MinecraftPacket> packet);

    void unregisterPETranslator(Class<? extends PEPacket> packet);

    <P extends MinecraftPacket> Optional<PCPacketTranslator<P>> getPCTranslator(Class<P> packet);

    <P extends PEPacket> Optional<PEPacketTranslator<P>> getPETranslator(Class<P> packet);

}
