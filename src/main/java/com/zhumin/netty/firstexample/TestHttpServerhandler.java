package com.zhumin.netty.firstexample;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * Created by charleszhu on 2017/5/16.
 */
public class TestHttpServerhandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 用于接受应用请求
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        /**
         * 这里会发现调用两次。
         * 也就是说一次请求会被调用两次
         */
        System.out.println("调用：channelRead0");
        // 打印出msg 可能的类
        /**
         * class io.netty.handler.codec.http.DefaultHttpRequest
         * class io.netty.handler.codec.http.LastHttpContent$1
         */
        System.out.println(msg.getClass());
        // 打印出远程的地址
        System.out.println(ctx.channel().remoteAddress());

        if (msg instanceof HttpRequest) {

            HttpRequest httpRequest = (HttpRequest) msg;
            // 打印出方法名
            System.out.println(httpRequest.method().name());

            // 针对chrome会请求图标的请求做处理
            URI uri = new URI(httpRequest.uri());
            if("/favicon.ico".equals(uri.getPath())){
                System.out.println("请求favicon");
                return;
            }

            // 生成返回内容
            ByteBuf content = Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8);
            // 生成返回的response
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    content);
            // 设置headers
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            // 返回，使用 ctx.writeAndFlush , 只使用 writer 不会立马返回，而是放在缓冲区中
            ctx.writeAndFlush(response);
            // 这里这样使用，并不是很恰当，实际情况要复杂很多，你要判断，请求是否是HTTP 1.0 / 或者 HTTP 1.1 的
            ctx.channel().close();
        }

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel Registered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel Unregistered");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel Active");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel Inactive");
        super.channelInactive(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handler Added");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handler Removed");
        super.handlerRemoved(ctx);
    }
}
