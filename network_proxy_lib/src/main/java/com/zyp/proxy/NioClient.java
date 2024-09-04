package com.zyp.proxy;

import static com.zyp.proxy.NioMain.SIZE;

import com.zyp.proxy.print.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;


public class NioClient {

    public static void main(String[] args) {
        try {
            new NioClient().run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void run() throws IOException {
        Selector selector = Selector.open();
        Log.info("client socketChannel.open()");
        //1. 获取通道，绑定主机和端口号
        SocketChannel socketChannel = SocketChannel.open();
        //2. 切换到非阻塞模式
        socketChannel.configureBlocking(false);
        // connect主要用在客户端
        Log.info("client connect start");
        socketChannel.connect(new InetSocketAddress(20000));
        Log.info("client connect end");
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        Log.info("client register OP_CONNECT");

        ByteBuffer buffer = ByteBuffer.allocate(20);
        Random rand = new Random();
        while(buffer.hasRemaining()){
            buffer.put((byte) (rand.nextInt() % 128));
        }
        buffer.flip();

        loop:while (true) {
            selector.select();
            Log.info("client select");
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while(keys.hasNext()){
                Log.info("client keys next");
                SelectionKey key = keys.next();
                keys.remove();
                if(key.isConnectable()){
                    Log.info("client isConnectable");
                    if(!socketChannel.finishConnect()){
                        throw new IOException("client connect Fail");
                    }
                    Log.info("client Connected");
                    socketChannel.register(selector, SelectionKey.OP_WRITE);
                    Log.info("client register OP_WRITE");
                }

                if(key.isWritable()){
                    socketChannel.write(buffer);
                    if(!buffer.hasRemaining()){
                        Log.info("client write: "+ Arrays.toString(buffer.array()));
                        ByteBuffer readBuf = ByteBuffer.allocate(SIZE);
                        socketChannel.register(selector, SelectionKey.OP_READ, readBuf);
                        Log.info("client register OP_READ");
                    }
                }

                if(key.isReadable()){
                    ByteBuffer readBuf = (ByteBuffer) key.attachment();
                    socketChannel.read(readBuf);
                    if(!readBuf.hasRemaining()){
                        Log.info("client read: "+ Arrays.toString(readBuf.array()));
                    }
                    socketChannel.close();
                    break loop;
                }
            }
        }
        socketChannel.close();
        Log.info("client close");
    }

}
