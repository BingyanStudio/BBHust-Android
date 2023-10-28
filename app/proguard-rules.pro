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
-dump class_files.txt    #记录生成的日志数据,gradle build时在本项目根目录输出 apk 包内所有 class 的内部结构
-printmapping mapping.txt #混淆前后的映射 (在build\outputs\mapping\你的项目名\release\文件夹下，出现了dump.txt, mapping.txt, seeds.txt, usage.txt)

-repackageclasses com.bingyan.bbhust
-obfuscationdictionary dict.txt
-classobfuscationdictionary dict.txt
-packageobfuscationdictionary dict.txt

-keepattributes LineNumberTable,SourceFile
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-optimizationpasses 5
#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

  #保持所有实现 Serializable 接口的类成员
  -keepclassmembers class * implements java.io.Serializable {
      static final long serialVersionUID;
      private static final java.io.ObjectStreamField[] serialPersistentFields;
      private void writeObject(java.io.ObjectOutputStream);
      private void readObject(java.io.ObjectInputStream);
      java.lang.Object writeReplace();
      java.lang.Object readResolve();
  }
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-dontwarn okio.**

-keep class * extends java.lang.annotation.Annotation { *; }
-keep interface * extends java.lang.annotation.Annotation { *; }
#-keep class cn.usfl.dian.data.bean.AppResultJsonAdapter

# 去除日志
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String,int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
   public static int e(...);
}
-assumenosideeffects class java.io.PrintStream {
    public *** println(...);
    public *** print(...);
}

# 友盟
-keep class com.umeng.** {*;}

-keep class org.repackage.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class com.bingyan.bbhust.R$*{
public static final int *;
}

# JsBridge
-keep class com.bingyan.bbhust.ui.screen.browser.* { *; }
-keepclassmembers class com.bingyan.bbhust.ui.screen.browser.* { *; }
-keepnames class com.bingyan.bbhust.ui.screen.browser.* { *; }
-keepclassmembernames class com.bingyan.bbhust.ui.screen.browser.* { *; }

# UploadImage Retrofit Interface
-keep class com.bingyan.bbhust.inter.* { *; }
-keepclassmembers class com.bingyan.bbhust.inter.* { *; }
-keepnames class com.bingyan.bbhust.inter.* { *; }
-keepclassmembernames class com.bingyan.bbhust.inter.* { *; }

-keep class com.bingyan.widget.bean.* { *; }
-keepclassmembers class com.bingyan.widget.bean.* { *; }
-keepnames class com.bingyan.widget.bean.* { *; }
-keepclassmembernames class com.bingyan.widget.bean.* { *; }

-keepattributes Annotation
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
#Prevents warnings for consumers not using AndroidX
-dontwarn androidx.annotation.**

-keepclassmembers public class com.bingyan.bbhust.ui.screen.browser.JsBridge{
    <fields>;
    <methods>;
    public *;
    private *;
}

###### Retrofit 混淆
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-dontwarn javax.lang.model.element.Modifier
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE