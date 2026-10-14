# Kamera Violet 💜

Aplikasi Android sederhana untuk membantu memeriksa konsistensi visual wajah sebelum dan sesudah memakai sunscreen. Aplikasi menggunakan kamera HP biasa dan dirancang agar fitur kamera berjalan offline.

> **Batasan penting:** kamera RGB biasa tidak dapat memastikan lapisan sunscreen, pemerataan fisik, atau perlindungan UV. Hasil aplikasi hanya membantu pemeriksaan visual dan bukan pengukuran SPF atau perlindungan UV.

## Fitur awal
- Pratinjau kamera depan.
- Ambil foto kondisi sebelum dan sesudah.
- Bandingkan foto secara lokal dengan peta perubahan piksel.
- Pemrosesan gambar dilakukan di perangkat; tidak ada unggahan ke server.
- Dapat digunakan tanpa internet setelah APK terpasang.
- GitHub Actions membangun APK debug secara otomatis.

## Unduh APK
Buka tab **Actions**, pilih workflow **Build Android APK**, lalu buka hasil run yang sukses dan unduh artifact `kamera-violet-debug-apk`. Untuk membangun manual, buka proyek ini di Android Studio dan jalankan **Build > Build APK(s)**.

## Cara menggunakan
1. Berada di pencahayaan yang stabil dan posisikan wajah di tengah.
2. Ambil foto kondisi awal.
3. Oleskan sunscreen sesuai petunjuk pada kemasan.
4. Ambil foto kedua dengan posisi dan pencahayaan serupa.
5. Tekan **Bandingkan foto** untuk melihat foto berdampingan dan peta perbedaan piksel. Peta ini menunjukkan perubahan gambar, bukan lokasi sunscreen yang pasti.
6. Jangan mengandalkan warna peta sebagai bukti pemerataan, SPF, atau perlindungan UV.

## Privasi
Foto diproses secara lokal. Aplikasi tidak meminta izin internet dan tidak mengunggah foto. Foto disimpan sementara di cache aplikasi dan dapat terhapus saat cache dibersihkan.
