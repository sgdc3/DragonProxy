package org.dragonet.protocol.type;

import java.util.UUID;
import org.dragonet.common.data.entity.Skin;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PlayerListEntry {

    public UUID uuid;
    public long eid;
    public String username;
    public String thirdPartyName;
    public int platform = 0; // TODO
    public String unk1 = ""; // TODO
    public Skin skin;
    public byte[] capeData = new byte[0]; // TODO
    public String geometryModel = "";
    public byte[] geometryData = new byte[0]; // TODO
    public String xboxUserId;

}
