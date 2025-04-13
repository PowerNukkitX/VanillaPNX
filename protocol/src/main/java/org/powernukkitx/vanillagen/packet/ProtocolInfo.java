package org.powernukkitx.vanillagen.packet;

public class ProtocolInfo {

    public static final byte UNKNOWN = 0;
    public static final byte CLIENT_HELLO = 1;
    public static final byte SERVER_HELLO = 2;
    public static final byte WORLD_INFO = 3;
    public static final byte LEVEL_ACKNOWLEDGED = 4;
    public static final byte CHUNK_REQUEST = 5;
    public static final byte CHUNK_TERRAIN_DATA = 6;
    public static final byte CHUNK_THROWAWAY = 7;
    public static final byte PLAYER_POSITION_UPDATE = 8;
    public static final byte POPULATION = 9;
    public static final byte CLIENT_HEARTBEAT = 10;
    public static final byte BLOCK_ENTITY_DATA = 11;
}
