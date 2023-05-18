# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn com.shensu.jiezhongapp.encrypt.**
-keep class com.shensu.jiezhongapp.encrypt.** {*;}

-dontwarn org.bouncycastle.**
-keep class org.bouncycastle.** {*;}

# 保留第三方jar包不被混淆
-dontwarn com.iflytek.**
-keep class com.iflytek.** {*;}

-dontwarn javax.annotation.**
-keep class javax.annotation.** {*;}

-dontwarn com.google.gson.**
-keep class com.google.gson.** {*;}

-dontwarn com.google.zxing.**
-keep class com.google.zxing.** {*;}

-dontwarn okhttp3.**
-keep class okhttp3.** {*;}

-dontwarn okio.**
-keep class okio.** {*;}

-dontwarn com.squareup.javawriter.**
-keep class com.squareup.javawriter.** {*;}

-dontwarn org.apache.commons.io.**
-keep class org.apache.commons.io.** {*;}

-dontwarn io.reactivex.**
-keep class io.reactivex.** {*;}

-dontwarn javax.inject.**
-keep class javax.inject.** {*;}

-dontwarn javax.inject.**
-keep class javax.inject.** {*;}

-dontwarn org.kxml2.**
-keep class org.kxml2.** {*;}

-dontwarn org.xmlpull.v1.**
-keep class org.xmlpull.v1.** {*;}

-dontwarn org.hamcrest.**
-keep class org.hamcrest.** {*;}

-dontwarn org.reactivestreams.**
-keep class org.reactivestreams.** {*;}

-dontwarn com.android.tools.**
-keep class com.android.tools.** {*;}

# 保留本项目中的类不被混淆
-dontwarn com.shensu.jiezhongapp.zxing.**
-keep class com.shensu.jiezhongapp.zxing.** {*;}
-dontwarn com.shensu.jiezhongapp.view.**
-keep class com.shensu.jiezhongapp.view.** {*;}
-dontwarn com.shensu.jiezhongapp.bean.**
-keep class com.shensu.jiezhongapp.bean.** {*;}
-dontwarn com.za.finger.**
-keep class com.za.finger.** {*;}
-dontwarn winuim.fingerprint.sdk.**
-keep class winuim.fingerprint.sdk.** {*;}


# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
# 保留R下面的资源
-keep class **.R$* {*;}
# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
