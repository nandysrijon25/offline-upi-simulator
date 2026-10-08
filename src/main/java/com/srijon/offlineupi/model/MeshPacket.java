package com.srijon.offlineupi.model;

public class MeshPacket {
    private final String packetId;
    private final String transactionId;
    private int hops;
    private int ttl;

    public MeshPacket(String packetId, String transactionId, int ttl) {
        this.packetId = packetId;
        this.transactionId = transactionId;
        this.ttl = ttl;
    }

    public String getPacketId() { return packetId; }
    public String getTransactionId() { return transactionId; }
    public int getHops() { return hops; }
    public int getTtl() { return ttl; }

    public void forward() {
        if (ttl > 0) {
            hops++;
            ttl--;
        }
    }
}
