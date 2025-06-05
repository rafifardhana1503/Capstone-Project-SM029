package com.example.capstone

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import androidx.core.graphics.scale

class ModelHelper(assetManager: AssetManager, modelPath: String) {
    private var interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModelFile(assetManager, modelPath))
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Ubah bitmap ke format input model (1, 224, 224, 3)
    fun convertBitmapToInput(bitmap: Bitmap): ByteBuffer {
        val resized = bitmap.scale(224, 224)
        val inputBuffer = ByteBuffer.allocateDirect(1 * 224 * 224 * 3 * 4)  // 4 bytes per float
        inputBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(224 * 224)
        resized.getPixels(intValues, 0, 224, 0, 0, 224, 224)

        for (pixel in intValues) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            inputBuffer.putFloat(r)
            inputBuffer.putFloat(g)
            inputBuffer.putFloat(b)
        }

        inputBuffer.rewind()
        return inputBuffer
    }

    fun runInference(inputBuffer: ByteBuffer): FloatArray {
        val output = Array(1) { FloatArray(6) }  // [1][6]
        interpreter.run(inputBuffer, output)
        return output[0]  // Ambil baris pertama
    }
}
