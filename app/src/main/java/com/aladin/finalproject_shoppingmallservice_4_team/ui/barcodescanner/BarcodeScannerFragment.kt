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
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingcart.SellingCartFragment
import android.widget.Button
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.app.AlertDialog
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.fragment.app.viewModels
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingCartModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.search.SearchFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingsearch.SellingSearchFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class BarcodeScannerFragment : Fragment() {

    private lateinit var fragmentBarcodeScannerBinding: FragmentBarcodeScannerBinding
    private val viewModel: BarcodeScannerViewModel by viewModels()
    private var camera: Camera? = null
    private var isProcessing = false
    private var lastProcessedTime = 0L

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBarcodeScannerBinding = FragmentBarcodeScannerBinding.inflate(layoutInflater, container, false)

        // Toolbar 설정 메서드 호출
        settingToolbar()

        // 카메라 권한 확인 메서드 호출
        checkCameraPermission()

        // 관찰 메서드 호출
        setupObservers()

        // ISBN 수동 입력 메서드 호출
        setupManualISBNInput()

        return fragmentBarcodeScannerBinding.root
    }

    // Toolbar 설정 메서드
    private fun settingToolbar() {
        fragmentBarcodeScannerBinding.toolbarBarcodeScanner.apply {
            title = "바코드 검색"
            setNavigationIcon(R.drawable.arrow_back_ios_24px)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }
    }

    // 카메라 권한 확인 메서드
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> startCameraPreview()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> permissionLauncher.launch(Manifest.permission.CAMERA)
            else -> permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // 카메라 PreView 시작 메서드
    private fun startCameraPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(fragmentBarcodeScannerBinding.cameraPreviewBarcodeScanner.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageAnalyzer = androidx.camera.core.ImageAnalysis.Builder()
                .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                        processBarcodeFromImage(imageProxy)
                    }
                }
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "카메라를 시작할 수 없습니다.", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // 바코드 처리 메서드
    @OptIn(ExperimentalGetImage::class)
    private fun processBarcodeFromImage(imageProxy: androidx.camera.core.ImageProxy) {
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
                            isProcessing = true
                            lastProcessedTime = currentTime

                            Toast.makeText(requireContext(), "ISBN 인식 중...", Toast.LENGTH_SHORT).show()

                            // 1초 대기 후 데이터 처리 시작
                            GlobalScope.launch(Dispatchers.Main) {
                                delay(1000)
                                viewModel.searchByIsbn(barcode.displayValue ?: "")
                                isProcessing = false
                            }
                            break
                        }
                    }
                }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }

    // 관찰 메서드
    private fun setupObservers() {
        viewModel.BarcodeScannerBooks.observe(viewLifecycleOwner) { books ->
            val book = books.firstOrNull()
            book?.let {
                navigateBasedOnQuery(it)
            }
            isProcessing = false
        }
    }

    // 쿼리에 따라 다른 프래그먼트로 이동하는 메서드
    private fun navigateBasedOnQuery(item: BookItem) {
        val fragmentQuery = arguments?.getString("FragmentQuery")
        val userToken = arguments?.getString("userToken")
        when (fragmentQuery) {
            "SellingCart" -> {
                if (userToken.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "사용자 정보가 없습니다. 로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show()
                    return
                }

                val sellingCartItem = SellingCartModel(
                    sellingCartSellingPrice = (item.priceStandard * 0.7).toInt(),
                    sellingCartQuality = 0,
                    sellingCartISBN = item.isbn13,
                    sellingCartUserToken = userToken,
                    sellingCartTime = System.currentTimeMillis(),
                    sellingCartState = 0
                )

                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("SellingCartTable")
                    .add(sellingCartItem)

                removeFragment()
                replaceMainFragment(SellingCartFragment(), true)
            }
            else -> {
                val fragment = when (fragmentQuery) {
                    "SellingSearch" -> SellingSearchFragment().apply {
                        arguments = Bundle().apply { putString("ISBN", item.isbn13) }
                    }
                    "Search" -> SearchFragment().apply {
                        arguments = Bundle().apply { putString("ISBN", item.isbn13) }
                    }
                    else -> BarcodeScanResultFragment().apply {
                        arguments = Bundle().apply { putString("ISBN", item.isbn13) }
                    }
                }
                removeFragment()
                replaceMainFragment(fragment, true)
            }
        }
    }

    // ISBN 수동 입력 메서드
    private fun setupManualISBNInput() {
        fragmentBarcodeScannerBinding.buttonBarcodeScannerInISBN.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_barcode_scanner_input, null)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

            val editTextISBN = dialogView.findViewById<EditText>(R.id.editTextISBN)
            val buttonCancel = dialogView.findViewById<Button>(R.id.button_dialog_negative)
            val buttonConfirm = dialogView.findViewById<Button>(R.id.button_dialog_positive)

            buttonCancel.setOnClickListener { dialog.dismiss() }
            buttonConfirm.setOnClickListener {
                val isbnInput = editTextISBN.text.toString()
                if (isbnInput.length == 13) {
                    viewModel.searchByIsbn(isbnInput)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "ISBN은 13자리여야 합니다.", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }
    }
}