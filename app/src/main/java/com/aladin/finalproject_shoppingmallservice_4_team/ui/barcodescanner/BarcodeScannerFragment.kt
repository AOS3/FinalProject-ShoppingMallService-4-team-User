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
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.*

class BarcodeScannerFragment : Fragment() {

    private lateinit var fragmentBarcodeScannerBinding: FragmentBarcodeScannerBinding
    private var camera: Camera? = null
    private var isProcessing = false // 중복 인식을 방지하는 플래그
    private var lastProcessedTime = 0L // 마지막으로 처리된 시간

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
        fragmentBarcodeScannerBinding = FragmentBarcodeScannerBinding.inflate(layoutInflater, container, false)

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

            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(fragmentBarcodeScannerBinding.cameraPreviewBarcodeScanner.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                        processBarcodeFromImage(imageProxy)
                    }
                }
            try {
                cameraProvider.unbindAll()
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
        // 만약 이미 처리 중 이라면 종료
        if (isProcessing) {
            imageProxy.close()
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProcessedTime < 3000) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner: BarcodeScanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        if (barcode.valueType == Barcode.TYPE_ISBN) {
                            val isbn = barcode.displayValue
                            isbn?.let {
                                // 처리 중 플래그 설정
                                isProcessing = true
                                // 마지막 처리 시간 업데이트
                                lastProcessedTime = currentTime
                                Toast.makeText(requireContext(), "ISBN: $isbn", Toast.LENGTH_SHORT).show()

                                // 1초 대기 후 화면 전환
                                GlobalScope.launch(Dispatchers.Main) {
                                    delay(1000)
                                    navigateToBarcodeResultFragment(isbn)
                                    // 처리 중 플래그 해제
                                    isProcessing = false
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    // 화면 전환
    private fun navigateToBarcodeResultFragment(isbn: String) {
        val fragment = BarcodeScanResultFragment().apply {
            arguments = Bundle().apply {
                putString("ISBN", isbn)
            }
        }
        replaceSubFragment(fragment, true)
    }

    // 바코드 입력 버튼 클릭
    private fun inISBNButtonOnCLick() {
        fragmentBarcodeScannerBinding.apply {
            buttonBarcodeScannerInISBN.setOnClickListener {
                val dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_barcode_scanner_input, null)

                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .create()

                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val editTextISBN = dialogView.findViewById<EditText>(R.id.editTextISBN)
                val buttonCancel = dialogView.findViewById<Button>(R.id.button_dialog_negative)
                val buttonConfirm = dialogView.findViewById<Button>(R.id.button_dialog_positive)

                buttonCancel.setOnClickListener {
                    dialog.dismiss()
                }

                buttonConfirm.setOnClickListener {
                    val isbnInput = editTextISBN.text.toString()
                    if (isbnInput.length == 13) {
                        Toast.makeText(requireContext(), "입력된 ISBN: $isbnInput", Toast.LENGTH_SHORT).show()

//                        val mainFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView) as? MainFragment
//                        if (mainFragment == null) {
//                            activity?.supportFragmentManager?.commit {
//                                replace(R.id.fragmentContainerView, MainFragment())
//                                addToBackStack(null)
//                            }
//                            activity?.supportFragmentManager?.executePendingTransactions()
//                        }

                        val fragment = BarcodeScanResultFragment().apply {
                            arguments = Bundle().apply {
                                putString("ISBN", isbnInput)
                            }
                        }
//                        replaceMainFragment(fragment, true)
                        replaceSubFragment(fragment, true)

                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "ISBN은 13자리여야 합니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                dialog.show()
            }
        }
    }
}
