package com.zyx.zerocopy;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author create by 张宇轩(yuxuan.zhang01@ucarinc.com)
 * @since 2019/5/22 11:29
 */
public class OldClient {

    public static void main(String[] args) throws Exception {
        // 建立socket连接
        Socket socket = new Socket("localhost", 8899);
        // 指定文件 要稍微大一点才能对比出效果
        String fileName = "D:\\迅雷下载\\40.gvg122\\gvg122.mp4";
        // 首先获取文件的输入流 通过这个输入流读取文件数据
        InputStream inputStream = new FileInputStream(fileName);
        // 包装socket的输出流使用DataOutputStream将读到的文件数据往外写
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        byte[] buffer = new byte[4096];
        long read;
        long total = 0;

        long startTime = System.currentTimeMillis();

        while ((read = inputStream.read(buffer)) >= 0) {
                total += read;
                dataOutputStream.write(buffer);
        }

        System.out.println("发送总字节数：" + total + "，耗时：" + (System.currentTimeMillis() - startTime));

        dataOutputStream.close();
        socket.close();
        inputStream.close();
    }
}
