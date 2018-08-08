package com.zhumin.netty.fourthexample;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by charleszhu on 2017/6/1.
 */
public class MyHBServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline channelPipeline = ch.pipeline();

        channelPipeline.addLast(new IdleStateHandler(5, 7, 10, TimeUnit.SECONDS));
        channelPipeline.addLast(new MyHBServerHandler());
    }

}
