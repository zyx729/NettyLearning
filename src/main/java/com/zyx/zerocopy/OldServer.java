package com.zyx.zerocopy;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author create by 张宇轩(yuxuan.zhang01@ucarinc.com)
 * @since 2019/5/22 11:24
 */
public class OldServer {

    public static void main(String[] args) throws Exception {
        // 创建serverSocket并绑定到8899端口上
        ServerSocket serverSocket = new ServerSocket(8899);

        // 死循环等待客户端连接
        while (true) {
            // 使用accept方法等待连接，一旦连接上就返回一个socket对象表示当前连上的链接
            // accept本身是一个阻塞的方法
            Socket socket = serverSocket.accept();
            // Socket本身是通过输入输出流获取到相应的InputStream和OutputStream，与对端建立好连接然后进行数据发送
            // 我们这里读取一个文件，文件本身是一个二进制的信息，所以我们这里使用DataInputStream
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            // 定义缓冲区 字节数组
            byte[] byteArray = new byte[4096];
            // 死循环 不断获取到对端发送过来的数据
            while (true) {
                // 不断的将对端发来的数据读到byteArray中，每次最多读的长度就是byteArray的长度
                // 返回的read代表实际读到的长度
                // 这个方法开始是阻塞的直到输入的数据是可用的时候或者文件的结尾已经被探测到或者抛出异常
                int read = dataInputStream.read(byteArray, 0, byteArray.length);
                // read为0时代表没有东西被读到
                // read为-1时代表已经读完了
                if (-1 == read) {
                    break;
                }
            }
        }
    }
}
