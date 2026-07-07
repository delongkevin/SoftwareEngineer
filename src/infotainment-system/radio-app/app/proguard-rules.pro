# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools proguard-defaults.txt

# Keep hardware HAL interfaces for reflection
-keep interface com.infotainment.radio.hardware.** { *; }
-keep class com.infotainment.radio.model.** { *; }
