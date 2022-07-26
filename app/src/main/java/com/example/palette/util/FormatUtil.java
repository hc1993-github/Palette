package com.example.palette.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {

    /**
     * 是否手机号
     * @param mobilePhone
     */
    public static boolean isMobliePhone(String mobilePhone){
        Pattern pattern = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}");
        Matcher m = pattern.matcher(mobilePhone);
        return m.matches();
    }

    /**
     * 是否邮箱
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
    }
}
