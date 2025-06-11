package com.base.app.baseapp.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.base.app.baseapp.R
import com.base.app.baseapp.base.BaseFragment
import com.base.app.baseapp.databinding.FragmentScanBinding
import com.base.app.baseapp.databinding.PopupFoodListBinding
import com.base.app.baseapp.model.FoodItem
import com.base.app.baseapp.utils.Constants
import com.base.app.baseapp.utils.Utils.gone
import com.base.app.baseapp.utils.Utils.visible
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ScanFragment : BaseFragment<FragmentScanBinding>(FragmentScanBinding::inflate) {
    private var imageCapture: ImageCapture? = null
    private lateinit var module: Module
    private lateinit var adapter: FoodAdapter2
    override fun initView() {
        module = Module.load(assetFilePath(requireContext(), "efficient_model.pt"))
        requestCameraPermission()

        binding.btnCap.setOnClickListener {
            binding.progressBar.visible()
            takePhoto { bitmap ->
                bitmap?.let {
                    runModel(it)
                }
            }
        }
        adapter = FoodAdapter2 {
        }
        binding.rcv.adapter = adapter

        binding.btnBack.setOnClickListener {
            binding.rl.visible()
            binding.lnSHow.gone()
            binding.lnResult.gone()
        }

        val bindingDialog = PopupFoodListBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(requireContext())
        popupWindow.setContentView(bindingDialog.root)
        popupWindow.width = WindowManager.LayoutParams.MATCH_PARENT
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val adapter2 = FoodAdapter2(isSelect = true) { newFood ->
            adapter.addNewFood(newFood)
            binding.edtFind.text.clear()
        }
        bindingDialog.recyclerPopup.adapter = adapter2
        binding.edtFind.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    val currentFoods = adapter.getList()
                    val filteredFoods = Constants.listItem.filter { food ->
                        !currentFoods.contains(food) && food.name.isFuzzyMatch(query)
                    }

                    adapter2.updateList(filteredFoods)
                    if(filteredFoods.isNotEmpty()){
                        popupWindow.showAsDropDown(binding.edtFind)
                    }
                } else {
                    popupWindow.dismiss()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.btnXacNhan.setOnClickListener {
            binding.lnSHow.gone()
            binding.lnResult.visible()
            val adapter3 = FoodInfoAdapter()
            binding.rcvResult.adapter = adapter3
            adapter3.updateList(adapter.getSelectedItems())
        }
    }

    private fun assetFilePath(context: Context, assetName: String): String {
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

        val foodItems = Constants.listItem.filter { it.id == maxIdx + 1 }
        foodItems.forEach {
            it.accuracy = scores[maxIdx] * 100
        }

        binding.progressBar.gone()
        binding.rl.gone()
        binding.lnSHow.visible()

        adapter.updateList(foodItems)
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1001)
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.preview.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto(callback: (Bitmap?) -> Unit) {
        val imageCapture = imageCapture ?: return

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            createTempFile()
        ).build()

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = imageProxyToBitmap(image)
                    callback(bitmap)
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    callback(null)
                }
            }
        )
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val yuvImage = YuvImage(
            bytes, ImageFormat.NV21,
            image.width, image.height, null
        )
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val yuv = out.toByteArray()
        return BitmapFactory.decodeByteArray(yuv, 0, yuv.size)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requestCameraPermission()
    }

    fun String.removeVietnameseTone(): String {
        val map = mapOf(
            'á' to 'a', 'à' to 'a', 'ả' to 'a', 'ã' to 'a', 'ạ' to 'a',
            'ă' to 'a', 'ắ' to 'a', 'ằ' to 'a', 'ẳ' to 'a', 'ẵ' to 'a', 'ặ' to 'a',
            'â' to 'a', 'ấ' to 'a', 'ầ' to 'a', 'ẩ' to 'a', 'ẫ' to 'a', 'ậ' to 'a',
            'đ' to 'd',
            'é' to 'e', 'è' to 'e', 'ẻ' to 'e', 'ẽ' to 'e', 'ẹ' to 'e',
            'ê' to 'e', 'ế' to 'e', 'ề' to 'e', 'ể' to 'e', 'ễ' to 'e', 'ệ' to 'e',
            'í' to 'i', 'ì' to 'i', 'ỉ' to 'i', 'ĩ' to 'i', 'ị' to 'i',
            'ó' to 'o', 'ò' to 'o', 'ỏ' to 'o', 'õ' to 'o', 'ọ' to 'o',
            'ô' to 'o', 'ố' to 'o', 'ồ' to 'o', 'ổ' to 'o', 'ỗ' to 'o', 'ộ' to 'o',
            'ơ' to 'o', 'ớ' to 'o', 'ờ' to 'o', 'ở' to 'o', 'ỡ' to 'o', 'ợ' to 'o',
            'ú' to 'u', 'ù' to 'u', 'ủ' to 'u', 'ũ' to 'u', 'ụ' to 'u',
            'ư' to 'u', 'ứ' to 'u', 'ừ' to 'u', 'ử' to 'u', 'ữ' to 'u', 'ự' to 'u',
            'ý' to 'y', 'ỳ' to 'y', 'ỷ' to 'y', 'ỹ' to 'y', 'ỵ' to 'y'
        )
        return lowercase().map { map[it] ?: it }.joinToString("")
    }

    fun String.isFuzzyMatch(query: String): Boolean {
        val name = this.removeVietnameseTone()
        val cleanQuery = query.removeVietnameseTone()

        var index = 0
        for (c in cleanQuery) {
            index = name.indexOf(c, index)
            if (index == -1) return false
            index++
        }
        return true
    }


}