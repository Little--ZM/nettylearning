package com.zhumin.netty.thirdexample;

import com.zhumin.netty.secondexample.MyServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by charleszhu on 2017/5/31.
 */
public class MyChatServer {

    public static void main(String[] args) throws Exception {


        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new MyChatServerInitializer());

            // childHandler 其实是对 workerGroup 进行操作
            // 其实 Server 也可以有 handler, 而 handler 方法其实是针对 bossGroup
            // 这个意思其实比价容易理解，boss 只是接受请求，worker 是处理请求，所以 Client端一般就只用handler

            ChannelFuture channelFuture = serverBootstrap.bind(8899).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

}
