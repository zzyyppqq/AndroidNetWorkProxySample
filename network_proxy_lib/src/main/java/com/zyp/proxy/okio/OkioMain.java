package com.zyp.proxy.okio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Sink;
import okio.Source;

public class OkioMain {
    public static void main(String[] args) {
        readByteString();
    }

    public static void readByteString() {
        Buffer buffer = new Buffer();
        buffer.writeByte(0x9);
        System.err.println(buffer.readByteString());
        ByteString byteStr = new ByteString(new byte[]{1,2});

    }

    public static void okioStream(String[] args) {
        File file = new File("example.txt");

        try {
            Source source = Okio.source(file);
            BufferedSource bufferedSource = Okio.buffer(source);

            // 将 BufferedSource 转换为 InputStream
            InputStream inputStream = bufferedSource.inputStream();

            // 现在可以像操作普通的 InputStream 一样来操作 Okio 的数据流
            int data;
            while ((data = inputStream.read()) != -1) {
                System.out.print((char) data);
            }

            inputStream.close();
            bufferedSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readByteArray() {
        try {
            Buffer buffer = new Buffer();
            buffer.writeUtf8("Hello, OKIO!");

            MessageDigest md = MessageDigest.getInstance("MD5");
            ByteString hash = ByteString.of(md.digest(buffer.readByteArray()));

            System.out.println("MD5 Hash: " + hash.hex());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void segmentedFileRead() {
        File file = new File("example.txt");

        try {
            Source source = Okio.source(file);
            BufferedSource bufferedSource = Okio.buffer(source);

            Buffer buffer = new Buffer();
            long bytesRead;
            while ((bytesRead = bufferedSource.read(buffer, 8192)) != -1) {
                // 处理每一次读取的数据
                byte[] data = buffer.readByteArray();
                processSegment(data);
                buffer.clear(); // 清空缓冲区
            }

            bufferedSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSegment(byte[] data) {
        // 在这里处理每个数据段
        System.out.println("Read segment: " + new String(data));
    }

    public static void memoryMappedFile() {
        File file = new File("example.txt");

        try {
            Source source = Okio.source(file);
            BufferedSource bufferedSource = Okio.buffer(source);

            Buffer buffer = new Buffer();
            bufferedSource.readAll(buffer);  // 将整个文件内容映射到内存中的缓冲区中

            // 现在可以直接通过 buffer 对象访问文件内容，而无需在每次读取时都进行 I/O 操作
            String fileContent = buffer.readUtf8();
            System.out.println(fileContent);

            bufferedSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFile() {
        File file = new File("example.txt"); // 假设存在一个名为 example.txt 的文本文件

        try {
            Source source = Okio.source(file); // 通过 Okio.source 方法获取文件的源
            BufferedSource bufferedSource = Okio.buffer(source); // 使用 Okio.buffer 方法创建 BufferedSource

            String fileContent = bufferedSource.readUtf8(); // 使用 readUtf8 方法读取文件内容
            System.out.println(fileContent); // 打印文件内容到控制台

            bufferedSource.close(); // 关闭资源
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile() {
        File sourceFile = new File("source.txt");
        File targetFile = new File("target.txt");

        try {
            Source source = Okio.source(sourceFile);
            BufferedSource bufferedSource = Okio.buffer(source);

            Sink target = Okio.sink(targetFile);
            BufferedSink bufferedSink = Okio.buffer(target);

            bufferedSink.writeAll(bufferedSource); // 将源文件内容写入目标文件

            bufferedSource.close();
            bufferedSink.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
