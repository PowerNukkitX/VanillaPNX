package org.powernukkitx.vanillagen.utils;

public class ChunkHash {

    public static long chunkHash(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    public static int getHashX(long hash) {
        return (int) (hash >> 32);
    }

    public static int getHashZ(long hash) {
        return (int) hash;
    }

}
