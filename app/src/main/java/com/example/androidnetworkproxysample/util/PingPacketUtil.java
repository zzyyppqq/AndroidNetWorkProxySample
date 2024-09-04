package com.example.androidnetworkproxysample.util;

import java.nio.ByteBuffer;

public class PingPacketUtil {
    private static final int ICMP_HEADER_SIZE = 8;

    // 构建ICMP报文
    public static byte[] buildPingPacket() {
        byte[] packet = new byte[ICMP_HEADER_SIZE];

        // 设置ICMP类型为8（Echo Request）
        packet[0] = (byte) 8;

        // 设置ICMP代码为0（Echo Request）
        packet[1] = 0;

        // 计算并设置校验和为0
        setChecksum(packet);

        return packet;
    }

    // 计算并设置校验和
    private static void setChecksum(byte[] packet) {
        int checksum = 0;

        // 计算校验和时，先将校验和字段置为0
        packet[2] = 0;
        packet[3] = 0;

        // 计算校验和
        for (int i = 0; i < ICMP_HEADER_SIZE; i += 2) {
            checksum += ((packet[i] & 0xFF) << 8) | (packet[i + 1] & 0xFF);
        }

        // 将校验和存储为网络字节序（大端序）
        checksum = ~((checksum & 0xFFFF) + (checksum >> 16));
        packet[2] = (byte) ((checksum >> 8) & 0xFF);
        packet[3] = (byte) (checksum & 0xFF);
    }

    public static void main(String[] args) {
        // 构造 Ping 帧数据
        ByteBuffer pingPayload = ByteBuffer.wrap("Hello, server!".getBytes());
        ByteBuffer pingFrame = constructPingFrame(pingPayload);

        // 构造 Pong 帧数据
        ByteBuffer pongPayload = ByteBuffer.wrap("Hello, client!".getBytes());
        ByteBuffer pongFrame = constructPongFrame(pongPayload);

        // 打印 Ping 帧和 Pong 帧数据
        System.out.println("Ping 帧数据：" + bytesToHex(pingFrame.array()));
        System.out.println("Pong 帧数据：" + bytesToHex(pongFrame.array()));
    }

    public static ByteBuffer constructPingFrame(ByteBuffer payload) {
        ByteBuffer frame = ByteBuffer.allocate(payload.remaining() + 2);
        frame.put((byte) 0x89);  // FIN = 1, Opcode = 9 (Ping)
        frame.put((byte) 0x80);  // Mask = 1, Payload Length = 0

        frame.put(payload.array(), payload.position(), payload.remaining());
        frame.flip();

        return frame;
    }

    public static ByteBuffer constructPongFrame(ByteBuffer payload) {
        ByteBuffer frame = ByteBuffer.allocate(payload.remaining() + 2);
        frame.put((byte) 0x8A);  // FIN = 1, Opcode = 10 (Pong)
        frame.put((byte) 0x80);  // Mask = 1, Payload Length = 0

        frame.put(payload.array(), payload.position(), payload.remaining());
        frame.flip();

        return frame;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
