# MorseVerse ProGuard Rules

# Keep the application class
-keep class com.morseverse.app.MorseVerseApp { *; }

# Keep Room entities and DAOs
-keep class com.morseverse.core.data.database.entities.** { *; }
-keep class com.morseverse.core.data.database.dao.** { *; }
-keep class com.morseverse.core.data.database.MorseVerseDatabase { *; }

# Keep domain models
-keep class com.morseverse.core.domain.models.** { *; }

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep kotlinx serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.morseverse.**$$serializer { *; }
-keepclassmembers class com.morseverse.** { *** Companion; }
-keepclasseswithmembers class com.morseverse.** { kotlinx.serialization.KSerializer serializer(...); }

# Keep kotlinx datetime
-keep class kotlinx.datetime.** { *; }

# Keep data classes
-keepclassmembers class * {
    public <init>(...);
}

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
