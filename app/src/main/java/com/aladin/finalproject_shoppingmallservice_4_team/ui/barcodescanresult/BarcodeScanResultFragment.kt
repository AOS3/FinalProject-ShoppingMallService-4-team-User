package com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanresult

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBarcodeScanResultBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BarcodeScanResultFragment : Fragment() {

    private lateinit var fragmentBarcodeScanResultBinding: FragmentBarcodeScanResultBinding
    private var isbn: String? = null
    private val TTBKey = BuildConfig.API_KEY // API 키

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isbn = arguments?.getString("ISBN")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBarcodeScanResultBinding =
            FragmentBarcodeScanResultBinding.inflate(inflater, container, false)

        isbn?.let { fetchBookData(it) }
        return fragmentBarcodeScanResultBinding.root
    }

    private fun createRetrofitInstance(): AladdinApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.aladin.co.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(AladdinApiService::class.java)
    }

    private fun fetchBookData(isbn: String) {
        val apiService = createRetrofitInstance()

        lifecycleScope.launch {
            try {
                val response = apiService.searchBooks(
                    apiKey = TTBKey,
                    query = isbn,
                    queryType = "ISBN"
                )

                // Raw JSON 확인
                Log.d("API_CALL", "Raw Response: $response")

                // items 확인
                if (response.items.isNullOrEmpty()) {
                    Log.e("API_CALL", "No items found in response.")
                    showToast("책 정보를 찾을 수 없습니다.")
                    return@launch
                }

                val book = response.items.firstOrNull()
                if (book != null) {
                    updateUI(book)
                } else {
                    Log.e("API_CALL", "No valid book found in items.")
                    showToast("책 정보를 찾을 수 없습니다.")
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "Exception during API call", e)
                showToast("API 호출 중 오류가 발생했습니다.")
            }
        }
    }


    private fun updateUI(book: BookItem) {
        with(fragmentBarcodeScanResultBinding) {
            Glide.with(requireContext())
                .load(book.cover)
                .into(imageViewBarcodeScanResultBookImage)

            textViewBarcodeScanResultBookName.text = book.title
            textViewBarcodeScanResultBookAuthor.text = book.author
            textViewBarcodeScanResultPrice.text = "₩${book.priceStandard}"
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
