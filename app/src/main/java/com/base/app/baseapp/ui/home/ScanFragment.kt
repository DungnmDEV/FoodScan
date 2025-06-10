package com.base.app.baseapp.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.base.app.baseapp.R
import com.base.app.baseapp.base.BaseFragment
import com.base.app.baseapp.databinding.FragmentScanBinding
import com.base.app.baseapp.model.FoodItem
import com.base.app.baseapp.utils.Utils.gone
import com.base.app.baseapp.utils.Utils.visible

class ScanFragment : BaseFragment<FragmentScanBinding>(FragmentScanBinding::inflate) {

    private var cameraProvider: ProcessCameraProvider? = null
    override fun initView() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                1001
            )
        } else {
            startCamera()
        }
        val foodAdapter = FoodAdapter2(){

        }
        binding.rcv.adapter = foodAdapter
        val randomItems = listData.shuffled().take((1..3).random()).toMutableList()
        binding.preview.setOnClickListener {
            binding.lnSHow.visible()
            binding.rl.gone()
            randomItems.clear()
            randomItems.addAll(listData.shuffled().take((1..3).random()))
            foodAdapter.updateList(randomItems)
        }

        binding.btnBack.setOnClickListener {
            binding.lnSHow.gone()
            binding.rl.visible()
        }

        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_food_list, null)
        val recyclerPopup = popupView.findViewById<RecyclerView>(R.id.recyclerPopup)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)


        binding.edtFind.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    val filteredList = listData.filter {
                        it.name.contains(query, ignoreCase = true)
                    }

                    if (filteredList.isNotEmpty()) {
                        val adapter = FoodAdapter2 { selectedItem ->
                            binding.edtFind.setText("")
                            randomItems.add(selectedItem)
                            foodAdapter.updateList(randomItems)
                            popupWindow.dismiss()
                        }
                        recyclerPopup.adapter = adapter

                        adapter.updateList(filteredList)

                        if (!popupWindow.isShowing) {
                            popupWindow.showAsDropDown(binding.edtFind)
                        }
                    } else {
                        popupWindow.dismiss()
                    }
                } else {
                    popupWindow.dismiss()
                }
            }
        })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.preview.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // Xử lý kết quả xin quyền
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val listData = listOf(
        FoodItem("Táo", "Trái cây", 0, "https://suckhoedoisong.qltns.mediacdn.vn/Images/haiyen/2018/12/10/tao.jpg", false),
        FoodItem("Chuối", "Trái cây", 0, "https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2021/10/14/chuoi1-16341869574602070184903.jpg", false),
        FoodItem("Gạo lứt", "Ngũ cốc", 0, "https://suckhoedoisong.qltns.mediacdn.vn/Images/nguyenkhanh/2018/08/25/nhung-tac-dung-cua-gao-luc-doi-voi-suc-khoe.-1.jpg", false),
        FoodItem("Gạo trắng", "Ngũ cốc", 0, "https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2022/8/16/chon-gao-ngon-16606367955691866664148.jpg", false),
        FoodItem("Khoai tây", "Rau củ", 0, "https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2022/12/10/chuyencuakhoaitaytb1-16706853682851812514449.jpg", false),
        FoodItem("Khoai lang", "Rau củ", 0, "https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2021/10/3/khoai-lang-16332714337561040798892.jpg", false),
        FoodItem("Bánh mì trắng", "Ngũ cốc", 0, "", false),
        FoodItem("Bánh mì nguyên cám", "Ngũ cốc", 0, "", false),
        FoodItem("Yến mạch", "Ngũ cốc", 0, "", false),
        FoodItem("Nho", "Trái cây", 0, "", false),
        FoodItem("Dưa hấu", "Trái cây", 0, "", false),
        FoodItem("Dâu tây", "Trái cây", 0, "", false),
        FoodItem("Sữa nguyên kem", "Sữa", 0, "", false),
        FoodItem("Sữa tách béo", "Sữa", 0, "", false),
        FoodItem("Sữa chua không đường", "Sữa", 0, "", false),
        FoodItem("Cam", "Trái cây", 0, "", false),
        FoodItem("Lê", "Trái cây", 0, "", false),
        FoodItem("Đậu lăng", "Họ đậu", 0, "", false),
        FoodItem("Đậu xanh", "Họ đậu", 0, "", false),
        FoodItem("Hạnh nhân", "Hạt", 0, "", false),
        FoodItem("Bắp ngô", "Ngũ cốc", 0, "", false),
        FoodItem("Khoai sọ", "Rau củ", 0, "", false),
        FoodItem("Mì ống lúa mì", "Ngũ cốc", 0, "", false),
        FoodItem("Bột yến mạch", "Ngũ cốc", 0, "", false),
        FoodItem("Hạt chia", "Hạt", 0, "", false),
        FoodItem("Hạt điều", "Hạt", 0, "", false),
        FoodItem("Bánh quy", "Bánh ngọt", 0, "", false),
        FoodItem("Mật ong", "Đồ ngọt", 0, "", false),
        FoodItem("Nước ngọt có ga", "Đồ uống", 0, "", false),
        FoodItem("Sô cô la đen", "Đồ ngọt", 0, "", false),
        FoodItem("Xoài", "Trái cây", 0, "", false),
        FoodItem("Bơ", "Trái cây", 0, "", false),
        FoodItem("Dứa", "Trái cây", 0, "", false),
        FoodItem("Mít", "Trái cây", 0, "", false),
        FoodItem("Mận", "Trái cây", 0, "", false),
        FoodItem("Ngô", "Ngũ cốc", 0, "", false),
        FoodItem("Lúa mạch", "Ngũ cốc", 0, "", false),
        FoodItem("Bột mì", "Ngũ cốc", 0, "", false),
        FoodItem("Bột gạo", "Ngũ cốc", 0, "", false),
        FoodItem("Bí đỏ", "Rau củ", 0, "", false),
        FoodItem("Cà rốt", "Rau củ", 0, "", false),
        FoodItem("Su hào", "Rau củ", 0, "", false),
        FoodItem("Đậu đen", "Họ đậu", 0, "", false),
        FoodItem("Đậu đỏ", "Họ đậu", 0, "", false),
        FoodItem("Đậu nành", "Họ đậu", 0, "", false),
        FoodItem("Hạt óc chó", "Hạt", 0, "", false),
        FoodItem("Hạt mè", "Hạt", 0, "", false),
        FoodItem("Hạt hướng dương", "Hạt", 0, "", false),
        FoodItem("Sữa bò", "Sữa", 0, "", false),
        FoodItem("Sữa đậu nành", "Sữa", 0, "", false),
        FoodItem("Sữa hạnh nhân", "Sữa", 0, "", false),
        FoodItem("Phô mai", "Sữa", 0, "", false),
        FoodItem("Sữa chua", "Sữa", 0, "", false),
        FoodItem("Cá hồi", "Hải sản", 0, "", false),
        FoodItem("Cá thu", "Hải sản", 0, "", false),
        FoodItem("Cá basa", "Hải sản", 0, "", false),
        FoodItem("Tôm", "Hải sản", 0, "", false),
        FoodItem("Mực", "Hải sản", 0, "", false),
        FoodItem("Thịt bò", "Thịt", 0, "", false),
        FoodItem("Thịt gà", "Thịt", 0, "", false),
        FoodItem("Thịt lợn", "Thịt", 0, "", false),
        FoodItem("Thịt cừu", "Thịt", 0, "", false),
        FoodItem("Trứng gà", "Trứng", 0, "", false),
        FoodItem("Trứng vịt", "Trứng", 0, "", false),
        FoodItem("Trứng cút", "Trứng", 0, "", false),
        FoodItem("Nấm hương", "Nấm", 0, "", false),
        FoodItem("Nấm kim châm", "Nấm", 0, "", false),
        FoodItem("Nấm bào ngư", "Nấm", 0, "", false),
    )
}