package org.dragonet.protocol.packets;

import org.dragonet.protocol.PEBinaryStream;
import org.dragonet.common.utilities.LoginChainDecoder;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.ProtocolInfo;

/**
 * Created on 2017/10/21.
 */
public class LoginPacket extends PEPacket {

    public int protocol;
    public LoginChainDecoder decoded;

    public LoginPacket() {

    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.LOGIN_PACKET;
    }

    @Override
    public void decodePayload() {
        protocol = getInt();

        if (protocol != ProtocolInfo.CURRENT_PROTOCOL) {
            if (protocol > 0xffff) { // guess MCPE <= 1.1
                offset -= 6;
                protocol = getInt();
            }
            return; // Do not attempt to continue decoding for non-accepted protocols
        }

        byte[] payload = getByteArray();
        PEBinaryStream bin = new PEBinaryStream(payload);
        byte[] chain = bin.get(bin.getLInt());
        byte[] client = bin.get(bin.getLInt());
        decoded = new LoginChainDecoder(chain, client);
        decoded.decode();
    }

    @Override
    public void encodePayload() {
        // TODO
    }
}
