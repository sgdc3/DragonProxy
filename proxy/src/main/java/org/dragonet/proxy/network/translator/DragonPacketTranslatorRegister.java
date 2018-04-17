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
package org.dragonet.proxy.network.translator;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerSetExperiencePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.*;
import com.google.common.base.Preconditions;
import org.dragonet.api.network.translator.PCPacketTranslator;
import org.dragonet.api.network.translator.PEPacketTranslator;
import org.dragonet.api.network.translator.PacketTranslatorRegister;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.packets.*;
import org.dragonet.proxy.network.translator.pc.*;
import org.dragonet.proxy.network.translator.pe.*;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class DragonPacketTranslatorRegister implements PacketTranslatorRegister {

    private final Map<Class<? extends MinecraftPacket>, PCPacketTranslator<? extends MinecraftPacket>> pcToPeTranslators;
    private final Map<Class<? extends PEPacket>, PEPacketTranslator<? extends PEPacket>> peToPcTranslators;

    @Inject
    DragonPacketTranslatorRegister() {
        pcToPeTranslators = new LinkedHashMap<>();
        peToPcTranslators = new LinkedHashMap<>();
        registerBuiltInTranslators();
    }

    @Override
    public <P extends MinecraftPacket> void registerPCTranslator(Class<P> packet, PCPacketTranslator<P> translator) {
        Preconditions.checkNotNull(packet, translator);
        pcToPeTranslators.put(packet, translator);
    }

    @Override
    public <P extends PEPacket> void registerPETranslator(Class<P> packet, PEPacketTranslator<P> translator) {
        Preconditions.checkNotNull(packet, translator);
        peToPcTranslators.put(packet, translator);
    }

    @Override
    public void unregisterPCTranslator(Class<? extends MinecraftPacket> packet) {
        Preconditions.checkNotNull(packet);
        pcToPeTranslators.remove(packet);
    }

    @Override
    public void unregisterPETranslator(Class<? extends PEPacket> packet) {
        Preconditions.checkNotNull(packet);
        peToPcTranslators.remove(packet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P extends MinecraftPacket> Optional<PCPacketTranslator<P>> getPCTranslator(Class<P> packet) {
        Preconditions.checkNotNull(packet);
        return Optional.ofNullable((PCPacketTranslator<P>) pcToPeTranslators.get(packet));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P extends PEPacket> Optional<PEPacketTranslator<P>> getPETranslator(Class<P> packet) {
        Preconditions.checkNotNull(packet);
        return Optional.ofNullable((PEPacketTranslator<P>) peToPcTranslators.get(packet));
    }

    private void registerBuiltInTranslators() {
        // PC -> PE

        // Special
        registerPCTranslator(ServerPluginMessagePacket.class, new PCPluginMessagePacketTranslator());

        // Login phase
        registerPCTranslator(ServerJoinGamePacket.class, new PCJoinGamePacketTranslator());
        registerPCTranslator(ServerDisconnectPacket.class, new PCDisconnectPacketTranslator());
        // Settings && Weather
        registerPCTranslator(ServerNotifyClientPacket.class, new PCNotifyClientPacketTranslator());
        registerPCTranslator(ServerDifficultyPacket.class, new PCSetDifficultyTranslator());

        // Chat
        registerPCTranslator(ServerChatPacket.class, new PCChatPacketTranslator());
        registerPCTranslator(ServerTitlePacket.class, new PCSetTitlePacketTranslator());

        // Map
        registerPCTranslator(ServerChunkDataPacket.class, new PCChunkDataPacketTranslator());
        registerPCTranslator(ServerUpdateTimePacket.class, new PCUpdateTimePacketTranslator());
        registerPCTranslator(ServerBlockChangePacket.class, new PCBlockChangePacketTranslator());
        registerPCTranslator(ServerMultiBlockChangePacket.class, new PCMultiBlockChangePacketTranslator());
        registerPCTranslator(ServerExplosionPacket.class, new PCExplosionTranslator());
        registerPCTranslator(ServerUnloadChunkPacket.class, new PCUnloadChunkDataPacketTranslator());
        registerPCTranslator(ServerPlaySoundPacket.class, new PCPlaySoundPacketTranslator());
        registerPCTranslator(ServerPlayBuiltinSoundPacket.class, new PCSoundEventPacketTranslator());
        registerPCTranslator(ServerPlayEffectPacket.class, new PCPlayEffectPacketTranslator());

        // Entity
        registerPCTranslator(ServerSpawnMobPacket.class, new PCSpawnMobPacketTranslator());
        registerPCTranslator(ServerSpawnPlayerPacket.class, new PCSpawnPlayerPacketTranslator());
        registerPCTranslator(ServerSpawnObjectPacket.class, new PCSpawnObjectPacketTranslator());
        registerPCTranslator(ServerSpawnPaintingPacket.class, new PCSpawnPaintingPacketTranslator());
        registerPCTranslator(ServerSpawnExpOrbPacket.class, new PCSpawnExpOrbPacketTranslator());
        registerPCTranslator(ServerSpawnParticlePacket.class, new PCSpawnParticlePacketTranslator());
        registerPCTranslator(ServerEntityMetadataPacket.class, new PCEntityMetadataPacketTranslator());
        registerPCTranslator(ServerEntityDestroyPacket.class, new PCDestroyEntitiesPacketTranslator());
        registerPCTranslator(ServerEntityPositionRotationPacket.class, new PCEntityPositionRotationPacketTranslator());
        registerPCTranslator(ServerEntityPositionPacket.class, new PCEntityPositionPacketTranslator());
        registerPCTranslator(ServerEntityRotationPacket.class, new PCEntityRotationPacketTranslator());
        registerPCTranslator(ServerEntityVelocityPacket.class, new PCEntityVelocityPacketTranslator());
        registerPCTranslator(ServerEntityEffectPacket.class, new PCEntityEffectPacketTranslator());
        registerPCTranslator(ServerEntityEquipmentPacket.class, new PCEntityEquipmentPacketTranslator());
        registerPCTranslator(ServerEntityRemoveEffectPacket.class, new PCEntityRemoveEffectPacketTranslator());
        registerPCTranslator(ServerEntityAnimationPacket.class, new PCAnimationPacketTranslator());
        registerPCTranslator(ServerEntitySetPassengersPacket.class, new PCEntitySetPassengerPacketTranslator());
        registerPCTranslator(ServerEntityHeadLookPacket.class, new PCEntityHeadLookPacketTranslator());
        registerPCTranslator(ServerEntityTeleportPacket.class, new PCEntityTeleportPacketTranslator());
        registerPCTranslator(ServerUpdateTileEntityPacket.class, new PCUpdateTileEntityPacketTranslator());

        // Player
        registerPCTranslator(ServerPlayerPositionRotationPacket.class, new PCPlayerPositionRotationPacketTranslator());
        registerPCTranslator(ServerPlayerListEntryPacket.class, new PCPlayerListItemPacketTranslator());
        registerPCTranslator(ServerPlayerHealthPacket.class, new PCUpdateHealthPacketTranslator());
        registerPCTranslator(ServerRespawnPacket.class, new PCRespawnPacketTranslator());
        registerPCTranslator(ServerSpawnPositionPacket.class, new PCSpawnPositionPacketTranslator());
        registerPCTranslator(ServerPlayerSetExperiencePacket.class, new PCSetExperiencePacketTranslator());
        registerPCTranslator(ServerBossBarPacket.class, new PCBossBarPacketTranslator());

        // Inventory
        registerPCTranslator(ServerOpenWindowPacket.class, new PCOpenWindowPacketTranslator());
        registerPCTranslator(ServerCloseWindowPacket.class, new PCClosedWindowPacketTranslator());
        registerPCTranslator(ServerWindowItemsPacket.class, new PCWindowItemsTranslator());
        registerPCTranslator(ServerSetSlotPacket.class, new PCSetSlotPacketTranslator());

        // PE -> PC

        // Map
        registerPETranslator(LevelSoundEventPacket.class, new PESoundEventPacketTranslator());
        registerPETranslator(RequestChunkRadiusPacket.class, new PERequestChunkRadiusPacketTranslator());

        // Chat
        registerPETranslator(TextPacket.class, new PEChatPacketTranslator());
        registerPETranslator(CommandRequestPacket.class, new PECommandRequestPacketTranslator());

        // Entity
        registerPETranslator(MovePlayerPacket.class, new PEMovePlayerPacketTranslator());
        registerPETranslator(PlayerActionPacket.class, new PEPlayerActionPacketTranslator());
        registerPETranslator(InteractPacket.class, new PEInteractPacketTranslator());
        registerPETranslator(AdventureSettingsPacket.class, new PEAdventureSettingsPacketTranslator());
        registerPETranslator(PlayerInputPacket.class, new PEPlayerInputPacketTranslator());
        registerPETranslator(EntityEventPacket.class, new PEEntityEventPacketTranslator());

        // Inventory
        registerPETranslator(ContainerClosePacket.class, new PEWindowClosePacketTranslator());
        registerPETranslator(MobEquipmentPacket.class, new PEPlayerEquipmentPacketTranslator());
        registerPETranslator(InventoryTransactionPacket.class, new PEInventoryTransactionPacketTranslator());
        registerPETranslator(BlockPickRequestPacket.class, new PEBlockPickRequestPacketTranslator());
        registerPETranslator(PlayerHotbarPacket.class, new PEPlayerHotbarPacketTranslator());

        // Player
        registerPETranslator(AnimatePacket.class, new PEAnimatePacketTranslator());
    }
}
