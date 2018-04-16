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
package org.dragonet.protocol;

import org.dragonet.common.utilities.BinaryStream;
import org.dragonet.common.utilities.Zlib;
import org.dragonet.protocol.packets.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.dragonet.protocol.ProtocolInfo.*;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Protocol {

    private static final Map<Byte, Class<? extends PEPacket>> PACKETS;

    static {
        PACKETS = new HashMap<>();
        PACKETS.put(DISCONNECT_PACKET, DisconnectPacket.class);
        PACKETS.put(LOGIN_PACKET, LoginPacket.class);
        PACKETS.put(PLAY_STATUS_PACKET, PlayStatusPacket.class);
        PACKETS.put(START_GAME_PACKET, StartGamePacket.class);
        PACKETS.put(REQUEST_CHUNK_RADIUS_PACKET, RequestChunkRadiusPacket.class);
        PACKETS.put(CHUNK_RADIUS_UPDATED_PACKET, ChunkRadiusUpdatedPacket.class);
        PACKETS.put(FULL_CHUNK_DATA_PACKET, FullChunkDataPacket.class);
        PACKETS.put(UPDATE_BLOCK_PACKET, UpdateBlockPacket.class);
        PACKETS.put(BLOCK_EVENT_PACKET, BlockEventPacket.class);
        PACKETS.put(ADD_PAINTING_PACKET, AddPaintingPacket.class);
        PACKETS.put(TEXT_PACKET, TextPacket.class);
        PACKETS.put(COMMAND_REQUEST_PACKET, CommandRequestPacket.class);
        PACKETS.put(CHANGE_DIMENSION_PACKET, ChangeDimensionPacket.class);
        PACKETS.put(REMOVE_ENTITY_PACKET, RemoveEntityPacket.class);
        PACKETS.put(ENTITY_EVENT_PACKET, EntityEventPacket.class);
        PACKETS.put(MOB_EFFECT_PACKET, MobEffectPacket.class);
        PACKETS.put(BOSS_EVENT_PACKET, BossEventPacket.class);
        PACKETS.put(ADD_ITEM_ENTITY_PACKET, AddItemEntityPacket.class);
        PACKETS.put(MOVE_ENTITY_PACKET, MoveEntityPacket.class);
        PACKETS.put(MOVE_PLAYER_PACKET, MovePlayerPacket.class);
        PACKETS.put(SET_ENTITY_MOTION_PACKET, SetEntityMotionPacket.class);
        PACKETS.put(SET_PLAYER_GAME_TYPE_PACKET, SetPlayerGameTypePacket.class);
        PACKETS.put(ADVENTURE_SETTINGS_PACKET, AdventureSettingsPacket.class);
        PACKETS.put(ANIMATE_PACKET, AnimatePacket.class);
        PACKETS.put(LEVEL_SOUND_EVENT_PACKET, LevelSoundEventPacket.class);
        PACKETS.put(BLOCK_PICK_REQUEST_PACKET, BlockPickRequestPacket.class);
        PACKETS.put(SET_SPAWN_POSITION_PACKET, SetSpawnPositionPacket.class);
        PACKETS.put(LEVEL_EVENT_PACKET, LevelEventPacket.class);
        PACKETS.put(PLAY_SOUND_PACKET, PlaySoundPacket.class);
        PACKETS.put(STOP_SOUND_PACKET, StopSoundPacket.class);
        PACKETS.put(ADD_ENTITY_PACKET, AddEntityPacket.class);
        PACKETS.put(ADD_PLAYER_PACKET, AddPlayerPacket.class);
        PACKETS.put(PLAYER_LIST_PACKET, PlayerListPacket.class);
        PACKETS.put(SET_HEALTH_PACKET, SetHealthPacket.class);
        PACKETS.put(RESPAWN_PACKET, RespawnPacket.class);
        PACKETS.put(BLOCK_ENTITY_DATA_PACKET, BlockEntityDataPacket.class);
        PACKETS.put(SET_TIME_PACKET, SetTimePacket.class);
        PACKETS.put(INTERACT_PACKET, InteractPacket.class);
        PACKETS.put(PLAYER_ACTION_PACKET, PlayerActionPacket.class);
        PACKETS.put(MOB_EQUIPMENT_PACKET, MobEquipmentPacket.class);
        PACKETS.put(SET_ENTITY_DATA_PACKET, SetEntityDataPacket.class);
        PACKETS.put(PLAYER_SKIN_PACKET, PlayerSkinPacket.class);
        PACKETS.put(PLAYER_HOTBAR_PACKET, PlayerHotbarPacket.class);
        PACKETS.put(SET_ENTITY_LINK_PACKET, SetEntityLinkPacket.class);
        PACKETS.put(PLAYER_INPUT_PACKET, PlayerInputPacket.class);
        PACKETS.put(SET_DIFFICULTY_PACKET, SetDifficultyPacket.class);
        PACKETS.put(SET_TITLE_PACKET, SetTitlePacket.class);
        PACKETS.put(SPAWN_EXPERIENCE_ORB_PACKET, SpawnExperienceOrb.class);
        PACKETS.put(EXPLODE_PACKET, ExplodePacket.class);
        PACKETS.put(ENTITY_FALL_PACKET, EntityFallPacket.class);

        PACKETS.put(MODAL_FORM_REQUEST_PACKET, ModalFormRequestPacket.class);
        PACKETS.put(MODAL_FORM_RESPONSE_PACKET, ModalFormResponsePacket.class);
        PACKETS.put(SERVER_SETTINGS_REQUEST_PACKET, ServerSettingsRequestPacket.class);
        PACKETS.put(SERVER_SETTINGS_RESPONSE_PACKET, ServerSettingsResponsePacket.class);

        PACKETS.put(CONTAINER_OPEN_PACKET, ContainerOpenPacket.class);
        PACKETS.put(CONTAINER_CLOSE_PACKET, ContainerClosePacket.class);
        PACKETS.put(INVENTORY_CONTENT_PACKET, InventoryContentPacket.class);
        PACKETS.put(INVENTORY_SLOT_PACKET, InventorySlotPacket.class);
        PACKETS.put(INVENTORY_TRANSACTION_PACKET, InventoryTransactionPacket.class);

        PACKETS.put(RESOURCE_PACKS_INFO_PACKET, ResourcePacksInfoPacket.class);
        PACKETS.put(RESOURCE_PACK_CLIENT_RESPONSE_PACKET, ResourcePackClientResponsePacket.class);
        PACKETS.put(RESOURCE_PACK_STACK_PACKET, ResourcePackStackPacket.class);

        PACKETS.put(BATCH_PACKET, BatchPacket.class);
    }

    public static Class<? extends PEPacket> getPacket(byte packetId) {
        return PACKETS.get(packetId);
    }

    public static PEPacket[] decode(byte[] data) throws PEPacketDecodeException {
        if (data == null || data.length < 1) {
            return null;
        }

        byte[] inflated;
        try {
            inflated = Zlib.inflate(Arrays.copyOfRange(data, 1, data.length));
        } catch (IOException e) {
            throw new PEPacketDecodeException("Unable to deflate data! An IO error occurred!", e);
        }

        List<PEPacket> packets = new ArrayList<>(2);
        BinaryStream stream = new BinaryStream(inflated);
        while (stream.offset < inflated.length) {
            byte[] buffer = stream.get((int) stream.getUnsignedVarInt());
            try {
                PEPacket decoded = decodeSingle(buffer);
                packets.add(decoded);
            } catch (PEPacketDecodeException e) {
                e.printStackTrace();
            }
        }

        return packets.size() > 0 ? packets.toArray(new PEPacket[0]) : null;
    }

    public static PEPacket decodeSingle(byte[] buffer) throws PEPacketDecodeException {
        byte packetId = (byte) new BinaryStream(buffer).getUnsignedVarInt();
        if (PACKETS.containsKey(packetId)) {
            Class<? extends PEPacket> packetClass = PACKETS.get(packetId);
            try {
                PEPacket packet = packetClass.getDeclaredConstructor().newInstance();
                packet.setBuffer(buffer);
                packet.decode();
                return packet;
            } catch (SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
                throw new PEPacketDecodeException("An error occurred while decoding a packet!", e);
            }
        } else {
            throw new PEPacketDecodeException("Unknown packet with id 0x" + Integer.toHexString(packetId));
        }
    }
}
