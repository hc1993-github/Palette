package com.example.palette.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class NettyClientHelper extends SimpleChannelInboundHandler<String> {
    private static final String TAG = "ClientHelper";
    private String ip;
    private int port;
    private Channel channel;
    private EventLoopGroup eventLoopGroup;
    private ClientListener listener;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case -1:
                    if (listener != null) {
                        listener.connectFail("连接服务器失败");
                    }
                    break;
                case 0:
                    if (listener != null) {
                        listener.connectSuccess("连接服务器成功");
                    }
                    break;
                case 1:
                    if (listener != null) {
                        listener.receiveMessage((String) msg.obj);
                    }
                    break;
            }
        }
    };

    public NettyClientHelper(String ip, int port, ClientListener listener) {
        this.ip = ip;
        this.port = port;
        this.listener = listener;
    }

    public void connect() {
        eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(NettyClientHelper.this);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
            channel = channelFuture.channel();
            handler.sendEmptyMessage(0);
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(-1);
        }
    }

    public void sendMessage(String message) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(message);
        }
    }

    public void disconnect() {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        if (!eventLoopGroup.isShutdown()) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.d(TAG, "客户端上线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.d(TAG, "客户端下线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        Log.d(TAG, "未知异常");
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        Log.d(TAG, "接收到消息");
        Message message = Message.obtain();
        message.what = 1;
        message.obj = msg;
        handler.sendMessage(message);
    }

    public interface ClientListener {
        void connectSuccess(String message);

        void connectFail(String message);

        void receiveMessage(String message);
    }
}
