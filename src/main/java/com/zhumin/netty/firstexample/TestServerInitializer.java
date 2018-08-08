package com.zhumin.netty.firstexample;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Created by charleszhu on 2017/5/16.
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel>{


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline cp = ch.pipeline();

        // 这里不要使用单例模式
        cp.addLast("HttpServerCodec", new HttpServerCodec());
        cp.addLast("TestHttpServerhandler", new TestHttpServerhandler());
    }


}
