package org.dragonet.plugin.dpaddon.bukkit.processors;

import org.bukkit.Bukkit;
import org.dragonet.plugin.dpaddon.bukkit.BedrockPacketProcessor;
import org.dragonet.plugin.dpaddon.bukkit.BedrockPlayer;
import org.dragonet.plugin.dpaddon.bukkit.events.ModalFormResponseEvent;
import org.dragonet.protocol.packets.ModalFormResponsePacket;
import org.json.JSONArray;

public class ModalFormResponseProcessor implements BedrockPacketProcessor<ModalFormResponsePacket> {
    @Override
    public void process(BedrockPlayer bedrockPlayer, ModalFormResponsePacket packet) {
        JSONArray array = new JSONArray(packet.formData);

        ModalFormResponseEvent event = new ModalFormResponseEvent(bedrockPlayer, array);
        Bukkit.getPluginManager().callEvent(event);
    }
}
