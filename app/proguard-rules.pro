# Keep Room entities/DAOs metadata
-keep class com.findora.app.data.db.** { *; }

# ML Kit text recognition
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
