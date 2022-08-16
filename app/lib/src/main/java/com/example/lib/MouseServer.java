package com.example.lib;

import java.awt.Robot;
import java.awt.event.InputEvent;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;


@ChannelHandler.Sharable
public class MouseServer extends SimpleChannelInboundHandler<String> {
    private EventLoopGroup connectGroup;
    private EventLoopGroup workGroup;
    private int port;
    private Robot robot;
    public MouseServer(int port) {
        try {
            this.port = port;
            this.robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void start() {
        connectGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(connectGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(MouseServer.this);
                        }
                    });
            server.bind(port).sync();
            while (!connectGroup.isShutdown() && !workGroup.isShutdown()){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        connectGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        if(msg.equals("stop")){
            stop();
        }else if(msg.equals("click")){
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }else if (msg.contains(".")){
            String[] split = msg.split("\\.");
            robot.mouseMove(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
        }
    }
}
