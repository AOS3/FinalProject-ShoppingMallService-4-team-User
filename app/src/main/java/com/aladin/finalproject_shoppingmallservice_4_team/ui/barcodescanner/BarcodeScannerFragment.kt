package com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBarcodeScannerBinding

class BarcodeScannerFragment : Fragment() {

    private lateinit var fragmentBarcodeScannerBinding: FragmentBarcodeScannerBinding
    private var camera: Camera? = null

    // 권한 요청 런처
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCameraPreview()
        } else {
            Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBarcodeScannerBinding = FragmentBarcodeScannerBinding.inflate(inflater, container, false)

        // Toolbar 설정 메서드 호출
        settingToolbar()
        // 권한 요청 메서드 호출
        checkCameraPermission()
        // 바코드 입력 버튼 클릭 메서드 호출
        inISBNButtonOnCLick()

        return fragmentBarcodeScannerBinding.root
    }

    // Toolbar 설정 메서드
    private fun settingToolbar() {
        fragmentBarcodeScannerBinding.toolbarBarcodeScanner.apply {
            title = "제품 추가"
            setNavigationIcon(R.drawable.arrow_back_ios_24px)
            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    // 권한 요청 메서드
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCameraPreview()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // CameraX 미리보기 시작
    private fun startCameraPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // 미리보기 설정
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(fragmentBarcodeScannerBinding.cameraPreviewBarcodeScanner.surfaceProvider)
            }

            // 후면 카메라 선택
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // 기존에 바인딩된 카메라가 있으면 해제
                cameraProvider.unbindAll()

                // 카메라와 미리보기 연결
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "카메라를 시작할 수 없습니다.", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // 바코드 입력 버튼 클릭 메서드
    private fun inISBNButtonOnCLick() {
        fragmentBarcodeScannerBinding.buttonBarcodeScannerInISBN.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(requireContext())
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_barcode_scanner_input, null)

            builder.setView(dialogView)
                .setCancelable(true)

            val editTextISBN = dialogView.findViewById<EditText>(R.id.editTextISBN)

            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }

            builder.setPositiveButton("확인") { dialog, _ ->
                val isbnInput = editTextISBN.text.toString()
                if (isbnInput.length == 13) {
                    Toast.makeText(requireContext(), "입력된 ISBN: $isbnInput", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "ISBN은 13자리여야 합니다.", Toast.LENGTH_SHORT).show()
                }
            }

            builder.create().show()
        }
    }
}
