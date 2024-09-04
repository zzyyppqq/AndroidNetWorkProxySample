package com.zyp.proxy;

import com.zyp.proxy.print.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

public class NioServer {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    new NioServer().run();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
//        try {
//            Thread.sleep(3000L);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        try {
            new NioClient().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        Log.info("server serverSocketChannel.open()");
        //1. 获取服务端通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //2. 切换非阻塞模式
        serverSocketChannel.configureBlocking(false);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.info("server bind start");
        //4. 绑定端口号
        serverSocketChannel.bind(new InetSocketAddress(20000));
        Log.info("server bind end");
        //5. 获取 selector 选择器
        Selector selector = Selector.open();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //6. 通道注册到选择器，进行监听
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        Log.info("server register OP_ACCEPT");

        //7. 选择器进行轮训，进行后续操作
        loop:while (true) {
            selector.select();
            Log.info("server select: " + selector.select());
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            // 循环
            while (keys.hasNext()) {
                // 获取就绪状态
                SelectionKey key = keys.next();
                keys.remove();
                // 操作判断
                if (key.isAcceptable()) {
                    Log.info("server new connection");
                    // 获取连接
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    // 切换非阻塞模式
                    socketChannel.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(20);
                    Log.info("server OP_READ buffer: " + Arrays.toString(buffer.array()));
                    socketChannel.register(selector, SelectionKey.OP_READ, buffer);
                    Log.info("server register OP_READ");
                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    int readByte = socketChannel.read(buffer);
                    if (readByte < 0) {
                        socketChannel.close();
                        break loop;
                    }
                    if (!buffer.hasRemaining()) {
                        buffer.flip();
                        Log.info("server OP_WRITE buffer: " + Arrays.toString(buffer.array()));
                        socketChannel.register(selector, SelectionKey.OP_WRITE, buffer);
                        Log.info("server register OP_WRITE");
                        // 对已注册的事件类型进行修改，包括添加、删除和修改事件
                        //key.interestOps(SelectionKey.OP_WRITE);
                    }
                } else if (key.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    int writeByte = socketChannel.write(buffer);
                    Log.info("server writeByte: " + writeByte);
                    if(!buffer.hasRemaining()){
                        buffer.clear();
                        Log.info("server OP_READ buffer: " + Arrays.toString(buffer.array()));
                        socketChannel.register(selector, SelectionKey.OP_READ, buffer);
                        Log.info("server register OP_READ");
                        //key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        }
        selector.close();
    }

}
