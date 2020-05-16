package com.zyx.zerocopy;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author create by 张宇轩(yuxuan.zhang01@ucarinc.com)
 * @since 2019/5/22 13:44
 */
public class NewIOServer {

    public static void main(String[] args) throws Exception {
        // 指定端口号
        InetSocketAddress address = new InetSocketAddress(8899);

        // 使用Open打开一个serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // serverSocketChannel的socket方法返回一个与当前channel关联的服务端的serverSocket对象
        ServerSocket serverSocket = serverSocketChannel.socket();
        // 这个要看方法的JAVA DOC
        serverSocket.setReuseAddress(true);
        // 绑定端口地址
        serverSocket.bind(address);

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        while (true) {
            // 准备客户端的连接，当客户端连接之后会返回一个连接了的socketChannel对象
            // 如果当前这个serverSocketChannel是处在非阻塞状态，那么这个方法会立马返回一个Null。
            // 否则就会一直阻塞直到有连接建立或者抛出IO异常
            // 无论当前这个serverSocketChannel是否是阻塞模式，返回的这个socketChannel一定是出于阻塞模式的
            SocketChannel socketChannel = serverSocketChannel.accept();
            // 配置为阻塞的 其实加不加无所谓 上面已经说了 这个socketChannel一定是阻塞的 除非手动改成非阻塞
            // 如果有selector的话一定要改成非阻塞
            socketChannel.configureBlocking(true);

            int read = 0;

            // 不等于负一代表有数据不断的进来
            while (-1 != read) {
                // 每次将数据读到byteBuffer中
                read = socketChannel.read(byteBuffer);

                // 对于byteBuffer的读写操作，这里其实应该是使用flip方法，但是我们这里不涉及写，所以不用flip
                // 但是如果不对byteBuffer进行处理也不行，因为读的文件大于ByteBuffer的底层数组大小
                // 那么读一次就读满了，position在第二次进行读取的时候，仍然处于最大的位置是读不了新数据的
                // 所以我们这里在每次读完之后要将position移回到初始位置，使用rewind方法
                byteBuffer.rewind();
            }
        }
    }
}
