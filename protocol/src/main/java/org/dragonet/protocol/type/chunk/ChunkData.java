/*
 * This file was automatically generated by sel-utils and
 * released under the MIT License.
 * 
 * License: https://github.com/sel-project/sel-utils/blob/master/LICENSE
 * Repository: https://github.com/sel-project/sel-utils
 * Generated from https://github.com/sel-project/sel-utils/blob/master/xml/protocol/bedrock137.xml
 */
package org.dragonet.protocol.type.chunk;

import org.dragonet.protocol.PEBinaryStream;

import java.util.Arrays;

/**
 * Chunk's blocks, lights and other immutable data.
 */
public class ChunkData extends PEBinaryStream {

    /**
     * 16x16x16 section of the chunk. The array's keys also indicate the
     * section's height (the 3rd element of the array will be the 3rd section
     * from bottom, starting at `y=24`). The amount of sections should be in a
     * range from 0 (empty chunk) to 16.
     */
    public Section[] sections = new Section[0];
    public short[] heights = new short[256];
    /**
     * Biomes in order `xz`.
     */
    public byte[] biomes = new byte[256];
    /**
     * Colums where there are world borders (in format `xz`). This feature
     * hasn't been implemented in the game yet and crashes the client.
     */
    public byte[] borders = new byte[0];
    public ExtraData[] extraData = new ExtraData[0];
    /**
     * Additional data for the chunk's block entities (tiles), encoded in the
     * same way as BlockEntityData.nbt is. The position is given by the `Int`
     * tags `x`, `y`, `z` which are added to the block's compound tag together
     * with the `String` tag `id` that contains the name of the tile in pascal
     * case. Wrong encoding or missing tags may result in the block becoming
     * invisible.
     */
    public byte[] blockEntities = new byte[0];

    public ChunkData() {

    }

    public ChunkData(Section[] sections, short[] heights, byte[] biomes, byte[] borders, ExtraData[] extraData,
                     byte[] blockEntities) {
        this.sections = sections;
        this.heights = heights;
        this.biomes = biomes;
        this.borders = borders;
        this.extraData = extraData;
        this.blockEntities = blockEntities;
    }

    public void encode() {
        reset();
        putByte((byte) (sections.length & 0xff));
        for (Section s : sections) {
            s.encode(this);
        }
        for (short h : heights) {
            this.putLShort(h);
        }
        this.put(biomes);
        this.putByte((byte) (borders.length & 0xFF));
        this.put(borders);
        this.putVarInt(extraData.length);
        for (ExtraData extra : extraData) {
            extra.encode(this);
        }
        this.put(blockEntities);
    }

    public String toString() {
        return "ChunkData(sections: " + Arrays.deepToString(this.sections) + ", heights: "
            + Arrays.toString(this.heights) + ", biomes: " + Arrays.toString(this.biomes) + ", borders: "
            + Arrays.toString(this.borders) + ", extraData: " + Arrays.deepToString(this.extraData)
            + ", blockEntities: " + Arrays.toString(this.blockEntities) + ")";
    }
}
