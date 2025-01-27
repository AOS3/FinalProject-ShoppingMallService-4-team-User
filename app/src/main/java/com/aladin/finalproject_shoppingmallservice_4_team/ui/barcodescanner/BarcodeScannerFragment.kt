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
import kotlinx.coroutines.*

class BarcodeScannerFragment : Fragment() {

    private lateinit var fragmentBarcodeScannerBinding: FragmentBarcodeScannerBinding
    private var camera: Camera? = null
    private var isProcessing = false
    private var lastProcessedTime = 0L

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

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                        val bookItem = BookItem(
                            title = "Sample Title",
                            author = "Sample Author",
                            pubDate = "2023-01-01",
                            publisher = "Sample Publisher",
                            priceStandard = 10000,
                            priceSales = 8000,
                            cover = "",
                            description = "Sample Description",
                            link = "",
                            isbn = "",
                            categoryName = "Sample Category",
                            isbn13 = ""
                        )
                        processBarcodeFromImage(imageProxy, bookItem)
                    }
                }
            try {
                // 기존에 바인딩된 카메라가 있으면 해제
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "카메라를 시작할 수 없습니다.", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processBarcodeFromImage(imageProxy: ImageProxy, item: BookItem) {
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
                            val updatedItem = item.copy(isbn13 = barcode.displayValue ?: "")
                            isProcessing = true
                            lastProcessedTime = currentTime
                            Toast.makeText(requireContext(), "ISBN: ${updatedItem.isbn13}", Toast.LENGTH_SHORT).show()

                            GlobalScope.launch(Dispatchers.Main) {
                                delay(1000)
                                navigateBasedOnQuery(updatedItem)
                                isProcessing = false
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
                        val bookItem = BookItem(
                            title = "Sample Title",
                            author = "Sample Author",
                            pubDate = "2023-01-01",
                            publisher = "Sample Publisher",
                            priceStandard = 10000,
                            priceSales = 8000,
                            cover = "",
                            description = "Sample Description",
                            link = "",
                            isbn = isbnInput,
                            categoryName = "Sample Category",
                            isbn13 = isbnInput
                        )
                        navigateBasedOnQuery(bookItem)
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
