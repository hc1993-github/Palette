package com.example.lib;

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

public class MyClass {
    public static void main(String[] args) {
        try {
//            File file = new File("D://cold.apk");
//            System.out.println(getMd5ByFile(file));
//            createFile(100,2.32f);
//            System.out.println(isMobliePhone(""));
//            System.out.println(isEmail("1234@a.com"));
//            createFile(30,1);
//            System.out.println(printString2("2022-08-12 10:00:00",100,"2022-08-13 10:00:00"));
            MouseServer mouseServer = new MouseServer(9999);
            mouseServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String printString2(String checkTime, int range, String currentTime) throws Exception {
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
}