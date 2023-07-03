package com.hc.other;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
    /**
     *                    **                  **     **
     *      ********     ********        ******    **     *********
     *           **     **    **            **     **     **     **
     *       ** **        **                 *     **     **   * **
     *         **        ***                 **    ** *   **     **
     *        ** **     **  **              ****   *     *
     *       **   **   **    **          ****   ********************
     *                **      **
     */
    public static void main(String[] args) {
        try {
//            MouseServer mouseServer = new MouseServer(9999);
//            mouseServer.start();
//            System.out.print((getMd5ByFile(new File("D:\\HealthQianHeAPP_chsV2.1_3_debug.apk"))));
            createFile(40,500,0.47f);
            System.out.print(pwdCheck("12a"));
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
    public static void createFile(int spline,int dpline,float scale){
        //1080/(480/160)  宽度/屏幕像素密度/160  --->sw-360dp
        //dp=px/density  density=dpi/160
        //设基准为380dp 则scale=360/380
        try {
            File file = new File("D://dimens.xml");
            if(!file.exists()){
                file.createNewFile();
            }
            DecimalFormat format = new DecimalFormat("#.0");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n\t");
            if(scale<=1.0f){
                writer.write("<dimen name=\"sp_size_"+1+"\">"+format.format(1.0)+"sp</dimen>\n");
                writer.write("\t");
            }else {
                writer.write("<dimen name=\"sp_size_"+1+"\">"+format.format(scale)+"sp</dimen>\n");
                writer.write("\t");
            }
            for(int i=2;i<spline+1;i++){
                float temp = i * scale;
                if(temp<1.0f){
                    temp = 1.0f;
                }
                writer.write("<dimen name=\"sp_size_"+i+"\">"+format.format(temp)+"sp</dimen>\n");
                if(i!=spline){
                    writer.write("\t");
                }
            }
            writer.write("\n");
            writer.write("\t");
            writer.write("<dimen name=\"dp_size_"+0+"\">"+0+"dp</dimen>\n");
            writer.write("\t");
            if(scale<=1.0f){
                writer.write("<dimen name=\"dp_size_"+1+"\">"+format.format(1.0)+"dp</dimen>\n");
                writer.write("\t");
            }else {
                writer.write("<dimen name=\"dp_size_"+1+"\">"+format.format(scale)+"dp</dimen>\n");
                writer.write("\t");
            }
            for(int i=2;i<dpline+1;i++){
                float temp = i * scale;
                if(temp<1.0f){
                    temp = 1.0f;
                }
                writer.write("<dimen name=\"dp_size_"+i+"\">"+format.format(temp)+"dp</dimen>\n");
                if(i!=dpline){
                    writer.write("\t");
                }
            }
            writer.write("</resources>");
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

    public static boolean pwdCheck(String value){
        boolean isPass;
        String letter_regex = "^.*[a-zA-Z]+.*$";
        String number_regex = "^.*[0-9]+.*$";
        String special_regex = "^.*[/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}+.*$]";
        String length_regex = "^.{8,}$";
        isPass = value.matches(letter_regex);
        isPass = value.matches(number_regex);
        isPass = value.matches(special_regex);
        isPass = value.matches(length_regex);
        isPass = MyClass.simpleLetterAndNumCheck(value, 3);
        return isPass;
    }

    public static boolean simpleLetterAndNumCheck(String value, int length){
        //是否合法
        boolean isValidate = true;
        //
        int i = 0;
        //计数器
        int counter = 1;
        //
        for(; i < value.length() -1;) {
            //当前ascii值
            int currentAscii = Integer.valueOf(value.charAt(i));
            //下一个ascii值
            int nextAscii = Integer.valueOf(value.charAt(i + 1));
            //满足区间进行判断
            if( (MyClass.rangeInDefined(currentAscii, 48, 57) || MyClass.rangeInDefined(currentAscii, 65, 90) || MyClass.rangeInDefined(currentAscii, 97, 122))
                    && (MyClass.rangeInDefined(nextAscii, 48, 57) || MyClass.rangeInDefined(nextAscii, 65, 90) || MyClass.rangeInDefined(nextAscii, 97, 122)) ) {
                //计算两数之间差一位则为连续
                if(Math.abs((nextAscii - currentAscii)) == 1){
                    //计数器++
                    counter++;
                }else{
                    //否则计数器重新计数
                    counter = 1;
                }
            }
            //满足连续数字或者字母
            if(counter >= length) return !isValidate;
            //
            i++;
        }

        //
        return isValidate;
    }

    public static boolean rangeInDefined(int current, int min, int max) {
        //
        return Math.max(min, current) == Math.min(current, max);
    }

    public static class KeyEvent_example extends JFrame {
        public KeyEvent_example() throws HeadlessException {
            super();
            setBounds(100,100,500,375);
            setTitle("键盘事件实例");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            JLabel label = new JLabel();
            label.setText("备注");
            getContentPane().add(label, BorderLayout.WEST);
            JScrollPane scrollPane = new JScrollPane();
            getContentPane().add(scrollPane,BorderLayout.CENTER);
            JTextArea textArea = new JTextArea();
            textArea.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {

                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    String text = KeyEvent.getKeyText(keyEvent.getKeyCode());
                    System.out.println(keyEvent.getKeyCode());
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {

                }
            });
            scrollPane.setViewportView(textArea);
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
            }else if (msg.contains(".") && !msg.contains(">")){
                String[] split = msg.split("\\.");
                robot.mouseMove(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
            }else if(msg.equals("rightclick")){
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            }else if(msg.equals("up")){
                robot.mouseWheel(-1);
            }else if(msg.equals("down")){
                robot.mouseWheel(1);
            }else if(msg.contains("Press")){
                msg = msg.replace("Press","").trim();
                if(msg.contains("Esc")){
                    robot.keyPress(KeyEvent.VK_ESCAPE);
                }else if(msg.contains("F1")){
                    robot.keyPress(KeyEvent.VK_F1);
                }else if(msg.contains("F2")){
                    robot.keyPress(KeyEvent.VK_F2);
                }else if(msg.contains("F3")){
                    robot.keyPress(KeyEvent.VK_F3);
                }else if(msg.contains("F4")){
                    robot.keyPress(KeyEvent.VK_F4);
                }else if(msg.contains("F5")){
                    robot.keyPress(KeyEvent.VK_F5);
                }else if(msg.contains("F6")){
                    robot.keyPress(KeyEvent.VK_F6);
                }else if(msg.contains("F7")){
                    robot.keyPress(KeyEvent.VK_F7);
                }else if(msg.contains("F8")){
                    robot.keyPress(KeyEvent.VK_F8);
                }else if(msg.contains("F9")){
                    robot.keyPress(KeyEvent.VK_F9);
                }else if(msg.contains("F10")){
                    robot.keyPress(KeyEvent.VK_F10);
                }else if(msg.contains("F11")){
                    robot.keyPress(KeyEvent.VK_F11);
                }else if(msg.contains("F12")){
                    robot.keyPress(KeyEvent.VK_F12);
                }else if(msg.contains("~")){
                    robot.keyPress(KeyEvent.VK_BACK_QUOTE);
                }else if(msg.contains("!")){
                    robot.keyPress(KeyEvent.VK_1);
                }else if(msg.contains("@")){
                    robot.keyPress(KeyEvent.VK_2);
                }else if(msg.contains("#")){
                    robot.keyPress(KeyEvent.VK_3);
                }else if(msg.contains("$")){
                    robot.keyPress(KeyEvent.VK_4);
                }else if(msg.contains("%")){
                    robot.keyPress(KeyEvent.VK_5);
                }else if(msg.contains("^")){
                    robot.keyPress(KeyEvent.VK_6);
                }else if(msg.contains("&")){
                    robot.keyPress(KeyEvent.VK_7);
                }else if(msg.contains("*")){
                    robot.keyPress(KeyEvent.VK_8);
                }else if(msg.contains("(")){
                    robot.keyPress(KeyEvent.VK_9);
                }else if(msg.contains(")")){
                    robot.keyPress(KeyEvent.VK_0);
                }else if(msg.contains("-")){
                    robot.keyPress(KeyEvent.VK_MINUS);
                }else if(msg.contains("+")){
                    robot.keyPress(KeyEvent.VK_EQUALS);
                }else if(msg.contains("Backspace")){
                    robot.keyPress(KeyEvent.VK_BACK_SPACE);
                }else if(msg.contains("Ins")){
                    robot.keyPress(KeyEvent.VK_INSERT);
                }else if(msg.contains("Home")){
                    robot.keyPress(KeyEvent.VK_HOME);
                }else if(msg.contains("PgUp")){
                    robot.keyPress(KeyEvent.VK_PAGE_UP);
                }else if(msg.contains("Tab")){
                    robot.keyPress(KeyEvent.VK_TAB);
                }else if(msg.contains("Q")){
                    robot.keyPress(KeyEvent.VK_Q);
                }else if(msg.contains("W") && !msg.contains("Win")){
                    robot.keyPress(KeyEvent.VK_W);
                }else if(msg.contains("E") && !msg.contains("Enter") && !msg.contains("End") && !msg.contains("Esc")){
                    robot.keyPress(KeyEvent.VK_E);
                }else if(msg.contains("R")){
                    robot.keyPress(KeyEvent.VK_R);
                }else if(msg.contains("T") && !msg.contains("Tab")){
                    robot.keyPress(KeyEvent.VK_T);
                }else if(msg.contains("Y")){
                    robot.keyPress(KeyEvent.VK_Y);
                }else if(msg.contains("U")&& !msg.contains("PgUp")){
                    robot.keyPress(KeyEvent.VK_U);
                }else if(msg.contains("I") && !msg.contains("Ins")){
                    robot.keyPress(KeyEvent.VK_I);
                }else if(msg.contains("O")){
                    robot.keyPress(KeyEvent.VK_O);
                }else if(msg.contains("P") && !msg.contains("PgUp")&& !msg.contains("PgDn")){
                    robot.keyPress(KeyEvent.VK_P);
                }else if(msg.contains("{")){
                    robot.keyPress(KeyEvent.VK_OPEN_BRACKET);
                }else if(msg.contains("}")){
                    robot.keyPress(KeyEvent.VK_CLOSE_BRACKET);
                }else if(msg.contains("\\")){
                    robot.keyPress(KeyEvent.VK_BACK_SLASH);
                }else if(msg.contains("Del")){
                    robot.keyPress(KeyEvent.VK_DELETE);
                }else if(msg.contains("End")){
                    robot.keyPress(KeyEvent.VK_END);
                }else if(msg.contains("PgDn")){
                    robot.keyPress(KeyEvent.VK_PAGE_DOWN);
                }else if(msg.contains("CapsLock")){
                    robot.keyPress(KeyEvent.VK_CAPS_LOCK);
                }else if(msg.contains("A") && !msg.contains("Alt")){
                    robot.keyPress(KeyEvent.VK_A);
                }else if(msg.contains("S") && !msg.contains("Shift") && !msg.contains("Space")){
                    robot.keyPress(KeyEvent.VK_S);
                }else if(msg.contains("D")&& !msg.contains("PgDn") && !msg.contains("Del")){
                    robot.keyPress(KeyEvent.VK_D);
                }else if(msg.contains("F") && !msg.contains("Fn") && !msg.contains("F1") && !msg.contains("F2") && !msg.contains("F3")
                        && !msg.contains("F4")&& !msg.contains("F5")&& !msg.contains("F6")&& !msg.contains("F7")&& !msg.contains("F8")
                        && !msg.contains("F9")&& !msg.contains("F10")&& !msg.contains("F11")&& !msg.contains("F12")){
                    robot.keyPress(KeyEvent.VK_F);
                }else if(msg.contains("G")){
                    robot.keyPress(KeyEvent.VK_G);
                }else if(msg.contains("H") && !msg.contains("Home")){
                    robot.keyPress(KeyEvent.VK_H);
                }else if(msg.contains("J")){
                    robot.keyPress(KeyEvent.VK_J);
                }else if(msg.contains("K")){
                    robot.keyPress(KeyEvent.VK_K);
                }else if(msg.contains("L") && !msg.contains("CapsLock")){
                    robot.keyPress(KeyEvent.VK_L);
                }else if(msg.contains(";")){
                    robot.keyPress(KeyEvent.VK_SEMICOLON);
                }else if(msg.contains("’")){
                    robot.keyPress(KeyEvent.VK_QUOTE);
                }else if(msg.contains("Enter")){
                    robot.keyPress(KeyEvent.VK_ENTER);
                }else if(msg.contains("Shift")){
                    robot.keyPress(KeyEvent.VK_SHIFT);
                }else if(msg.contains("Z")){
                    robot.keyPress(KeyEvent.VK_Z);
                }else if(msg.contains("X")){
                    robot.keyPress(KeyEvent.VK_X);
                }else if(msg.contains("C") && !msg.contains("CapsLock") && !msg.contains("Ctrl")){
                    robot.keyPress(KeyEvent.VK_C);
                }else if(msg.contains("V")){
                    robot.keyPress(KeyEvent.VK_V);
                }else if(msg.contains("B") && !msg.contains("Backspace")){
                    robot.keyPress(KeyEvent.VK_B);
                }else if(msg.contains("N")){
                    robot.keyPress(KeyEvent.VK_N);
                }else if(msg.contains("M")){
                    robot.keyPress(KeyEvent.VK_M);
                }else if(msg.contains("<")){
                    robot.keyPress(KeyEvent.VK_COMMA);
                }else if(msg.contains(">")){
                    robot.keyPress(KeyEvent.VK_PERIOD);
                }else if(msg.contains("?")){
                    robot.keyPress(KeyEvent.VK_SLASH);
                }else if(msg.contains("Ctrl")){
                    robot.keyPress(KeyEvent.VK_CONTROL);
                }else if(msg.contains("Fn")){

                }else if(msg.contains("Win")){
                    robot.keyPress(KeyEvent.VK_WINDOWS);
                }else if(msg.contains("Alt")){
                    robot.keyPress(KeyEvent.VK_ALT);
                }else if(msg.contains("Space")){
                    robot.keyPress(KeyEvent.VK_SPACE);
                }else if(msg.contains("↑")){
                    robot.keyPress(KeyEvent.VK_UP);
                }else if(msg.contains("↓")){
                    robot.keyPress(KeyEvent.VK_DOWN);
                }else if(msg.contains("←")){
                    robot.keyPress(KeyEvent.VK_LEFT);
                }else if(msg.contains("→")){
                    robot.keyPress(KeyEvent.VK_RIGHT);
                }
            }else if(msg.contains("Release")){
                msg = msg.replace("Release","").trim();
                if(msg.contains("Esc")){
                    robot.keyRelease(KeyEvent.VK_ESCAPE);
                }else if(msg.contains("F1")){
                    robot.keyRelease(KeyEvent.VK_F1);
                }else if(msg.contains("F2")){
                    robot.keyRelease(KeyEvent.VK_F2);
                }else if(msg.contains("F3")){
                    robot.keyRelease(KeyEvent.VK_F3);
                }else if(msg.contains("F4")){
                    robot.keyRelease(KeyEvent.VK_F4);
                }else if(msg.contains("F5")){
                    robot.keyRelease(KeyEvent.VK_F5);
                }else if(msg.contains("F6")){
                    robot.keyRelease(KeyEvent.VK_F6);
                }else if(msg.contains("F7")){
                    robot.keyRelease(KeyEvent.VK_F7);
                }else if(msg.contains("F8")){
                    robot.keyRelease(KeyEvent.VK_F8);
                }else if(msg.contains("F9")){
                    robot.keyRelease(KeyEvent.VK_F9);
                }else if(msg.contains("F10")){
                    robot.keyRelease(KeyEvent.VK_F10);
                }else if(msg.contains("F11")){
                    robot.keyRelease(KeyEvent.VK_F11);
                }else if(msg.contains("F12")){
                    robot.keyRelease(KeyEvent.VK_F12);
                }else if(msg.contains("~")){
                    robot.keyRelease(KeyEvent.VK_BACK_QUOTE);
                }else if(msg.contains("!")){
                    robot.keyRelease(KeyEvent.VK_1);
                }else if(msg.contains("@")){
                    robot.keyRelease(KeyEvent.VK_2);
                }else if(msg.contains("#")){
                    robot.keyRelease(KeyEvent.VK_3);
                }else if(msg.contains("$")){
                    robot.keyRelease(KeyEvent.VK_4);
                }else if(msg.contains("%")){
                    robot.keyRelease(KeyEvent.VK_5);
                }else if(msg.contains("^")){
                    robot.keyRelease(KeyEvent.VK_6);
                }else if(msg.contains("&")){
                    robot.keyRelease(KeyEvent.VK_7);
                }else if(msg.contains("*")){
                    robot.keyRelease(KeyEvent.VK_8);
                }else if(msg.contains("(")){
                    robot.keyRelease(KeyEvent.VK_9);
                }else if(msg.contains(")")){
                    robot.keyRelease(KeyEvent.VK_0);
                }else if(msg.contains("-")){
                    robot.keyRelease(KeyEvent.VK_MINUS);
                }else if(msg.contains("+")){
                    robot.keyRelease(KeyEvent.VK_EQUALS);
                }else if(msg.contains("Backspace")){
                    robot.keyRelease(KeyEvent.VK_BACK_SPACE);
                }else if(msg.contains("Ins")){
                    robot.keyRelease(KeyEvent.VK_INSERT);
                }else if(msg.contains("Home")){
                    robot.keyRelease(KeyEvent.VK_HOME);
                }else if(msg.contains("PgUp")){
                    robot.keyRelease(KeyEvent.VK_PAGE_UP);
                }else if(msg.contains("Tab")){
                    robot.keyRelease(KeyEvent.VK_TAB);
                }else if(msg.contains("Q")){
                    robot.keyRelease(KeyEvent.VK_Q);
                }else if(msg.contains("W") && !msg.contains("Win")){
                    robot.keyRelease(KeyEvent.VK_W);
                }else if(msg.contains("E") && !msg.contains("Enter") && !msg.contains("End") && !msg.contains("Esc")){
                    robot.keyRelease(KeyEvent.VK_E);
                }else if(msg.contains("R")){
                    robot.keyRelease(KeyEvent.VK_R);
                }else if(msg.contains("T") && !msg.contains("Tab")){
                    robot.keyRelease(KeyEvent.VK_T);
                }else if(msg.contains("Y")){
                    robot.keyRelease(KeyEvent.VK_Y);
                }else if(msg.contains("U")&& !msg.contains("PgUp")){
                    robot.keyRelease(KeyEvent.VK_U);
                }else if(msg.contains("I") && !msg.contains("Ins")){
                    robot.keyRelease(KeyEvent.VK_I);
                }else if(msg.contains("O")){
                    robot.keyRelease(KeyEvent.VK_O);
                }else if(msg.contains("P") && !msg.contains("PgUp")&& !msg.contains("PgDn")){
                    robot.keyRelease(KeyEvent.VK_P);
                }else if(msg.contains("{")){
                    robot.keyRelease(KeyEvent.VK_OPEN_BRACKET);
                }else if(msg.contains("}")){
                    robot.keyRelease(KeyEvent.VK_CLOSE_BRACKET);
                }else if(msg.contains("\\")){
                    robot.keyRelease(KeyEvent.VK_BACK_SLASH);
                }else if(msg.contains("Del")){
                    robot.keyRelease(KeyEvent.VK_DELETE);
                }else if(msg.contains("End")){
                    robot.keyRelease(KeyEvent.VK_END);
                }else if(msg.contains("PgDn")){
                    robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
                }else if(msg.contains("CapsLock")){
                    robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
                }else if(msg.contains("A") && !msg.contains("Alt")){
                    robot.keyRelease(KeyEvent.VK_A);
                }else if(msg.contains("S") && !msg.contains("Shift") && !msg.contains("Space")){
                    robot.keyRelease(KeyEvent.VK_S);
                }else if(msg.contains("D")&& !msg.contains("PgDn") && !msg.contains("Del")){
                    robot.keyRelease(KeyEvent.VK_D);
                }else if(msg.contains("F") && !msg.contains("Fn") && !msg.contains("F1") && !msg.contains("F2") && !msg.contains("F3")
                        && !msg.contains("F4")&& !msg.contains("F5")&& !msg.contains("F6")&& !msg.contains("F7")&& !msg.contains("F8")
                        && !msg.contains("F9")&& !msg.contains("F10")&& !msg.contains("F11")&& !msg.contains("F12")){
                    robot.keyRelease(KeyEvent.VK_F);
                }else if(msg.contains("G")){
                    robot.keyRelease(KeyEvent.VK_G);
                }else if(msg.contains("H") && !msg.contains("Home")){
                    robot.keyRelease(KeyEvent.VK_H);
                }else if(msg.contains("J")){
                    robot.keyRelease(KeyEvent.VK_J);
                }else if(msg.contains("K")){
                    robot.keyRelease(KeyEvent.VK_K);
                }else if(msg.contains("L") && !msg.contains("CapsLock")){
                    robot.keyRelease(KeyEvent.VK_L);
                }else if(msg.contains(";")){
                    robot.keyRelease(KeyEvent.VK_SEMICOLON);
                }else if(msg.contains("’")){
                    robot.keyRelease(KeyEvent.VK_QUOTE);
                }else if(msg.contains("Enter")){
                    robot.keyRelease(KeyEvent.VK_ENTER);
                }else if(msg.contains("Shift")){
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                }else if(msg.contains("Z")){
                    robot.keyRelease(KeyEvent.VK_Z);
                }else if(msg.contains("X")){
                    robot.keyRelease(KeyEvent.VK_X);
                }else if(msg.contains("C") && !msg.contains("CapsLock") && !msg.contains("Ctrl")){
                    robot.keyRelease(KeyEvent.VK_C);
                }else if(msg.contains("V")){
                    robot.keyRelease(KeyEvent.VK_V);
                }else if(msg.contains("B") && !msg.contains("Backspace")){
                    robot.keyRelease(KeyEvent.VK_B);
                }else if(msg.contains("N")){
                    robot.keyRelease(KeyEvent.VK_N);
                }else if(msg.contains("M")){
                    robot.keyRelease(KeyEvent.VK_M);
                }else if(msg.contains("<")){
                    robot.keyRelease(KeyEvent.VK_COMMA);
                }else if(msg.contains(">")){
                    robot.keyRelease(KeyEvent.VK_PERIOD);
                }else if(msg.contains("?")){
                    robot.keyRelease(KeyEvent.VK_SLASH);
                }else if(msg.contains("Ctrl")){
                    robot.keyRelease(KeyEvent.VK_CONTROL);
                }else if(msg.contains("Fn")){

                }else if(msg.contains("Win")){
                    robot.keyRelease(KeyEvent.VK_WINDOWS);
                }else if(msg.contains("Alt")){
                    robot.keyRelease(KeyEvent.VK_ALT);
                }else if(msg.contains("Space")){
                    robot.keyRelease(KeyEvent.VK_SPACE);
                }else if(msg.contains("↑")){
                    robot.keyRelease(KeyEvent.VK_UP);
                }else if(msg.contains("↓")){
                    robot.keyRelease(KeyEvent.VK_DOWN);
                }else if(msg.contains("←")){
                    robot.keyRelease(KeyEvent.VK_LEFT);
                }else if(msg.contains("→")){
                    robot.keyRelease(KeyEvent.VK_RIGHT);
                }
            }
        }
    }
}
