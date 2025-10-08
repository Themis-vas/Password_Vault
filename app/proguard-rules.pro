# PassGuard specific rules
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }
-keep class dagger.hilt.internal.** { *; }
-keep class com.passguard.app.** { *; }
-dontwarn kotlinx.coroutines.**
-dontwarn androidx.security.crypto.**
-dontwarn dagger.hilt.internal.**

# Keep Room entities and dao methods
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keep interface androidx.room.RoomDatabase_Impl

-keepclassmembers class com.passguard.app.core.crypto.BiometricCryptoManager { *; }
