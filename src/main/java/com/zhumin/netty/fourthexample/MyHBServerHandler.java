package com.zhumin.netty.fourthexample;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import static io.netty.handler.timeout.IdleState.READER_IDLE;

/**
 * Created by charleszhu on 2017/6/1.
 */
public class MyHBServerHandler extends ChannelInboundHandlerAdapter {

    // 这里继承的是一个适配器, 是之前用的那个 SimpleChannelInboundHandler 的父类

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent) {

            IdleStateEvent event = (IdleStateEvent) evt;

            String eventType = null;

            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }

            System.out.println(ctx.channel().remoteAddress() + " 超时事件: " + eventType);


        }


    }
}
