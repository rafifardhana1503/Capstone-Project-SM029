# 🌿 LensaTani

**LensaTani** adalah aplikasi Android yang memanfaatkan teknologi **machine learning** untuk mendeteksi penyakit tanaman dari **citra daun**, khususnya untuk tanaman lokal Indonesia seperti teh. Aplikasi ini dirancang untuk membantu petani, pelajar, dan praktisi pertanian dalam mengenali penyakit tanaman secara cepat, mudah, dan akurat.

---
## Persiapan
- Dataset & Model:

  Model `model_terlatih.tflite` dilatih menggunakan dataset citra daun teh yang dikumpulkan melalui platform kaggle, dengan klasifikasi beberapa jenis penyakit umum. Untuk tahap preprocessing data dapat diakses melalui file `Preprocessing_Tea_Disease.ipynb` dan untuk tahap pengembangan model dapat diakses melalui file `Modelling_Tea_Disease.ipynb` pada repositori GitHub ini. Berikut dataset yang digunakan dalam proyek ini.
  - Raw Dataset: [Tea Leaf Disease Detection Data](https://www.kaggle.com/datasets/pavantejamedi/tea-leaf-disease-detection-data)
  - Preprocessing Dataset: [Preprocessing Tea Leaf Disease Dataset](https://drive.google.com/file/d/1nssl01tRJJ8Uu2Y_cIkCwADAcndOQ3m3/view?usp=sharing)

- Setup Environment:
1. Prasyarat Tools
    - Google Colab: [Google Colab](https://colab.research.google.com/)
    - Android Studio: [Android Studio](https://developer.android.com/studio)
2. Clone Repository\
   Clone Repository menggunakan `git`:
   ```
   git clone https://github.com/username/Capstone-Project-SM029.git
   cd Capstone-Project-SM029
   ```
3. Setup Google Colab
   ```
   Python 3.11.12
   ```
   Penggunaan Google Colab sudah menyediakan versi Python terbaru secara default. Sebagian besar library populer sudah kompatibel.
4. Setup Android Studio
   Menginstal Andriod Studio Terbaru
   Menginstall SDK Android minimal versi API 21
   Mempunyai file model .tflite hasil training machine learningnya
5. Setelah seluruh proses setup selesai, Anda bisa menjalankan skrip utama atau mulai melakukan pengembangan model.
   - Untuk menjalankan preprocessing data terdapat pada
     ```
     Preprocessing_Tea_Disease.ipynb
     ```
   - Untuk menjalankan tahap modelling hingga save model terdapat pada
     ```
     Modelling_Tea_Disease.ipynb
     ```
   - Untuk melakukan deployment ke Android studio terdapat pada folder `Capstone`
    Import Project dengan cara mendownload terlebih dahulu file project di dalam zip folder, kemudian tunggu hingga grandle selesai lalu nyalakan emulator dan terkahir run aplikasi
## 📱 Fitur Utama Produk

- 📷 Deteksi penyakit daun menggunakan kamera atau galeri
- 🧠 Model AI lokal (CNN) yang dilatih dengan dataset tanaman Indonesia (saat ini tanaman teh)
- 📊 Menampilkan hasil deteksi dengan tingkat akurasi
- 📚 Informasi penyakit dan saran penanganan
- ☁️ Ringan dan bisa berjalan offline (tanpa koneksi internet)

## 📲 Cara Penggunaan Produk

1. Download aplikasi melalui file APK, dapat diakses melalui
   ```
   LensaTani.zip
   ```
2. 
   Kemudian untuk cara menggunakan aplikasi dapt dilihat pada [Video Demo](https://drive.google.com/file/d/1qQCqW577i-beGJSP4g2lE2grAG2-8MCm/view?usp=sharing)

## 🧩 Kontribusi Kolaborasi
Pengembangan proyek akan menjadi lebih baik dengan melakukan kontribusi terbuka
- Kolaborasi bersama pihak terkait seperti Dinas Pertanian, Pelaku Pertanian, dan lainnya.
- Penambahan jenis tanaman/penyakit baru
- Peningkatan UI/UX
- Optimisasi model machine learning
