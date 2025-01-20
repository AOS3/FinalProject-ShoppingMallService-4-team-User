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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBarcodeScannerBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanresult.BarcodeScanResultFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import android.widget.Button
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.app.AlertDialog
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.fragment.app.commit
import com.aladin.finalproject_shoppingmallservice_4_team.ui.main.MainFragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBarcodeScannerBinding = FragmentBarcodeScannerBinding.inflate(layoutInflater,container,false)

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

            // ImageAnalysis 설정
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                        processBarcodeFromImage(imageProxy)
                    }
                }

            try {
                // 기존에 바인딩된 카메라가 있으면 해제
                cameraProvider.unbindAll()

                // 카메라와 미리보기, 이미지 분석 연결
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "카메라를 시작할 수 없습니다.", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // 바코드 이미지 처리
    @OptIn(ExperimentalGetImage::class)
    private fun processBarcodeFromImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner: BarcodeScanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        // ISBN 코드인지 확인
                        if (barcode.valueType == Barcode.TYPE_ISBN) {
                            val isbn = barcode.displayValue
                            isbn?.let {
                                Toast.makeText(requireContext(), "ISBN: $isbn", Toast.LENGTH_SHORT).show()

                                // MainFragment 확인 및 전환
                                val mainFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView) as? MainFragment
                                if (mainFragment == null) {
                                    // MainFragment로 전환 후 SubFragment 설정
                                    activity?.supportFragmentManager?.commit {
                                        replace(R.id.fragmentContainerView, MainFragment())
                                        addToBackStack(null)
                                    }
                                    activity?.supportFragmentManager?.executePendingTransactions()
                                }

                                val fragment = BarcodeScanResultFragment().apply {
                                    arguments = Bundle().apply {
                                        putString("ISBN", isbn)
                                    }
                                }
                                replaceSubFragment(fragment, true)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close() // 이미지 리소스 해제
                }
        } else {
            imageProxy.close() // 이미지 리소스 해제
        }
    }

    // 바코드 입력 버튼 클릭 메서드
    private fun inISBNButtonOnCLick() {
        fragmentBarcodeScannerBinding.apply {
            buttonBarcodeScannerInISBN.setOnClickListener {
                val dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_barcode_scanner_input, null)

                // 다이얼로그 생성
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .create()

                // 다이얼로그 배경을 투명하게 설정
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                // 레이아웃 내부의 요소 참조 및 설정
                val editTextISBN = dialogView.findViewById<EditText>(R.id.editTextISBN)
                val buttonCancel = dialogView.findViewById<Button>(R.id.button_dialog_negative)
                val buttonConfirm = dialogView.findViewById<Button>(R.id.button_dialog_positive)

                // 취소 버튼 클릭 이벤트
                buttonCancel.setOnClickListener {
                    dialog.dismiss()
                }

                buttonConfirm.setOnClickListener {
                    val isbnInput = editTextISBN.text.toString()
                    if (isbnInput.length == 13) {
                        Toast.makeText(requireContext(), "입력된 ISBN: $isbnInput", Toast.LENGTH_SHORT).show()

                        // MainFragment 확인 및 전환
                        val mainFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView) as? MainFragment
                        if (mainFragment == null) {
                            // MainFragment로 전환 후 SubFragment 설정
                            activity?.supportFragmentManager?.commit {
                                replace(R.id.fragmentContainerView, MainFragment())
                                addToBackStack(null)
                            }
                            activity?.supportFragmentManager?.executePendingTransactions()
                        }

                        val fragment = BarcodeScanResultFragment().apply {
                            arguments = Bundle().apply {
                                putString("ISBN", isbnInput)
                            }
                        }
                        replaceSubFragment(fragment, true)

                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "ISBN은 13자리여야 합니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                // 다이얼로그 표시
                dialog.show()
            }
        }
    }
}
