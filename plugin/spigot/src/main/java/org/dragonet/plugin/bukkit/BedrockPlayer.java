package org.dragonet.plugin.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.dragonet.common.gui.ModalFormComponent;
import org.dragonet.protocol.PEBinaryStream;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.packets.ModalFormRequestPacket;

public class BedrockPlayer {

    private final Player player;

    // constructor
    private BedrockPlayer(Player player) {
        this.player = player;
    }

    // public
    public Player getPlayer() {
        return player;
    }

    public void process(PEPacket packet) {
        BedrockPacketProcessor handler = BedrockPacketProcessorRegister.getHandler(packet);
        if(handler != null) {
            handler.process(this, packet);
        }
    }

    // NOT SUPPORTED YET
    /* public void setPacketSubscription(Class<? extends PEPacket> clazz, boolean sub) {
        if(clazz.equals(PEPacket.class)) throw new IllegalArgumentException();
        BinaryStream bis = new BinaryStream();
        bis.putString("PacketSubscription");
        bis.putString(clazz.getSimpleName());
        bis.putBoolean(sub);
        player.sendPluginMessage(DPAddonBukkit.getInstance(), "DragonProxy", bis.getBuffer());
    } */

    public void sendForm(int formId, ModalFormComponent form) {
        String formData = form.serializeToJson().toString();
        ModalFormRequestPacket request = new ModalFormRequestPacket();
        request.formId = formId;
        request.formData = formData;
        request.encode();

        PEBinaryStream bis = new PEBinaryStream();
        bis.putString("SendPacket");
        bis.putByteArray(request.getBuffer());
        player.sendPluginMessage(DragonProxyBukkitAddon.getInstance(), "DragonProxy", bis.getBuffer());
    }


    // private


    // static
    public static void createForPlayer(Player player) {
        if(player.hasMetadata("BedrockPlayer")) {
            DragonProxyBukkitAddon.getInstance().getLogger().info("BedrockPlayer " + player.getName() + " already have the Bedrock Metadata");
            return;
        }
        player.setMetadata("BedrockPlayer", new FixedMetadataValue(
                DragonProxyBukkitAddon.getInstance(),
                new BedrockPlayer(player)
        ));
    }

    public static BedrockPlayer getForPlayer(Player player) {
        if(!player.hasMetadata("BedrockPlayer")) return null;
        return (BedrockPlayer) player.getMetadata("BedrockPlayer").get(0).value();
    }

}
