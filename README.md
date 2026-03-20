<div align="center">

# 🔦 ChopLight

**The ultra-minimalist, headless background flashlight toggle.**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![MinSdk](https://img.shields.io/badge/MinSdk-26%20(Android%208.0)-orange.svg)](https://android-arsenal.com/api?level=26)
[![Size](https://img.shields.io/badge/Size-~400KB-brightgreen.svg)](#)

</div>

<br/>

## 📖 Overview

**ChopLight** is an insanely lightweight, UI-less Android background utility dedicated to one single task: **turning on your flashlight with a double karate chop.**

No settings screens. No bloat. No complicated front-end. It runs silently in the background and is controlled exclusively via an Android Quick Settings tile.

## ✨ Features

- **🥋 One Motive**: Double downward "karate chop" to instantly toggle the flashlight.
- **🫥 Headless Architecture**: Absolutely zero User Interface. The app launches, requests basic background permissions, and instantly vanishes into the background for maximum efficiency.
- **🔋 Battery Optimized**: Uses an aggressive sensor gating system (moving average + low-pass filter) inside a deeply optimized Foreground Service.
- **🪶 Microscopic Size**: Stripped of all UI frameworks (Compose, Navigation, Material), bringing the total app size down to roughly ~400 KB.
- **🔒 Privacy First**: Utilizes modern Android APIs to toggle the torch *without* requiring the `CAMERA` permission.

## ⚙️ Control

Pull down your Android Notification Drawer, tap the edit pencil, and drag the **ChopLight** tile into your active Quick Settings. 

Tap the tile to instantly spin up or tear down the background detection engine.

---

## 🚀 Installation & Setup

1. Check out the repository.
2. Build via Gradle:
   ```bash
   ./gradlew assembleRelease
   ```
3. Install the resulting lightweight APK onto your device.
4. Launch the app once to grant Notification and Battery limits exemptions.

---
