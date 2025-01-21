package com.aladin.finalproject_shoppingmallservice_4_team.ui.booklist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.NewBookListAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.UsedBookListAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookListFragment : Fragment() {
    private lateinit var fragmentBookListBinding: FragmentBookListBinding

    // 중고책 adapter
    private lateinit var usedBookListAdapter: UsedBookListAdapter

    // 신책 adapter
    private lateinit var newBookListAdapter: NewBookListAdapter

    private val bookListViewModel: BookListViewModel by viewModels()
    private lateinit var queryData: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookListBinding = FragmentBookListBinding.inflate(layoutInflater, container, false)

        // 상황에 맞는 BookList 가져오기
        checkBookQuery()

        // 드롭다운 세팅
        settingDropMenu()

        // 툴바 세팅
        settingToolbar()

        // 옵저버 세팅
        observeBookListData()

        // 리싸이클러뷰 세팅
        setupRecyclerView()

        // Progress Dialog
        observeProgressDialog()

        // 주제 Text 변경
        changeBookListSubject()

        return fragmentBookListBinding.root
    }


    private fun changeBookListSubject() {
        fragmentBookListBinding.apply {
            arguments?.getString("bookQuery")?.let { value ->
                when (value) {
                    // 중고 도서
                    "Used" -> {
                        textViewBookListSubject.text = "중고 도서 목록"
                        materialToolbarBookList.title = "중고 도서"
                    }
                    // 신간 전체
                    "ItemNewAll" -> {
                        textViewBookListSubject.text = "신간 도서 목록"
                        materialToolbarBookList.title = "신간 도서"
                    }
                    // 베스트셀러
                    "Bestseller" -> {
                        textViewBookListSubject.text = "베스트 셀러 목록"
                        materialToolbarBookList.title = "베스트 셀러"
                    }
                    // 블로그 베스트 셀러
                    "BlogBest" -> {
                        textViewBookListSubject.text = "블로그 베스트 셀러 목록"
                        materialToolbarBookList.title = "블로그 베스트 셀러"
                    }
                }
            }
        }
    }

    // Dialog
    private fun observeProgressDialog() {
        // 화면 입장 시 공지사항 로딩을 위한 Dialog
        val progressBarDialog = CustomDialogProgressbar(requireContext())
        progressBarDialog.show()

        bookListViewModel.isLoadBookList.observe(viewLifecycleOwner) {
            if (it) {
                progressBarDialog.dismiss()
            }
        }
    }

    // Toolbar
    private fun settingToolbar() {
        fragmentBookListBinding.apply {
            materialToolbarBookList.setNavigationOnClickListener {
                removeFragment()
            }
            materialToolbarBookList.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.item_bookList_notification -> {
                        replaceSubFragment(MainMenuFragment(),true)
                    }
                    R.id.item_bookList_menu -> {
                        replaceSubFragment(MainMenuFragment(),true)
                    }
                }
                true
            }
        }
    }

    // Setting
    private fun checkBookQuery() {
        if (arguments != null) {
            val query = arguments?.getString("bookQuery")!!
            queryData = query
            bookListViewModel.gettingBookList(queryData)
        }
    }

    // RecyclerView
    private fun setupRecyclerView() {
        fragmentBookListBinding.apply {
            arguments?.getString("bookQuery")?.let { value ->
                if (value == "Used") {
                    usedBookListAdapter =
                        UsedBookListAdapter(emptyList(), this@BookListFragment) // 초기값으로 빈 리스트
                    recyclerViewBookList.layoutManager = LinearLayoutManager(context)
                    recyclerViewBookList.adapter = usedBookListAdapter
                } else {
                    newBookListAdapter =
                        NewBookListAdapter(emptyList(), this@BookListFragment) // 초기값으로 빈 리스트
                    recyclerViewBookList.layoutManager = LinearLayoutManager(context)
                    recyclerViewBookList.adapter = newBookListAdapter
                }
            }

        }
    }

    // Observer
    private fun observeBookListData() {
        fragmentBookListBinding.apply {
            arguments?.getString("bookQuery")?.let { value ->
                if (value == "Used") {
                    // LiveData에서 데이터를 받아와서 RecyclerView 업데이트
                    bookListViewModel.usedBookList.observe(viewLifecycleOwner) { usedBookList ->
                        if (usedBookList.isNotEmpty()) {
                            // 데이터를 받아왔을 때, RecyclerView에 전달
                            usedBookListAdapter =
                                UsedBookListAdapter(
                                    usedBookList,
                                    this@BookListFragment
                                ) // 새 데이터로 Adapter를 갱신
                            recyclerViewBookList.adapter = usedBookListAdapter
                            linearLayoutBookListEmptyBookList.visibility = View.GONE
                        } else {
                            // 데이터가 없을 시
                            linearLayoutBookListEmptyBookList.visibility = View.VISIBLE
                        }
                    }
                } else {
                    bookListViewModel.newBookList.observe(viewLifecycleOwner) { newBookList ->
                        if (newBookList.isNotEmpty()) {
                            // 데이터를 받아왔을 때, RecyclerView에 전달
                            // 데이터를 받아왔을 때, RecyclerView에 전달
                            newBookListAdapter =
                                NewBookListAdapter(
                                    newBookList,
                                    this@BookListFragment
                                ) // 새 데이터로 Adapter를 갱신
                            recyclerViewBookList.adapter = newBookListAdapter
                            linearLayoutBookListEmptyBookList.visibility = View.GONE
                        } else {
                            // 데이터가 없을 시
                            linearLayoutBookListEmptyBookList.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    // DropDown
    private fun settingDropMenu() {
        fragmentBookListBinding.apply {
            // 드롭다운 아이템 목록
            val sortOptions = arrayOf("이름순", "최고가", "최저가")

            // 어댑터 설정
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
            autoCompleteTextViewBookListSortOrder.setAdapter(adapter)

            // 드롭다운 선택 시 동작
            autoCompleteTextViewBookListSortOrder.setOnItemClickListener { _, _, position, _ ->
                when (sortOptions[position]) {
                    "이름순" -> {
                        renewalRecyclerViewFromDropDown(1)
                        imageViewBookListDropDownText.text = "이름순"
                    }

                    "최고가" -> {
                        renewalRecyclerViewFromDropDown(2)
                        imageViewBookListDropDownText.text = "최고가"
                    }

                    "최저가" -> {
                        renewalRecyclerViewFromDropDown(3)
                        imageViewBookListDropDownText.text = "최저가"
                    }
                }
            }
            // 드롭다운이 닫혔을 때 화살표 모양 바뀌게
            autoCompleteTextViewBookListSortOrder.setOnDismissListener {
                imageViewBookListDropDownIcon.setImageResource(R.drawable.arrow_drop_down_24px)
            }

            // 드롭다운 열리게 설정, 화살표 변경 / 이미 열려있으면 닫히게 설정
            linearLayoutBookListSortOrder.setOnClickListener {
                if (!autoCompleteTextViewBookListSortOrder.isPopupShowing) { // 드롭다운이 열려 있는지 확인
                    autoCompleteTextViewBookListSortOrder.showDropDown()
                    imageViewBookListDropDownIcon.setImageResource(R.drawable.arrow_drop_up_24px)
                } else {
                    autoCompleteTextViewBookListSortOrder.dismissDropDown() // 이미 열려 있으면 닫기
                    imageViewBookListDropDownIcon.setImageResource(R.drawable.arrow_drop_down_24px)
                }
            }
        }
    }

    // 1 -> 이름순
    // 1 -> 최고가
    // 1 -> 최저가
    // 드롭다운 선택에 따라 recyclerView가 갱신
    private fun renewalRecyclerViewFromDropDown(dropDownValue: Int) {
        fragmentBookListBinding.apply {
            arguments?.getString("bookQuery")?.let { value ->
                if (value == "Used") {
                    // 중고인 경우
                    when (dropDownValue) {
                        1 -> usedBookListAdapter.sortByName()
                        2 -> usedBookListAdapter.sortByHighestPrice()
                        else -> usedBookListAdapter.sortByLowestPrice()
                    }
                } else {
                    // 새거인 경우
                    when (dropDownValue) {
                        1 -> newBookListAdapter.sortByName()
                        2 -> newBookListAdapter.sortByHighestPrice()
                        else -> newBookListAdapter.sortByLowestPrice()
                    }
                }
            }
        }
    }
}