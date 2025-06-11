package com.base.app.baseapp.ui.login

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.databinding.ActivityLoginBinding
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            runModel(bitmap)
        }
    }

    private fun pickImageFromGallery() {
        imagePickerLauncher.launch("image/*")
    }

    lateinit var module: Module
    override fun initView() {
        module = Module.load(assetFilePath(this, "efficient_model.pt"))

        binding.btnNext.setOnClickListener {
            pickImageFromGallery()
        }


//        binding.btnNext.setOnClickListener {
//            startActivity(Intent(this, InfoActivity::class.java))
//        }
//
//        binding.btnRegister.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
    }

    fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) return file.absolutePath

        context.assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        }
        return file.absolutePath
    }

    private fun runModel(bitmap: Bitmap) {
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true) // tùy thuộc model
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            resized,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )

        val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
        val scores = outputTensor.dataAsFloatArray

        val maxIdx = scores.indices.maxByOrNull { scores[it] } ?: -1
        val result = "Label: $maxIdx - Score: ${scores[maxIdx]}"
        Toast.makeText(this, result, Toast.LENGTH_LONG).show()
    }


}