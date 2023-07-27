package com.example.palette.util;

public class StringUtil {

    /**
     * 整体真实为空(空或长度为0)
     * @param string
     * @return true 是 false 否
     */
    public static boolean isRealEmpty(CharSequence string){
        if(string==null || string.length()==0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 整体看起来为空(包含空格、Tab、特殊字符)
     * @param string
     * @return true 是 false 否
     */
    public static boolean isLookEmpty(CharSequence string){
        if(isRealEmpty(string)){
            return true;
        }
        for (int i = 0; i < string.length(); i++) {
            if(!Character.isWhitespace(string.charAt(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * 是否某个字符有空(包含空格、Tab、特殊字符)
     * @param string
     * @return true 是 false 否
     */
    public static boolean isContainsEmpty(CharSequence string){
        for (int i = 0; i < string.length(); i++) {
            if(Character.isWhitespace(string.charAt(i))){
                return true;
            }
        }
        return false;
    }
}
