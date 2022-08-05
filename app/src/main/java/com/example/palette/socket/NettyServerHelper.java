package com.example.palette.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
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
public class NettyServerHelper extends SimpleChannelInboundHandler<String> {
    private static final String TAG = "ServerHelper";
    private int port;
    private ServerListener listener;
    private EventLoopGroup connectGroup;
    private EventLoopGroup workGroup;
    List<Channel> channels;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    if (listener != null) {
                        listener.receiveMessage((String) msg.obj);
                    }
                    break;
            }
        }
    };

    public NettyServerHelper(int port, ServerListener listener) {
        this.port = port;
        this.listener = listener;
        this.channels = new ArrayList<>();
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
                            pipeline.addLast(NettyServerHelper.this);
                        }
                    });
            server.bind(port).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        for (Channel channel : channels) {
            channel.writeAndFlush("服务器关闭");
            channel.close();
        }
        connectGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.d(TAG, "客户端上线");
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.d(TAG, "客户端下线");
        channels.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Log.d(TAG, "未知异常");
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        Log.d(TAG, "接收到消息");
        Message message = Message.obtain();
        message.what = 1;
        message.obj = msg;
        handler.sendMessage(message);
        for (Channel channel : channels) {
            channel.writeAndFlush(msg);
            //dosomething()
        }
    }

    public interface ServerListener {
        void receiveMessage(String message);
    }
}
