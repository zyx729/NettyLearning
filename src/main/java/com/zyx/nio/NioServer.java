package com.zyx.nio;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NioServer {
    // 作为一个聊天程序，消息要能准确的分发到不同的客户端上，就必须要有一个容器去缓存各个客户端，并根据一定的标识来区分不同的客户端
    // 这里使用一个map，它的key使用UUID，value就是具体的客户端的channel对象
    private static Map<String, SocketChannel> clientMap = new HashMap();

    public static void main(String[] args) throws Exception {
        // 使用open方法打开一个serverSocketChannel对象
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 配置成非阻塞
        serverSocketChannel.configureBlocking(false);
        // 获取到服务端对应的serverSocket对象
        ServerSocket serverSocket = serverSocketChannel.socket();
        // 绑定端口号
        serverSocket.bind(new InetSocketAddress(8899));
        // 创建selector对象
        Selector selector = Selector.open();
        // 将serverSocketChannel注册到selector上，关注链接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 网络程序的主程序都是死循环
        while (true) {
            try {
                // 调用select方法阻塞，等待关注的事件发生
                // 返回一个Int，就是关注的事件发生的数量
                selector.select();
                // 当执行到这一行时，代表上面的select方法已经返回
                // 也就是关注的事件已经发生，那么我们就可以获取selectionKey构成的事件集合
                // 获取selectionKey集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                // 遍历集合，获取里面的selectionKey，根据不同事件来进行不同的处理
                // 目前只关注了一个accept事件，所以当这个事件发生的时候我们再往selector上注册别的事件
                selectionKeys.forEach(selectionKey -> {
                    final SocketChannel client;

                    try {
                        // 判断事件类型
                        if (selectionKey.isAcceptable()) {
                            // 进入这个判断之后就代表着链接事件发生
                            // 我们就可以通过selectionKey获取与之链接的channel对象
                            // 然后将这个channel继续注册到selector上去，进而关注这个已链接的channel后续的事件
                            // 这里为什么强转是因为.channel方法返回的是SelectableChannel对象，
                            // 它是一个父类，serverSocketChannel是它的子类
                            // 那么为什么我们就确定一定能强转成功呢？
                            // 那是因为在最开始我们在关注accept事件的时候，仅仅只将serverSocketChannel注册到selector上了
                            // 如果这个accept事件发生，那么这里反向获取的channel对象就一定是前面第一步注册的那个serverSocketChannel对象
                            ServerSocketChannel server = (ServerSocketChannel)selectionKey.channel();
                            // 这个时候serverSocketChannel对象就用不上了，
                            // 它仅仅只用在建立链接时，所以后面就开始对client这个socketChannel对象进行注册
                            client = server.accept();
                            client.configureBlocking(false);
                            // 链接建立完成之后，就开始关注read事件
                            client.register(selector, SelectionKey.OP_READ);

                            String key = "【" + UUID.randomUUID().toString() + "】";

                            clientMap.put(key, client);
                        }else if (selectionKey.isReadable()) {
                            // 进入这个判断之后就代表着读事件被触发
                            client = (SocketChannel) selectionKey.channel();
                            ByteBuffer readBuffer = ByteBuffer.allocate(512);

                            int count = client.read(readBuffer);

                            if (count > 0) {
                                readBuffer.flip();

                                Charset charset = Charset.forName("utf-8");
                                String receivedMessage = String.valueOf(charset.decode(readBuffer).array());

                                System.out.println(client + ": " + receivedMessage);

                                // 分发给已链接的所有的客户端
                                String sendKey = null;

                                for (Map.Entry<String, SocketChannel> entry : clientMap.entrySet()) {
                                    if (client == entry.getValue()) {
                                        sendKey = entry.getKey();
                                        break;
                                    }
                                }
                                for (Map.Entry<String, SocketChannel> entry : clientMap.entrySet()) {
                                   SocketChannel value = entry.getValue();

                                   ByteBuffer writeBuffer = ByteBuffer.allocate(512);
                                   writeBuffer.put((sendKey + ": " + receivedMessage).getBytes());
                                   writeBuffer.flip();

                                   value.write(writeBuffer);
                                }
                            }
                        }
                        // 一定要从集合中将已经使用过的selectionKey删除掉
                        selectionKeys.remove(selectionKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
