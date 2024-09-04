package com.zyp.proxy.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
    private static final int PORT = 8889;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Server.run();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public static void run() throws IOException {

        // 创建服务器端的通道和选择器
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        Selector selector = Selector.open();

        // 绑定端口并设置为非阻塞模式
        serverChannel.socket().bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);

        // 向选择器注册通道，并监听接收连接事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started on port " + PORT);

        while (true) {
            // 选择就绪的通道
            selector.select();
            System.out.println("selector select");
            // 处理就绪的事件
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                System.out.println("key hasNext");
                if (key.isAcceptable()) {
                    // 处理接收连接事件
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = server.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);

                    System.out.println("New client connected: " + clientChannel.getRemoteAddress());
                } else if (key.isReadable()) {
                    // 处理读事件
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                    int bytesRead = clientChannel.read(buffer);

                    if (bytesRead > 0) {
                        buffer.flip();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        String message = new String(data).trim();

                        System.out.println("Received from client " + clientChannel.getRemoteAddress() + ": " + message);

                        // 回显消息给客户端
                        ByteBuffer responseBuffer = ByteBuffer.wrap(data);
                        clientChannel.write(responseBuffer);
                    } else {
                        // 客户端断开连接
                        key.cancel();
                        clientChannel.close();
                        System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
                    }
                }
            }
        }
    }
}
