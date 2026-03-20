# SnapMotion ProGuard Rules

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep data classes (used with DataStore)
-keepclassmembers class com.snapmotion.domain.model.** {
    *;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep service classes
-keep class com.snapmotion.service.** { *; }

# Keep ViewModel constructors
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep AndroidViewModel constructors
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# DataStore
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# Sensor-related classes
-keep class android.hardware.Sensor { *; }
-keep class android.hardware.SensorEvent { *; }

# Prevent R8 from removing debug logging in release
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
}

# Keep source file names for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
