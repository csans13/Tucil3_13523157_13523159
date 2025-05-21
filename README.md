# Tugas Kecil 3 IF2211 Strategi Algoritma – Rush Hour Puzzle Solver  

## Tentang Projek

Rush Hour Puzzle Solver adalah sebuah aplikasi berbasis CLI yang dikembangkan menggunakan bahasa pemrograman **Java** untuk menyelesaikan permainan puzzle **Rush Hour**. Program ini dirancang untuk mengimplementasikan algoritma pencarian jalur (pathfinding) sebagai solusi dari Tugas Kecil 3 mata kuliah **IF2211 Strategi Algoritma** pada Semester II Tahun Akademik 2024/2025.

Aplikasi ini mendukung empat algoritma pencarian:

- Uniform Cost Search (UCS)
- Greedy Best First Search
- A* Search 
- Beam Search

Selain itu, tersedia juga beberapa pilihan fungsi heuristik:
- Jarak Manhattan ke pintu keluar
- Jumlah kendaraan penghalang (blocking pieces)

Program ini membaca konfigurasi puzzle dari file `.txt`, lalu menyelesaikan puzzle dan menampilkan langkah demi langkah solusi, waktu eksekusi, jumlah simpul yang dikunjungi, serta menyimpan hasilnya ke file jika diinginkan.

---

## Requirement

- **Java Development Kit (JDK) versi 17 atau lebih baru**

---

## Cara Kompilasi
1. Clone repository ini 
https://github.com/csans13/Tucil3_13523157_13523159.git

2. Masuk ke direktori proyek dan jalankan perintah berikut:

```bash
javac -d bin src/*.java
```

File hasil kompilasi akan muncul di dalam folder `bin/`.

---

## Cara Menjalankan Program

Setelah kompilasi, jalankan program dengan:

```bash
java -cp bin Main
```

Ikuti instruksi pada terminal untuk:
1. Memasukkan path file konfigurasi puzzle (misal: `test/input1.txt`)
2. Memilih algoritma yang diinginkan:
   - 1. Uniform Cost Search
   - 2. Greedy Best First Search
   - 3. A* Search
   - 4. Beam Search
3. Memilih heuristik (jika algoritma mendukung):
   - 1. Manhattan Distance
   - 2. Blocking Pieces
4. Program akan:
   - Menampilkan konfigurasi awal
   - Menampilkan setiap langkah gerakan
   - Menampilkan jumlah langkah, waktu eksekusi, dan simpul yang dikunjungi
   - Menawarkan opsi menyimpan hasil ke `.txt`

---

## Author

- Natalia Desiany Nursimin - 13523157
- Anas Ghazi Al Ghifari - 13523159
