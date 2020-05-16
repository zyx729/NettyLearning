package com.zyx.protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;

public class MyProtoBufClientHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.MyMessage msg) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MyDataInfo.MyMessage myMessage = null;
        for (int i = 0; i < 10; i++) {
            int random = new Random().nextInt(3);

            if (random == 0) {
                myMessage = MyDataInfo.MyMessage.newBuilder().
                        setDataType(MyDataInfo.MyMessage.DataType.StudentType).
                        setStudent(MyDataInfo.Student.newBuilder().setAddress("wh").setAge(20).setName("张三")).build();
            } else if (random == 1) {
                myMessage = MyDataInfo.MyMessage.newBuilder().
                        setDataType(MyDataInfo.MyMessage.DataType.DogType).
                        setDog(MyDataInfo.Dog.newBuilder().setAge(20).setName("旺财")).build();
            } else {
                myMessage = MyDataInfo.MyMessage.newBuilder().
                        setDataType(MyDataInfo.MyMessage.DataType.CatType).
                        setCat(MyDataInfo.Cat.newBuilder().setCity("wh").setName("阿喵")).build();
            }
            ctx.channel().writeAndFlush(myMessage);
        }
    }
}
