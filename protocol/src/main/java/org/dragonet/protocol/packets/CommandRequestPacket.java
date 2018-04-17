package org.dragonet.protocol.packets;

import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class CommandRequestPacket extends PEPacket {

    public String command;

    public CommandRequestPacket() {
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.COMMAND_REQUEST_PACKET;
    }

    @Override
    public void encodePayload() {
        putString(command);
    }

    @Override
    public void decodePayload() {
        command = getString();
    }
}
