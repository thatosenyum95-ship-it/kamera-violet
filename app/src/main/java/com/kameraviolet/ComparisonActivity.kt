package com.kameraviolet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import kotlin.math.abs
import kotlin.math.max

class ComparisonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(36, 19, 61)
        window.navigationBarColor = Color.rgb(22, 13, 37)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(20))
            setBackgroundColor(Color.rgb(250, 247, 255))
        }

        val title = TextView(this).apply {
            text = "Perbandingan visual"
            textSize = 24f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(75, 35, 120))
        }
        root.addView(title, LinearLayout.LayoutParams(-1, dp(48)))

        val description = TextView(this).apply {
            text = "Peta ini menunjukkan perbedaan piksel antara dua foto, bukan deteksi sunscreen. Perubahan cahaya, ekspresi, dan posisi wajah juga dapat menghasilkan perbedaan."
            textSize = 14f
            setTextColor(Color.rgb(65, 55, 75))
            setPadding(0, dp(6), 0, dp(12))
        }
        root.addView(description)

        val before = BitmapFactory.decodeFile(File(cacheDir, "sebelum_sunscreen.jpg").absolutePath)
        val after = BitmapFactory.decodeFile(File(cacheDir, "sesudah_sunscreen.jpg").absolutePath)

        if (before == null || after == null) {
            val missing = TextView(this).apply {
                text = "Dua foto belum tersedia. Kembali ke kamera dan ambil foto SEBELUM serta SESUDAH terlebih dahulu."
                textSize = 16f
                setTextColor(Color.DKGRAY)
            }
            root.addView(missing)
        } else {
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            row.addView(photoColumn("Sebelum", before), LinearLayout.LayoutParams(0, dp(190), 1f))
            row.addView(photoColumn("Sesudah", after), LinearLayout.LayoutParams(0, dp(190), 1f))
            root.addView(row)

            val heatTitle = TextView(this).apply {
                text = "Peta perubahan visual"
                textSize = 18f
                setTextColor(Color.rgb(75, 35, 120))
                setPadding(0, dp(18), 0, dp(8))
            }
            root.addView(heatTitle)

            val map = ImageView(this).apply {
                setImageBitmap(createDifferenceMap(before, after))
                scaleType = ImageView.ScaleType.FIT_CENTER
                contentDescription = "Peta perubahan piksel visual"
                setBackgroundColor(Color.rgb(35, 25, 45))
            }
            root.addView(map, LinearLayout.LayoutParams(-1, dp(300)))

            val legend = TextView(this).apply {
                text = "Warna ungu gelap = perubahan kecil • Merah/kuning = perubahan piksel lebih besar"
                textSize = 12f
                setTextColor(Color.rgb(65, 55, 75))
                setPadding(0, dp(8), 0, dp(12))
            }
            root.addView(legend)

            before.recycle()
            after.recycle()
        }

        val caution = TextView(this).apply {
            text = "Penting: hasil ini tidak mengukur pemerataan, ketebalan, SPF, atau perlindungan UV sunscreen. Gunakan hanya sebagai pembanding foto. Untuk memastikan perlindungan, oleskan produk sesuai petunjuk kemasan."
            textSize = 13f
            setTextColor(Color.rgb(95, 55, 45))
            setPadding(dp(12), dp(12), dp(12), dp(12))
            setBackgroundColor(Color.rgb(255, 239, 226))
        }
        root.addView(caution)

        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun photoColumn(label: String, bitmap: Bitmap): LinearLayout {
        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
        }
        column.addView(TextView(this).apply {
            text = label
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(75, 35, 120))
        }, LinearLayout.LayoutParams(-1, dp(28)))
        column.addView(ImageView(this).apply {
            setImageBitmap(bitmap)
            scaleType = ImageView.ScaleType.CENTER_CROP
            contentDescription = "Foto $label"
        }, LinearLayout.LayoutParams(-1, 0, 1f))
        return column
    }

    private fun createDifferenceMap(first: Bitmap, second: Bitmap): Bitmap {
        val width = 240
        val height = 300
        val a = Bitmap.createScaledBitmap(first, width, height, true)
        val b = Bitmap.createScaledBitmap(second, width, height, true)
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val ca = a.getPixel(x, y)
                val cb = b.getPixel(x, y)
                val diff = (abs(Color.red(ca) - Color.red(cb)) +
                    abs(Color.green(ca) - Color.green(cb)) +
                    abs(Color.blue(ca) - Color.blue(cb))) / 3
                val strength = (diff * 2).coerceIn(0, 255)
                val r = strength
                val g = max(0, strength - 110)
                val blue = (90 + (255 - strength) / 2).coerceIn(0, 255)
                pixels[y * width + x] = Color.rgb(r, g, blue)
            }
        }
        output.setPixels(pixels, 0, width, 0, 0, width, height)
        if (a !== first) a.recycle()
        if (b !== second) b.recycle()
        return output
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
