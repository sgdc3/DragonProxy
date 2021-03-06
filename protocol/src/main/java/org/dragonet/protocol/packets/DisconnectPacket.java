package org.dragonet.protocol.packets;

import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DisconnectPacket extends PEPacket {

    public boolean hideDisconnectionScreen;
    public String message;

    public DisconnectPacket() {
    }

    public DisconnectPacket(boolean hideDisconnectionScreen, String message) {
        this.hideDisconnectionScreen = hideDisconnectionScreen;
        this.message = message;
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.DISCONNECT_PACKET;
    }

    @Override
    public void encodePayload() {
        putBoolean(hideDisconnectionScreen);
        putString(message);
    }

    @Override
    public void decodePayload() {
        hideDisconnectionScreen = getBoolean();
        message = getString();
    }
}
