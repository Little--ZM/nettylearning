package com.zhumin.netty.fifthexample;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 因为是基于HTTP 协议之上的，所以需要使用到 HTTP 的编解码技术
        pipeline.addLast(new HttpServerCodec());
        // 按照块来写数据 handler
        pipeline.addLast(new ChunkedWriteHandler());
        // http request 和 response 的一个聚合类
        // netty 会对http 请求做分段的处理；所以在第一个 http 的示例中，有些会调用多次
        pipeline.addLast(new HttpObjectAggregator(8192));

        // web socket netty 的特殊支持
        // 其中 "ws" 是指的是 websocket 协议路径
        // 通常形式   ws://localhost:8899/ws
        // 8899/ws 后面的 ws 就是我传入的 /ws 路径。
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new TextWebSocketFrameHandler());
    }
}
