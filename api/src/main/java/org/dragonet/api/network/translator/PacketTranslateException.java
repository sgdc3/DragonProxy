package org.dragonet.api.network.translator;

public class PacketTranslateException extends Exception {

    public PacketTranslateException(String message) {
        super(message);
    }

    public PacketTranslateException(String message, Throwable cause) {
        super(message, cause);
    }

}
