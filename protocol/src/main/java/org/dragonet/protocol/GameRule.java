package org.dragonet.protocol;


import java.util.Optional;

@SuppressWarnings({"unused", "WeakerAccess"})
/**
 * This class was taken from the Nukkit project.
 */
public class GameRule {

    private String name;
    private GameRuleType type;
    private Optional<Object> value;

    public enum GameRuleType {
        UNKNOWN, BOOLEAN, VARUINT, FLOAT
    }

    public GameRule(String name, GameRuleType type, Optional<Object> value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public static GameRule read(PEBinaryStream source) {
        String name = source.getString();
        GameRuleType type = GameRuleType.values()[(int) source.getUnsignedVarInt()];
        Object value;
        switch (type) {
            case BOOLEAN:
                value = source.getBoolean();
                break;
            case VARUINT:
                value = source.getUnsignedVarInt();
                break;
            case FLOAT:
                value = source.getLFloat();
                break;
            default:
                value = null;
                break;
        }
        return new GameRule(name, type, Optional.ofNullable(value));
    }

    public void write(PEBinaryStream out) {
        if (type.ordinal() == 0) {
            return; // wtf
        }
        out.putString(name);
        out.putUnsignedVarInt(type.ordinal());
        switch (type) {
            case BOOLEAN:
                out.putBoolean((boolean) value);
                break;
            case VARUINT:
                out.putUnsignedVarInt((int) value);
                break;
            case FLOAT:
                out.putLFloat((Float) value);
                break;
        }
    }
}
