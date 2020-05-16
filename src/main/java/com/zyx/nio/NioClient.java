package com.zyx.nio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 大体上的流程跟服务端差不多，只是其中有些细节的部分与服务端有所区别，注意看注释部分
 */
public class NioClient {

    public static void main(String[] args) throws Exception {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            // 注意这里的链接事件
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress("localhost", 8899));

            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                for (SelectionKey selectionKey : selectionKeys) {
                    if (selectionKey.isConnectable()) {
                        SocketChannel client = (SocketChannel) selectionKey.channel();

                        // 判断链接是不是处在已经挂起的状态
                        if (client.isConnectionPending()) {
                            // 完成链接
                            client.finishConnect();

                            ByteBuffer writeBuffer = ByteBuffer.allocate(512);
                            writeBuffer.put((LocalDateTime.now() + "链接成功").getBytes());
                            writeBuffer.flip();
                            client.write(writeBuffer);

                            // 上面是将完成链接的消息发送出去，此时已经与服务端建立了一个双工的通信通道
                            // 因为客户端是监听的键盘输入，这是一个阻塞的操作，所以我们需要单独起一个线程来处理这个部分的功能
                            ExecutorService executorService = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());
                            executorService.submit(() -> {
                                while (true) {
                                    try {
                                        // 读取标准输入，然后输出给服务端
                                        writeBuffer.clear();
                                        InputStreamReader input = new InputStreamReader(System.in);
                                        BufferedReader br = new BufferedReader(input);
                                        String message = br.readLine();
                                        writeBuffer.put(message.getBytes());
                                        writeBuffer.flip();
                                        client.write(writeBuffer);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        client.register(selector, SelectionKey.OP_READ);
                    }else if (selectionKey.isReadable()) {
                        SocketChannel client = (SocketChannel)selectionKey.channel();

                        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

                        int count = client.read(byteBuffer);
                        if (count > 0) {
                            String receivedMessage = new String(byteBuffer.array(), 0, count);
                            System.out.println(receivedMessage);
                        }
                    }
                    selectionKeys.remove(selectionKey);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
