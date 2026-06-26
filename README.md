<div align="center">

<img width="1024" height="1024" alt="Logo_App" src="https://github.com/user-attachments/assets/17f3bb5c-4038-4010-bf48-9fc0059325b9" />

# 🥦 HelloNutri AI
### *AI Teman Nutrisimu Sehat Setiap Hari*

[![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Language-Java-orange?logo=java)](https://www.java.com)
[![Gemini AI](https://img.shields.io/badge/AI-Gemini%202.5-blue?logo=google)](https://ai.google.dev)
[![Status](https://img.shields.io/badge/Status-In%20Development-yellow)]()
[![UAS](https://img.shields.io/badge/Mata%20Kuliah-Pemrograman%20Mobile%202-lightgreen)]()

</div>

---

## 📖 Tentang Aplikasi

**HelloNutri AI** adalah aplikasi asisten nutrisi personal berbasis AI yang dirancang untuk mendampingi perjalanan gaya hidup sehat penggunanya. Aplikasi ini tidak hanya mampu melacak asupan kalori secara cerdas, tetapi juga memberikan rekomendasi gizi yang sepenuhnya disesuaikan dengan kebutuhan unik tubuh pengguna.

Dibangun sebagai proyek UAS mata kuliah **Pemrograman Mobile 2**, HelloNutri AI mengintegrasikan teknologi **Google Gemini 2.5 API** untuk memberikan pengalaman chatbot nutrisi yang interaktif dan personal.

> 📅 Dibuat pada: **02 Maret 2026**  
> 👩‍💻 Developer: **Vivit Nurul Hidayah**
> Link Figma : **https://www.figma.com/design/2R4E9Hyob9tBA34gwaMRBK/HelloNutri?node-id=124-105&t=0c0Z1iJbCynd15xW-1**

> Link ClickUp : **https://sharing.clickup.com/90181792771/g/h/2kzm1x03-458/e7f6ff079d9a33a**

---

## ✨ Fitur Utama

### 🏠 Home Dashboard
Menampilkan ringkasan BMR harian, target kalori, dan inspirasi menu sehat yang dipersonalisasi berdasarkan profil pengguna.

### 🔍 Scan Makanan (AI Vision)
Foto makanan menggunakan kamera atau pilih dari galeri — AI secara otomatis menganalisis kandungan kalori, protein, lemak, dan karbohidrat, lengkap dengan opsi **Versi Sehat** dari makanan yang sama.

### 🤖 Chatbot AI (Tanya AI)
Fitur obrolan interaktif berbasis Gemini 2.5 yang siap menjawab pertanyaan seputar nutrisi, gizi makanan, dan rekomendasi menu diet kapan saja.

### 🥗 Resep Sehat
Rekomendasi resep sehat harian yang dikurasi AI, dikategorikan berdasarkan waktu makan (Sarapan, Makan Siang, Makan Malam) dengan filter Tinggi Protein, Simple & Murah, dll.

### 💾 Riwayat Tersimpan
Menyimpan semua hasil scan makanan dan resep sehat yang pernah dilihat, lengkap dengan detail nutrisi dan tanggal penyimpanan.

### 👤 Profil Pengguna
Manajemen data diri (nama, berat, tinggi, usia, jenis kelamin, tujuan kesehatan) yang digunakan AI untuk kalkulasi BMR dan personalisasi rekomendasi.

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| Language | Java |
| Platform | Android (Android Studio) |
| AI / Chatbot | Google Gemini 2.5 API |
| Database Lokal | Room / SQLite |
| Image Source | Pexels API |
| UI Design | XML Layouts (Minimalist Health Style) |
| Design Tool | Figma |

---

## 📱 Screenshots

| Splash Screen | Location Detection | Onboarding | Profil Setup | Home |
|:---:|:---:|:---:|:---:|:---:|
| <img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/3cb6c452-d897-4b61-8116-9a7a2a1f624b" /> | <img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/33bac7e7-84fd-4a0c-a62a-80f4194806b0" />| <img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/4aa9c65d-bcdf-48b5-bb8f-c99f4f2d86d4" />| <img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/f4aa009e-486f-4ff8-bb13-4798887e995d" />| <img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/328533a3-6458-4a1f-92ab-dbddee4117b4" />|

| Scan Makanan | Hasil Scan | Versi Sehat | Chatbot | Resep Sehat |
|:---:|:---:|:---:|:---:|:---:|
|<img width="160" height="366" alt="image" src="https://github.com/user-attachments/assets/9aa353b6-4a8e-46e3-8c5e-38a1336095d8" />|<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/9940eccc-75d4-4f8b-843f-507f3d1ef376" />|<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/69bcd915-3c13-4f80-9600-55c1612917f3" />| <img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/57638b34-291d-4cf7-bba4-91befc8ba780" />|<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/265e0244-3b59-4f17-9843-85c77ddb33a4" />
|

---

## 🗂️ Struktur Proyek

```
HelloNutriAI/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/hellonutri/
│   │       │   ├── activity/
│   │       │   │   ├── SplashActivity.java
│   │       │   │   ├── LocationActivity.java
│   │       │   │   ├── OnboardingActivity.java
│   │       │   │   ├── ProfileSetupActivity.java
│   │       │   │   ├── MainActivity.java
│   │       │   │   ├── ScanActivity.java
│   │       │   │   ├── ScanResultActivity.java
│   │       │   │   └── ChatbotActivity.java
│   │       │   ├── fragment/
│   │       │   │   ├── HomeFragment.java
│   │       │   │   ├── ChatFragment.java
│   │       │   │   ├── SavedFragment.java
│   │       │   │   └── ProfileFragment.java
│   │       │   ├── model/
│   │       │   │   ├── UserProfile.java
│   │       │   │   ├── ScanResult.java
│   │       │   │   └── Recipe.java
│   │       │   ├── database/
│   │       │   │   ├── AppDatabase.java
│   │       │   │   └── dao/
│   │       │   └── api/
│   │       │       ├── GeminiApiService.java
│   │       │       └── PexelsApiService.java
│   │       └── res/
│   │           ├── layout/
│   │           ├── drawable/
│   │           └── values/
├── build.gradle
└── README.md
```

---

## 🚀 Cara Menjalankan

### Prerequisites
- Android Studio **Hedgehog** atau lebih baru
- JDK 11+
- Android SDK API Level 24+
- Koneksi internet (untuk Gemini API & Pexels API)

### Langkah Instalasi

1. **Clone repository ini**
   ```bash
   git clone https://github.com/username/HelloNutriAI.git
   cd HelloNutriAI
   ```

2. **Tambahkan API Key** di `local.properties` atau `gradle.properties`:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   PEXELS_API_KEY=your_pexels_api_key_here
   ```

3. **Buka di Android Studio**, sync Gradle, lalu **Run** di emulator atau perangkat fisik.

---

## 📋 Timeline Pengerjaan

<img width="1323" height="510" alt="image" src="https://github.com/user-attachments/assets/a08af69a-cb53-4445-b8cd-8b9e8a60218f" />


| Fase | Aktivitas | Status |
|------|-----------|--------|
| Fase 1 | Riset Masalah, Pembuatan User Flow & Storyboard | ✅ Selesai |
| Fase 2 | Desain High-Fidelity di Figma (Minimalist Health Style) | ✅ Selesai |
| Fase 3 | Implementasi XML, Navigasi, & UI User-Friendly | ✅ Selesai |
| Fase 4 | Integrasi API Gemini 2.5 & Logika Chat Interaktif | ✅ Selesai |
| Fase 5 | Manajemen Database Lokal (Room/SQLite) untuk Profil | ✅ Selesai  |
| Fase 6 | Optimasi Smart Scanner & Final Bug Fixing | ✅ Selesai |

---

## 🔮 Rencana Pengembangan

- [ ] Finalisasi Room Database untuk profil & riwayat lokal
- [ ] Optimasi akurasi Smart Food Scanner
- [ ] Tambah fitur Water Intake Tracker
- [ ] Notifikasi pengingat makan & minum
- [ ] Export laporan nutrisi mingguan (PDF)
- [ ] Integrasi dengan Google Fit / Health Connect

---

## 👩‍💻 Developer

<div align="center">

**Vivit Nurul Hidayah**  
Teknik Informatika — Universitas Pelita Bangsa  
Pemrograman Mobile 2 | Genap 2025/2026

</div>

---

<div align="center">

*"Makan enak, tetap sehat! — HelloNutri AI 🥦"*

Made with ❤️ and a lot of vegetables

</div>
