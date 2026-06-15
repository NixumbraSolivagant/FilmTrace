# FilmLog

> 胶片摄影参数记录 App · 快速、方便地记录每次快门的参数与胶卷状态

[![Release](https://img.shields.io/github/v/release/NixumbraSolivagant/FilmLog)](https://github.com/NixumbraSolivagant/FilmLog/releases/latest)
[![Platform](https://img.shields.io/badge/platform-Android-3DDC84)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Kotlin-1.9-7F52FF)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

## ⬇️ 下载

**👉 [点击下载最新 APK](https://github.com/NixumbraSolivagant/FilmLog/releases/latest/download/FilmLog-v0.1.0-debug.apk)**

调试签名的 APK，27 MB。下载后在 Android 设备上启用「未知来源安装」即可。

## ✨ 功能

- **胶卷管理** — 添加/编辑/删除胶卷，支持上卷/卸下、当前状态跟踪
- **库存批量入库** — 选预设（Kodak Portra 400 / Ilford HP5+ / Fujifilm Pro 400H 等 7 种），一次性入库多卷
- **拍摄记录** — 每张快门记一次：快门 / 光圈 / 焦段 / ISO / 备注
- **参数预设** — 保存常用相机参数组合，新建拍摄记录时一键调用
- **复古胶片主题** — 暖色调暗色 UI，配合胶片摄影的视觉气质

## 🛠 技术栈

| 层次 | 技术 |
|------|------|
| 语言 | Kotlin 1.9 |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM (ViewModel + Repository) |
| 数据库 | Room |
| 依赖注入 | Hilt |
| 异步 | Kotlin Coroutines + Flow |
| 导航 | Navigation Compose |

## 📦 项目结构

```
app/src/main/java/com/filmlog/
├── data/
│   ├── local/         # Room: Database, DAO, Entity, Mappers
│   └── repository/    # Repository 实现
├── domain/
│   ├── model/         # 业务模型
│   └── repository/    # Repository 接口
├── di/                # Hilt 模块
├── ui/
│   ├── components/    # 通用组件
│   ├── navigation/    # NavHost + 路由
│   ├── screens/       # 各业务页面
│   └── theme/         # 颜色 / 字体 / 主题
├── FilmLogApp.kt      # @HiltAndroidApp
└── MainActivity.kt    # Compose 入口
```

## 🚀 构建

```bash
./gradlew assembleDebug
# 产物: app/build/outputs/apk/debug/app-debug.apk
```

需要 Android Studio Hedgehog | 2023.1.1+，JDK 17+，Android SDK 34。

## 📝 规格

详细功能规格见 [SPEC.md](SPEC.md)。

## 📄 许可证

MIT
