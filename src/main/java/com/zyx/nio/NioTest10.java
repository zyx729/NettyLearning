package com.zyx.nio;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class NioTest10 {

    public static void main(String[] args) throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile("NioTest9.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();

        // 参数含义：
        // 1.position 从哪开始锁
        // 2.size 锁多长
        // 3.shared 共享锁还是排他锁 true表示共享锁，false表示排他锁
        FileLock fileLock = fileChannel.lock(3, 6, true);

        System.out.println("valid :" + fileLock.isValid());
        System.out.println("lock type : " + fileLock.isShared());

        fileLock.release();
        randomAccessFile.close();
    }
}
