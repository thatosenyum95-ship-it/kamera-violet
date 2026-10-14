package com.kameraviolet

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private var imageCapture: ImageCapture? = null
    private lateinit var statusText: TextView

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera()
            else statusText.text = "Izin kamera diperlukan agar aplikasi dapat digunakan."
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(36, 19, 61)
        window.navigationBarColor = Color.rgb(22, 13, 37)
        buildUi()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(12))
            setBackgroundColor(Color.rgb(250, 247, 255))
        }

        val title = TextView(this).apply {
            text = "Kamera Violet"
            textSize = 26f
            setTextColor(Color.rgb(75, 35, 120))
            gravity = Gravity.CENTER
        }
        root.addView(title, LinearLayout.LayoutParams(-1, dp(42)))

        val subtitle = TextView(this).apply {
            text = "Pemeriksaan visual sebelum dan sesudah sunscreen"
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(75, 65, 85))
        }
        root.addView(subtitle, LinearLayout.LayoutParams(-1, dp(38)))

        previewView = PreviewView(this).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            setBackgroundColor(Color.rgb(30, 20, 40))
        }
        root.addView(
            previewView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            ).apply {
                topMargin = dp(8)
                bottomMargin = dp(10)
            }
        )

        val beforeButton = Button(this).apply {
            text = "Ambil foto SEBELUM"
            setOnClickListener { capturePhoto("sebelum_sunscreen.jpg", "Foto sebelum") }
        }
        root.addView(beforeButton, LinearLayout.LayoutParams(-1, dp(52)))

        val afterButton = Button(this).apply {
            text = "Ambil foto SESUDAH"
            setOnClickListener { capturePhoto("sesudah_sunscreen.jpg", "Foto sesudah") }
        }
        root.addView(afterButton, LinearLayout.LayoutParams(-1, dp(52)))

        statusText = TextView(this).apply {
            text = "Posisikan wajah di tengah dan gunakan pencahayaan yang stabil."
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(60, 45, 75))
            setPadding(dp(4), dp(8), dp(4), dp(4))
        }
        root.addView(statusText, LinearLayout.LayoutParams(-1, dp(54)))

        val disclaimer = TextView(this).apply {
            text = "Catatan: kamera HP biasa tidak dapat memastikan lapisan atau perlindungan UV sunscreen. Foto hanya disimpan di perangkat."
            textSize = 11f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(100, 85, 110))
        }
        root.addView(disclaimer, LinearLayout.LayoutParams(-1, dp(48)))

        setContentView(root)
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            try {
                val provider = providerFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val selector = if (provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                provider.unbindAll()
                provider.bindToLifecycle(this, selector, preview, imageCapture)
                statusText.text = "Kamera siap. Jaga jarak dan pencahayaan tetap sama untuk kedua foto."
            } catch (error: Exception) {
                statusText.text = "Kamera tidak dapat dibuka. Periksa izin dan coba lagi."
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturePhoto(fileName: String, label: String) {
        val capture = imageCapture
        if (capture == null) {
            Toast.makeText(this, "Kamera belum siap.", Toast.LENGTH_SHORT).show()
            return
        }

        val outputFile = File(cacheDir, fileName)
        val options = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        capture.takePicture(
            options,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    statusText.text = "$label tersimpan sementara di perangkat. Ambil foto kedua dengan kondisi cahaya yang sama."
                    Toast.makeText(this@MainActivity, "$label berhasil diambil", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    statusText.text = "Foto gagal diambil. Silakan coba kembali."
                    Toast.makeText(this@MainActivity, "Gagal mengambil foto.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
