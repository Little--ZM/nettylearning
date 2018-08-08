package com.zhumin.netty.thirdexample;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Created by charleszhu on 2017/6/1.
 */
public class MyChatServerHandler extends SimpleChannelInboundHandler<String> {

    // channelGroup 用于保存一获取的连接
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        Channel channel = ctx.channel();

        channelGroup.forEach( ch -> {

            // 判断是否是发给自己的消息
            if(channel != ch) {
                ch.writeAndFlush(channel.remoteAddress() + " 发送的消息: " + msg + "\n");
            } else {
                channel.writeAndFlush("【自己】" + msg + "\n");
            }
        });


    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("【服务器】- " + channel.remoteAddress() + " 加入\n");
        // 之所以先 flush 再加入的原因是，后加入的handler 没必要收到自己上线的消息，所以逻辑上加入后端
        channelGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("【服务器】- " + channel.remoteAddress() + " 离开\n");
        // 这里不需要 channelGroup.remove； 因为netty 会自动帮你处理
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 上线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 下线");
    }
}
