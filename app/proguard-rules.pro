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

# 混淆时是否记录日志
-verbose

#包下所有内容不混淆
-dontwarn 包名.**
-keep class 包名.** {*;}

#R文件不混淆
-keep class **.R$* {*;}

#Android类不混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.preference.Preference
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.view.View
#这两个类我们基本也用不上，是接入Google原生的一些服务时使用的，配置上以防万一
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

#自定义控件和属性动画不混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}


#onClick事件不混淆
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

#Parcelable不混淆
-keepclassmembers class * implements android.os.Parcelable{
    public static final android.os.Parcelable$Creator *;
}

#序列化类不混淆
-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[]   serialPersistentFields;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}

#Native方法不混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#json和gson
-keep class com.alibaba.fastjson.**{*;}
-keep class com.google.gson.**{*;}

#注解不混淆
-keepattributes *Annotation*,InnerClasses

#泛型不混淆
-keepattributes Signature

#枚举类不混淆
-keepclassmembers enum * {
   public static **[] values();
   public static ** valueOf(java.lang.String);
}

#回调函数不混淆
-keepclassmembers class * {
    void *(**On*Event);
}

#WebView不混淆
-keepclassmembers class * extends android.webkit.WebViewClient{
    public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
    public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}
-keepattributes JavascriptInterface
-keep class *{
    @android.webkit.JavascriptInterface <methods>;
}

#xml
-keep class org.xmlpull.** {*;}
-keep public class * extends org.xmlpull.**
-keep interface org.xmlpull.** {*;}