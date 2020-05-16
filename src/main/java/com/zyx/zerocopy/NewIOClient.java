package com.zyx.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @author create by 张宇轩(yuxuan.zhang01@ucarinc.com)
 * @since 2019/5/22 16:40
 */
public class NewIOClient {

    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8899));
        socketChannel.configureBlocking(true);

        String fileName = "D:\\迅雷下载\\40.gvg122\\gvg122.mp4";

        // 通过这种形式获取与文件相关的channel
        FileChannel fileChannel = new FileInputStream(fileName).getChannel();

        long startTime = System.currentTimeMillis();

        // transferFrom 从一个可读取的channel中将数据读取过来
        // transferTo 把channel关联的文件写到WritableByteChannel中 这里就是socketChannel
        // 这里我们使用transferTo
        // 三个参数
        // position：从哪开始
        // count：文件长度，可使用fileChannel.size方法获取
        // WritableByteChannel：写出的通道
        long position = 0;
        while (position < fileChannel.size()) {
            long transferCount = fileChannel.transferTo(position, fileChannel.size(), socketChannel);
            if (transferCount > 0) {
                position += transferCount;
            }
        }

        // 1342527609
        System.out.println("发送字节数：" + position + "，耗时：" + (System.currentTimeMillis()- startTime));

        fileChannel.close();
    }
}
