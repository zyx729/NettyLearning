package com.zyx.nio;

import java.nio.ByteBuffer;

public class NioTest6 {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);

        for (int i = 0; i < byteBuffer.capacity(); i++) {
            byteBuffer.put((byte)i);
        }


        byteBuffer.position(2);
        byteBuffer.limit(6);

        // slice的长度是根据上面的position和limit来的，含左不含右
        ByteBuffer sliceBuffer = byteBuffer.slice();

        for (int i = 0; i < sliceBuffer.capacity(); i++) {
            byte a = sliceBuffer.get(i);
            // byte转int的一个小技巧
            a *= 2;
            sliceBuffer.put(i, a);
        }

        byteBuffer.position(0);
        byteBuffer.limit(byteBuffer.capacity());

        while (byteBuffer.hasRemaining()) {
            System.out.println(byteBuffer.get());
        }

    }
}
