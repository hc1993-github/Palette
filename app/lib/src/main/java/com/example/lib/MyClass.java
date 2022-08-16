package com.example.lib;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class MyClass {
    public static void main(String[] args) {
        try {
            MouseServer mouseServer = new MouseServer(9999);
            mouseServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String printString(String checkTime, int range, String currentTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long checkms = format.parse(checkTime).getTime();
        long currentms = format.parse(currentTime).getTime();
        long per = 60 * 60 * 1000;
        if (currentms > checkms + range * per) {
            return "yin xing";
        }
        if (range <= 24) {
            return "in " + range + " hours";
        }
        if (currentms > (range / 24) * 24 * per + checkms) {
            return String.valueOf(range);
        }
        if((currentms - checkms)%(24 * per)==0){
            long m = (currentms - checkms) / (24 * per);
            return String.valueOf(m*24);
        }else {
            long n = (currentms - checkms) / (24 * per)+1;
            return String.valueOf(n*24);
        }
    }
    public static String getRandomString(int length){
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<length;i++){
            int nextInt = random.nextInt(62);
            builder.append(str.charAt(nextInt));
        }
        return builder.toString();
    }
    public static boolean isMobliePhone(String mobilePhone){
        Pattern pattern = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}");
        Matcher m = pattern.matcher(mobilePhone);
        return m.matches();
    }

    public static boolean isEmail(String email){
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
    }

    public static void createFile(int line,float scale){
        //1080/(480/160)  宽度/屏幕像素密度/160  --->sw-360dp
        //dp=px/density  density=dpi/160
        //设基准为380dp 则scale=360/380
        try {
            File file = new File("D://test.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            DecimalFormat format = new DecimalFormat("#.0");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for(int i=1;i<line+1;i++){
                writer.write("<dimen name=\"sp_size_"+i+"\">"+format.format(i*scale)+"sp</dimen>");
                writer.write("\n");
            }
            writer.flush();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static String getMd5ByFile(File file){
        FileInputStream in =null ;
        StringBuffer sb = new StringBuffer();
        try {
            in = new FileInputStream(file);
            FileChannel channel = in.getChannel();
            long position = 0;
            long total = file.length();
            long page = 1024 * 1024 * 500;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            while (position < total) {
                long size = page <= total - position ? page : total - position;
                MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, position, size);
                position += size;
                md5.update(byteBuffer);
            }
            byte[] b = md5.digest();

            for (int i = 0; i < b.length; i++) {
                sb.append(byteToChars(b[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString().toLowerCase();
    }
    private static char[] byteToChars(byte b) {
        int h = ((b & 0xf0) >> 4);
        int l = (b & 0x0f);
        char[] r = new char[2];
        r[0] = intToChart(h);
        r[1] = intToChart(l);

        return r;
    }
    private static char intToChart(int i) {
        if (i < 0 || i > 15) {
            return ' ';
        }
        if (i < 10) {
            return (char) (i + 48);
        } else {
            return (char) (i + 55);
        }
    }

    @ChannelHandler.Sharable
    public static class MouseServer extends SimpleChannelInboundHandler<String> {
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
}