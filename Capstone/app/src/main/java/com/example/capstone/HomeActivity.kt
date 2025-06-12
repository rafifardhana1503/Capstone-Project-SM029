package com.example.capstone

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import androidx.core.content.FileProvider
import androidx.appcompat.widget.Toolbar

class HomeActivity : AppCompatActivity() {

    private lateinit var modelHelper: ModelHelper
    private lateinit var imageView: ImageView
    private lateinit var predictionText: TextView
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private lateinit var currentPhotoUri: Uri

    private val labels = listOf(
        "Bercak Alga",       // algal_spot
        "Hawar Coklat",      // brown_blight
        "Hawar Abu-Abu",     // gray_blight
        "Sehat",             // healthy
        "Helopeltis",        // helopeltis
        "Bercak Merah"       // red_spot
    )

    private val rekomendasiMap = mapOf(
        "Sehat" to "Tanaman dalam kondisi baik. Lanjutkan perawatan rutin.",
        "Layu" to "Periksa kelembaban tanah dan pastikan penyiraman cukup.",
        "Jamur" to "Gunakan fungisida dan pastikan sirkulasi udara baik.",
        "Busuk" to "Potong bagian yang busuk dan perbaiki drainase.",
        "Karat" to "Gunakan pestisida organik dan buang daun yang terkena.",
        "Hama" to "Gunakan insektisida alami dan periksa tanaman secara rutin.",
        "Bercak Alga" to "Pangkas daun yang terkena dan kurangi kelembaban berlebih.",
        "Hawar Coklat" to "Gunakan fungisida sistemik dan jaga kebersihan lahan.",
        "Hawar Abu-Abu" to "Perbaiki drainase dan hindari kelembaban tinggi.",
        "Helopeltis" to "Gunakan insektisida alami dan monitoring hama.",
        "Bercak Merah" to "Perbaiki sirkulasi udara dan potong daun terinfeksi."
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.logo_teh)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.title = "LensaTani"

        modelHelper = ModelHelper(assets, "model.tflite")
        imageView = findViewById(R.id.imgLeaf)
        predictionText = findViewById(R.id.txtPrediction)

        findViewById<Button>(R.id.btnChooseGallery).setOnClickListener {
            ambilDariGaleri()
        }

        findViewById<Button>(R.id.btnTakePhoto).setOnClickListener {
            ambilDariKamera()
        }
    }

    fun ambilDariGaleri() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    fun ambilDariKamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()

        photoFile?.also {
            currentPhotoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun createImageFile(): File? {
        val timeStamp = System.currentTimeMillis()
        val fileName = "IMG_$timeStamp"
        val storageDir = cacheDir
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val bitmap = when {
            requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data?.data != null -> {
                MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
            }
            requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK -> {
                BitmapFactory.decodeStream(contentResolver.openInputStream(currentPhotoUri))
            }
            else -> null
        }

        val solusiText = findViewById<TextView>(R.id.txtSolusi)

        bitmap?.let {
            imageView.setImageBitmap(it)

            val inputBuffer = modelHelper.convertBitmapToInput(it)
            val output = modelHelper.runInference(inputBuffer)

            val hasilRaw = output.joinToString { "%.4f".format(it) }
            Log.d("MODEL_RAW", "Output: $hasilRaw")

            val top3 = output
                .mapIndexed { index, value -> index to value }
                .sortedByDescending { it.second }
                .take(3)

            val top3Text = top3.joinToString("\n") {
                "${labels[it.first]}: %.2f%%".format(it.second * 100)
            }

            val maxIndex = output.indices.maxByOrNull { output[it] } ?: -1
            val maxConfidence = output[maxIndex]

            if (maxConfidence < 0.7f) {
                predictionText.text = "Gambar bukan daun teh atau tidak dikenali"
                solusiText.text = ""
            } else {
                predictionText.text = "Top Prediksi:\n$top3Text"
                val predictedLabel = labels[maxIndex]
                val rekomendasi = rekomendasiMap[predictedLabel] ?: "Belum ada rekomendasi untuk kondisi ini."
                solusiText.text = "Rekomendasi:\n$rekomendasi"
            }
        }
    }
}
