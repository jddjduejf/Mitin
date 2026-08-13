# ============================================================
# Layer 1: ProGuard Obfuscation
# ============================================================

# Keep entry points
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep all classes in the app package
-keep class chat.mitin.app.** { *; }

# ============================================================
# Aggressive Obfuscation
# ============================================================

-dontwarn
-optimizationpasses 7
-overloadaggressively
-repackageclasses ''
-allowaccessmodification
-useuniqueclassmembernames
-keepattributes *

-dontpreverify
-flattenpackagehierarchy ''
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# ============================================================
# Keep necessary Android classes
# ============================================================

-keep class android.** { *; }
-keep class androidx.** { *; }
-keep class java.** { *; }
-keep class javax.** { *; }
-keep class org.** { *; }

# Keep serialization
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep inner classes
-keepattributes InnerClasses,EnclosingMethod
-keep class *.R$* {
    public static <fields>;
}

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
