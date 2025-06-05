package com.example.capstone

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import androidx.core.content.FileProvider

class HomeActivity : AppCompatActivity() {

    private lateinit var modelHelper: ModelHelper
    private lateinit var imageView: ImageView
    private lateinit var predictionText: TextView
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private lateinit var currentPhotoUri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        modelHelper = ModelHelper(assets, "model.tflite")
        imageView = findViewById(R.id.imgLeaf)
        predictionText = findViewById(R.id.txtPrediction)

        val btnChooseGallery = findViewById<Button>(R.id.btnChooseGallery)
        btnChooseGallery.setOnClickListener {
            ambilDariGaleri()
        }

        val btnCamera = findViewById<Button>(R.id.btnTakePhoto)
        btnCamera.setOnClickListener {
            ambilDariKamera()
        }
    }

    fun ambilDariGaleri() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

            // Tampilkan gambar
            imageView.setImageBitmap(bitmap)

            // Proses model
            val inputBuffer = modelHelper.convertBitmapToInput(bitmap)
            val output = modelHelper.runInference(inputBuffer)

            val labels = listOf("Sehat", "Layu", "Jamur", "Busuk", "Karat", "Hama")
            val index = output.indices.maxByOrNull { output[it] } ?: -1
            val hasil = if (index != -1) labels[index] else "Tidak diketahui"

            predictionText.text = "Prediksi: $hasil"
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(currentPhotoUri))

            imageView.setImageBitmap(bitmap)

            val inputBuffer = modelHelper.convertBitmapToInput(bitmap)
            val output = modelHelper.runInference(inputBuffer)

            val labels = listOf("Sehat", "Layu", "Jamur", "Busuk", "Karat", "Hama")
            val index = output.indices.maxByOrNull { output[it] } ?: -1
            val hasil = if (index != -1) labels[index] else "Tidak diketahui"

            predictionText.text = "Prediksi: $hasil"
        }
    }

    fun createImageFile(): File? {
        val timeStamp = System.currentTimeMillis()
        val fileName = "IMG_$timeStamp"
        val storageDir = cacheDir
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    fun ambilDariKamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()

        photoFile?.also {
            currentPhotoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",  // pastikan sesuai manifest
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

}
