package com.zhumin.netty.thirdexample;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by charleszhu on 2017/6/1.
 */
public class MyChatClient {

    public static void main(String[] args) throws Exception {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new MyChatClientInitializer());

//            ChannelFuture future = bootstrap.connect("localhost",8899).sync();
            // 获取channel ， 因为是死循环接受消息，所以也用不到 close 方法。
            Channel channel = bootstrap.connect("localhost",8899).sync().channel();

            for(;;) {
                BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
                channel.writeAndFlush(bf.readLine() + "\r\n");
            }

        } finally {
            eventLoopGroup.shutdownGracefully();
        }

    }

}
