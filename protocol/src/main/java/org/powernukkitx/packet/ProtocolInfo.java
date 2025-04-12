package org.powernukkitx.packet;

public class ProtocolInfo {

    public static final byte UNKNOWN = 0;
    public static final byte CLIENT_HELLO = 1;
    public static final byte SERVER_HELLO = 2;
    public static final byte CLIENT_HEARTBEAT = 3;
    public static final byte WORLD_INFO = 4;
    public static final byte LEVEL_ACKNOWLEDGED = 5;
    public static final byte CHUNK_COMPLETION = 6;
    public static final byte CHUNK_REQUEST = 7;
    public static final byte PLAYER_POSITION_UPDATE = 8;
    public static final byte CHUNK_THROWAWAY = 9;

}
