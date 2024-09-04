package com.zyp.proxy.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8889;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Client.run();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public static void run() throws IOException {
        // 创建客户端通道
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);
        System.out.println("Connect start");
        clientChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
        System.out.println("Connect end");

        // 等待连接完成
        while (!clientChannel.finishConnect()) {
            // 等待连接完成
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Connected to server: " + clientChannel.getRemoteAddress());

        // 从控制台读取输入并发送给服务器
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("exit")) {
                // 输入exit退出聊天
                break;
            }

            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            clientChannel.write(buffer);

            // 读取服务器回应并打印
            ByteBuffer responseBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            int bytesRead = clientChannel.read(responseBuffer);
            if (bytesRead > 0) {
                responseBuffer.flip();
                byte[] data = new byte[responseBuffer.remaining()];
                responseBuffer.get(data);
                String response = new String(data).trim();
                System.out.println("Received from server: " + response);
            }
        }

        // 关闭通道
        clientChannel.close();
        scanner.close();
    }
}
