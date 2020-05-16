package nettyDemo.handlerDemo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyByteToLongDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("decode invoke");
        System.out.println(in.readableBytes());

        // 要把byte转换成Long类型，Long类型占据了8个字节，所以要进行判断，如果不够8个字节就不能做转换
        if (in.readableBytes() >= 8) {
            out.add(in.readLong());
        }
    }
}
