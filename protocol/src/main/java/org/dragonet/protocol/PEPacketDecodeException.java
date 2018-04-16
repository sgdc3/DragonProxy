package org.dragonet.protocol;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PEPacketDecodeException extends Exception {

    public PEPacketDecodeException(String message) {
        super(message);
    }

    public PEPacketDecodeException(String message, Throwable cause) {
        super(message, cause);
    }

}
