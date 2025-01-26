package com.aladin.finalproject_shoppingmallservice_4_team.ui.booksellinginquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBarcodeScannerBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookSellingInquiryBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentOrderInquiryBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingCartBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.RowBookSellingInquiryBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingInquiryModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.qualityguide.QualityGuideFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class BookSellingInquiryFragment : Fragment() {

    private lateinit var fragmentBookSellingInquiryBinding: FragmentBookSellingInquiryBinding
    private val viewModel: BookSellingInquiryViewModel by viewModels()
    private lateinit var adapter: RecyclerBookSellingInquiryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBookSellingInquiryBinding = FragmentBookSellingInquiryBinding.inflate(inflater, container, false)

        // Toolbar 설정 메서드 호출
        settingToolbar()

        // RecyclerView 설정 메서드 호출
        settingRecyclerView()

        // 총 도서 개수 업데이트
        totalCount()

        // 버튼 설정 메서드 호출
        buttonSetting()

        return fragmentBookSellingInquiryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Application에서 로그인된 사용자 토큰 가져오기
        val userToken = (requireActivity().application as BookApplication).loginUserModel?.userToken.orEmpty()


        // 데이터 로드 호출
        viewModel.loadSellingInquiries(userToken)

        // 데이터 관찰 및 UI 업데이트
        viewModel.items.observe(viewLifecycleOwner) { sellingInquiries ->
            if (sellingInquiries != null && sellingInquiries.isNotEmpty()) {
                // 데이터가 있을 경우
                adapter.updateData(sellingInquiries)
                fragmentBookSellingInquiryBinding.recyclerViewBookSellingInquiry.visibility = View.VISIBLE
                fragmentBookSellingInquiryBinding.includeBookSellingInquiryEmpty.root.visibility = View.GONE
            } else {
                // 데이터가 없을 경우
                fragmentBookSellingInquiryBinding.recyclerViewBookSellingInquiry.visibility = View.GONE
                fragmentBookSellingInquiryBinding.includeBookSellingInquiryEmpty.root.visibility = View.VISIBLE
            }
        }
    }

    // Toolbar를 설정 메서드
    private fun settingToolbar() {
        fragmentBookSellingInquiryBinding.apply {
            materialToolbarBookSellingInquiry.title = "판매 조회"
            materialToolbarBookSellingInquiry.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            materialToolbarBookSellingInquiry.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }

            materialToolbarBookSellingInquiry.menu.add(Menu.NONE, 1, Menu.NONE, "품질 가이드")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            materialToolbarBookSellingInquiry.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> replaceMainFragment(QualityGuideFragment(), true)
                }
                true
            }
        }
    }

    // 버튼 설정
    private fun buttonSetting() {
        fragmentBookSellingInquiryBinding.apply {
            buttonBookSellingInquiryAddBookForSelling.setOnClickListener {
                Toast.makeText(requireContext(), "알림 화면으로 가기", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 총 도서 개수
    private fun totalCount() {
        viewModel.documentCount.observe(viewLifecycleOwner) { count ->
            fragmentBookSellingInquiryBinding.textViewBookSellingInquiry.text = "중고 팔기 내역 (총 ${count}건)"
        }
    }

    // RecyclerView 설정 메서드
    private fun settingRecyclerView() {
        adapter = RecyclerBookSellingInquiryAdapter(emptyList())
        fragmentBookSellingInquiryBinding.recyclerViewBookSellingInquiry.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BookSellingInquiryFragment.adapter
            addItemDecoration(MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    // RecyclerView 어댑터
    private inner class RecyclerBookSellingInquiryAdapter(
        private var items: List<SellingInquiryModel>
    ) : RecyclerView.Adapter<RecyclerBookSellingInquiryAdapter.ViewHolder>() {

        inner class ViewHolder(private val binding: RowBookSellingInquiryBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: SellingInquiryModel) {
                // 상태에 따른 텍스트와 색상 설정
                val state = when (item.sellingInquiryState) {
                    0 -> {
                        "승인 신청"
                    }
                    1 -> {
                        binding.textViewBookSellingInquiryListState.setTextColor(
                            ContextCompat.getColor(binding.root.context, R.color.orange_color)
                        ) // FF6A00
                        "품질 검수 중"
                    }
                    2 -> {
                        binding.textViewBookSellingInquiryListState.setTextColor(
                            ContextCompat.getColor(binding.root.context, R.color.blue_color)
                        ) // 0064C8
                        binding.textViewBookSellingInquiryListQuality.setTextColor(
                            ContextCompat.getColor(binding.root.context, R.color.green_color)
                        ) // 32BE07
                        "검수 완료"
                    }
                    else -> "오류"
                }

                // 상태 텍스트 설정
                binding.textViewBookSellingInquiryListState.text = state

                // 품질 설정
                val quality = when (item.sellingInquiryQuality) {
                    0 -> "상"
                    1 -> "중"
                    2 -> "하"
                    else -> "오류"
                }

                // 검수 완료 상태에서 직원 품질도 추가로 표시
                val qualityText = if (item.sellingInquiryState == 2) {
                    val staffQuality = when (item.sellingInquiryChoiceQuality) {
                        0 -> "상"
                        1 -> "중"
                        2 -> "하"
                        else -> "오류"
                    }
                    "내가 선택한 품질: $quality -> $staffQuality"
                } else {
                    "내가 선택한 품질: $quality"
                }
                binding.textViewBookSellingInquiryListQuality.text = qualityText

                // 나머지 데이터 설정
                binding.textViewBookSellingInquiryListName.text = item.sellingInquiryBookName
                binding.textViewBookSellingInquiryListAuthor.text = item.sellingInquiryBookAuthor
                binding.textViewBookSellingInquiryListPrice.text = "예상 판매가: ${item.sellingInquiryPrice}원"

                // 날짜 형식 변환
                val formattedDate = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(
                    Date(item.sellingInquiryTime)
                )
                binding.textViewBookSellingInquiryListDate.text = "등록 날짜: $formattedDate"
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RowBookSellingInquiryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        fun updateData(newItems: List<SellingInquiryModel>) {
            items = newItems.sortedByDescending { it.sellingInquiryTime }
            notifyDataSetChanged()
        }
    }
}
