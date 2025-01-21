package com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingsearch

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingSearchBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.RowSellingSearchBinding
import com.bumptech.glide.Glide
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellingSearchFragment : Fragment() {

    private var _binding: FragmentSellingSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SellingSearchViewModel by viewModels()
    private var bookList: MutableList<BookItem> = mutableListOf()
    private var query: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellingSearchBinding.inflate(inflater, container, false)

        // Toolbar 설정 메서드 호출
        settingToolbar()

        // 검색 기능 설정 메서드 호출
        setupSearchFeature()

        // RecyclerView 설정 메서드 호출
        setupRecyclerView()

        // "더보기" 버튼 설정 메서드 호출
        setupMoreButton()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // Toolbar 설정 메서드
    private fun settingToolbar() {
        binding.materialToolbarSellingSearch.apply {
            title = "판매할 도서 검색"
            setNavigationIcon(R.drawable.arrow_back_ios_24px)
            setNavigationOnClickListener {
                Toast.makeText(requireContext(), "뒤로 이동합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // 검색 기능 설정 메서드
    private fun setupSearchFeature() {
        val performSearch = {
            query = binding.editTextSellingSearchSearch.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchBooks(query, 10, "Accuracy")
                binding.editTextSellingSearchSearch.text.clear()
            } else {
                Toast.makeText(requireContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editTextSellingSearchSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.imageViewSellingSearchSearchIcon.setOnClickListener {
            performSearch()
        }
    }


    // RecyclerView 설정 메서드
    private fun setupRecyclerView() {
        binding.recyclerViewSellingSearchInfo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            adapter = object : RecyclerView.Adapter<SellingSearchViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellingSearchViewHolder {
                    val binding = RowSellingSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    return SellingSearchViewHolder(binding)
                }

                override fun onBindViewHolder(holder: SellingSearchViewHolder, position: Int) {
                    val book = bookList[position]
                    holder.bind(book)
                }

                override fun getItemCount(): Int = bookList.size
            }
        }

        viewModel.sellingSearchBooks.observe(viewLifecycleOwner) { books ->
            bookList.clear()
            bookList.addAll(books)
            binding.recyclerViewSellingSearchInfo.adapter?.notifyDataSetChanged()

            val isDataEmpty = books.isEmpty()
            binding.includeSellingSearchEmpty.root.isVisible = isDataEmpty
            binding.recyclerViewSellingSearchInfo.isVisible = !isDataEmpty
        }
    }


    // "더보기" 버튼 설정 메서드
    private fun setupMoreButton() {
        binding.buttonSellingSearchAddBookForSelling.setOnClickListener {
            if (query.isNotEmpty()) {
                viewModel.loadMoreBooks(query, "Accuracy")
            } else {
                Toast.makeText(requireContext(), "검색 후 더보기를 눌러주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // ViewHolder
    private inner class SellingSearchViewHolder(private val binding: RowSellingSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: BookItem) {
            binding.apply {
                // 책 이미지를 업데이트
                Glide.with(requireContext())
                    .load(book.cover)
                    .into(imageViewSellingSearchBook)
                textViewSellingSearchBookTitle.text = book.title
                textViewSellingSearchBookAuthor.text = book.author
                textViewSellingSearchBookPrice.text = "정가 : ${book.priceStandard}원"

                buttonSellingSearchRegister.setOnClickListener {
                    Toast.makeText(
                        requireContext(),
                        "${book.title} 도서가 팔기 등록되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
