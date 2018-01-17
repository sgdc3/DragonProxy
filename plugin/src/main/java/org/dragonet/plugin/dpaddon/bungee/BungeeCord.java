package org.dragonet.plugin.dpaddon.bungee;

public class BungeeCord {

    public final static Class InitialHandler;

    static {
        Class out_InitialHandler = null;
        try {
            out_InitialHandler = Class.forName("net.md_5.bungee.connection.InitialHandler");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            InitialHandler = out_InitialHandler;
        }
    }

}
