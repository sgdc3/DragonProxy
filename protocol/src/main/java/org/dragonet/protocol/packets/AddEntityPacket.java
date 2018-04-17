package org.dragonet.protocol.packets;

import org.dragonet.common.data.entity.PEEntityAttribute;
import org.dragonet.common.data.entity.PEEntityLink;
import org.dragonet.common.data.entity.meta.EntityMetaData;
import org.dragonet.common.maths.Vector3F;
import org.dragonet.common.utilities.OptionalUtils;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the AddEntity packet from the Bedrock edition protocol.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AddEntityPacket extends PEPacket {

    public long entityId;
    public long runtimeId;
    public int type;
    public Vector3F position;
    public Vector3F motion;
    public float pitch;
    public float yaw;
    public Optional<List<PEEntityAttribute>> attributes;
    public Optional<EntityMetaData> meta;
    public Optional<List<PEEntityLink>> links;

    /**
     * Constructor of the AddEntity packet.
     *
     * @param entityId   the entity id.
     * @param runtimeId  the entity runtime id.
     * @param type       the type.
     * @param position   the position of the entity.
     * @param motion     the motion of the entity.
     * @param pitch      the pitch of the entity.
     * @param yaw        the yaw of the entity.
     * @param attributes the attributes of the entity.
     * @param meta       the metadata of the entity.
     * @param links      the links of the entity.
     */
    public AddEntityPacket(long entityId, long runtimeId, int type, Vector3F position, Vector3F motion, float pitch,
                           float yaw, Optional<List<PEEntityAttribute>> attributes, Optional<EntityMetaData> meta,
                           Optional<List<PEEntityLink>> links) {
        this.entityId = entityId;
        this.runtimeId = runtimeId;
        this.type = type;
        this.position = position;
        this.motion = motion;
        this.pitch = pitch;
        this.yaw = yaw;
        this.attributes = attributes;
        this.meta = meta;
        this.links = links;
    }

    /**
     * Constructor of the AddEntity packet.
     *
     * @param entityId  the entity id.
     * @param runtimeId the entity runtime id.
     * @param type      the type.
     * @param position  the position of the entity.
     * @param motion    the motion of the entity.
     * @param pitch     the pitch of the entity.
     * @param yaw       the yaw of the entity.
     */
    public AddEntityPacket(long entityId, long runtimeId, int type, Vector3F position, Vector3F motion, float pitch, float yaw) {
        this(entityId, runtimeId, type, position, motion, pitch, yaw, Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Default constructor of ghe AddEntity packet.
     * Please fill all the required fields manually or use the decodePayload method!
     */
    public AddEntityPacket() {
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.ADD_ENTITY_PACKET;
    }

    @Override
    public void encodePayload() {
        putVarLong(entityId);
        putUnsignedVarLong(runtimeId);
        putUnsignedVarInt(type);
        putVector3F(position);
        putVector3F(motion);
        putLFloat(pitch);
        putLFloat(yaw);
        OptionalUtils.ifPresentOr(attributes, attributesList -> {
            putUnsignedVarInt(attributesList.size());
            for (PEEntityAttribute attr : attributesList) {
                putString(attr.name);
                putLFloat(attr.min);
                putLFloat(attr.currentValue);
                putLFloat(attr.max);
            }
        }, () -> putUnsignedVarInt(0));
        OptionalUtils.ifPresentOr(meta, metadata -> {
            metadata.encode();
            put(metadata.getBuffer());
        }, () -> putUnsignedVarInt(0));
        OptionalUtils.ifPresentOr(links, linkList -> {
            putUnsignedVarInt(linkList.size());
            for (PEEntityLink link : linkList) {
                putEntityLink(link);
            }
        }, () -> putUnsignedVarInt(0));
    }

    /**
     * FIXME: decode will NOT work since meta decoding is not implemented
     */
    @Override
    public void decodePayload() {
        entityId = getVarLong();
        runtimeId = getUnsignedVarLong();
        type = (int) getUnsignedVarInt();
        position = getVector3F();
        motion = getVector3F();
        pitch = getLFloat();
        yaw = getLFloat();

        int attributeCount = (int) getUnsignedVarInt();
        if (attributeCount == 0) {
            attributes = Optional.empty();
        } else {
            List<PEEntityAttribute> attributeList = new ArrayList<>(attributeCount);
            for (int i = 0; i < attributeCount; i++) {
                String name = getString();
                float min = getLFloat();
                float current = getLFloat();
                float max = getLFloat();
                PEEntityAttribute.findAttribute(name).ifPresent(attribute -> {
                    attribute.min = min;
                    attribute.max = max;
                    attribute.currentValue = current;
                    attributeList.add(attribute);
                });
            }
            attributes = Optional.of(attributeList);
        }

        // TODO: read meta!!

        int linkCount = (int) getUnsignedVarInt();
        if(linkCount == 0) {
            links = Optional.empty();
        } else {
            List<PEEntityLink> linkList = new ArrayList<>(linkCount);
            for (int i = 0; i < linkCount; i++) {
                linkList.add(getEntityLink());
            }
            links = Optional.of(linkList);
        }
    }
}
