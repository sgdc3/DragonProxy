package org.dragonet.protocol.packets;

import org.dragonet.common.utilities.Binary;
import org.dragonet.common.utilities.Zlib;
import org.dragonet.protocol.PEPacket;
import org.dragonet.protocol.Protocol;
import org.dragonet.protocol.ProtocolInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

/**
 * @author MagicDroidX
 */
public class BatchPacket extends PEPacket {

    public List<PEPacket> packets;

    public BatchPacket() {
        packets = new ArrayList<>();
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.BATCH_PACKET;
    }

    @Override
    public void encodePayload() {
        byte[][] payload = new byte[packets.size() * 2][];
        for (int i = 0; i < packets.size(); i++) {
            PEPacket p = packets.get(i);
            if (!p.isEncoded()) {
                p.encode();
            }
            byte[] buf = p.getBuffer();
            payload[i * 2] = Binary.writeUnsignedVarInt(buf.length);
            payload[i * 2 + 1] = buf;
        }

        // FIXME: compression should be processed async
        try {
            putByteArray(Zlib.deflate(Binary.appendBytes(payload), Deflater.BEST_SPEED));
        } catch (Exception ex) {
            Logger.getLogger(BatchPacket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void decodePayload() {
        byte[] data;
        // FIXME: decompression should be handled async
        try {
            data = Zlib.inflate(this.get(), 64 * 1024 * 1024);
        } catch (Exception e) {
            return;
        }

        int len = data.length;
        try {
            while (offset < len) {
                byte[] buf = getByteArray();
                packets.add(Protocol.decodeSingle(buf));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
