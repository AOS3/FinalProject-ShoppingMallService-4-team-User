package com.aladin.finalproject_shoppingmallservice_4_team.ui.likeList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentLikeListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.LikeListAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notice.NoticeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.clearAllBackStack
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikeListFragment : Fragment() {
    private lateinit var fragmentLikeListBinding: FragmentLikeListBinding
    private lateinit var bookApplication: BookApplication
    private lateinit var likeListAdapter: LikeListAdapter
    private val likeListViewModel: LikeListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLikeListBinding = FragmentLikeListBinding.inflate(layoutInflater)

        // bookApplication 초기화
        bookApplication = requireActivity().application as BookApplication

        // 로그인 체크
        checkLoginProcess()

        // 화면 입장 시 ProgresssDialog
        observeProgressDialog()

        // 옵저버
        observeBookListData()

        // 리싸이클러뷰
        setupRecyclerView()

        // 툴바 설정
        settingToolbar()

        return fragmentLikeListBinding.root
    }

    // Observer
    private fun observeBookListData() {
        fragmentLikeListBinding.apply {
            likeListViewModel.likeListBookList.observe(viewLifecycleOwner) { likeListBookList ->
                if (likeListBookList.isNotEmpty()) {
                    // 데이터를 받아왔을 때, RecyclerView에 전달
                    likeListAdapter =
                        LikeListAdapter(
                            likeListBookList,
                            this@LikeListFragment,
                            likeListViewModel,
                            requireContext()
                        )
                    recyclerViewLikeList.adapter = likeListAdapter
                    linearLayoutLikeListEmptyList.visibility = View.GONE
                    recyclerViewLikeList.visibility = View.VISIBLE
                } else {
                    linearLayoutLikeListEmptyList.visibility = View.VISIBLE
                    recyclerViewLikeList.visibility = View.INVISIBLE
                }
            }
        }
    }

    // Dialog
    private fun observeProgressDialog() {
        // 화면 입장 시 공지사항 로딩을 위한 Dialog
        val progressBarDialog = CustomDialogProgressbar(requireContext())

        likeListViewModel.isLoadLikeList.observe(viewLifecycleOwner) {
            if (it) {
                progressBarDialog.dismiss()
            }else {
                progressBarDialog.show()
            }
        }
    }

    // RecyclerView
    private fun setupRecyclerView() {
        fragmentLikeListBinding.apply {
            likeListAdapter =
                LikeListAdapter(
                    emptyList(),
                    this@LikeListFragment,
                    likeListViewModel,
                    requireContext()
                )
            recyclerViewLikeList.run {
                layoutManager = LinearLayoutManager(context)
                adapter = likeListAdapter
            }

        }
    }

    // Check Login
    private fun checkLoginProcess() {
        try {
            if (::bookApplication.isInitialized && bookApplication.loginUserModel != null) {
                likeListViewModel.gettingLikeListBookData(bookApplication.loginUserModel.userToken)
            } else {
                likeListViewModel.gettingLikeListBookData(bookApplication.loginUserModel.userToken)
            }
        } catch (e: Exception) {
            likeListViewModel.dismissProgressDialog()
            val loginDialog = CustomDialog(
                requireContext(),
                // 리스트 삭제 진행
                onPositiveClick = {
                    removeFragment()
                },
                contentText = "로그인을 먼저 진행해주세요.",
                icon = R.drawable.error_24px
            )
            loginDialog.showCustomDialog()
        }
    }

    // Toolbar
    private fun settingToolbar() {
        fragmentLikeListBinding.apply {
            materialToolbarLikeList.setNavigationOnClickListener {
                removeFragment()
            }
            materialToolbarLikeList.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_likeList_shoppingCart -> {
                        replaceSubFragment(ShoppingCartFragment(), true)
                        true
                    }

                    R.id.item_likeList_notification -> {
                        replaceSubFragment(NoticeFragment(), true)
                        true
                    }

                    else -> {
                        replaceSubFragment(MainMenuFragment(), true)
                        true
                    }
                }
            }
        }
    }
}