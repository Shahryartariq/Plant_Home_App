# R8 rules for the release build.
# AndroidX, Material and Firebase ship their own consumer rules, so only app-specific rules go here.

# Fragments are created by name from res/navigation/mobile_navigation.xml.
-keep class com.example.plantshome.ui.** extends androidx.fragment.app.Fragment { <init>(); }

# Keep line numbers so crash reports point at real lines.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Release builds don't write debug or warning logs (error details can include account emails).
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}
