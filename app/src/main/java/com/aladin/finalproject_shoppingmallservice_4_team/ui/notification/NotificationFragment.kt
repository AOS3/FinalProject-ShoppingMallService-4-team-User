package com.aladin.finalproject_shoppingmallservice_4_team.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentHomeBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentNotificationBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.NotificationModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.NotificationAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.NotificationOnClickListener
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment(), NotificationOnClickListener {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val adapter: NotificationAdapter by lazy { NotificationAdapter(this) }
    private lateinit var bookApplication: BookApplication

    private val viewModel: NotificationViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        bookApplication = requireActivity().application as BookApplication

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        combineButtonMethod()
        settingRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    버튼
     */

    private fun combineButtonMethod() {
        settingBackButton()
        settingDeleteAllButton()
    }

    private fun settingDeleteAllButton() {
        binding.buttonNotificationDeleteAll.setOnClickListener {
            viewModel.deleteAllNotificationData()
        }
    }

    /*
    툴바
     */

    private fun settingBackButton() = binding.materialToolbarNotification.setNavigationOnClickListener { removeFragment() }

    /*
    리사이클러 뷰
     */

    override fun itemClickListener(item: NotificationModel) {
        // 상세 화면으로 보낸다
        val dataBundle = Bundle()
        dataBundle.putString("notifyTitle", item.notificationTitle)
        dataBundle.putString("notifyContent", item.notificationContent)
        dataBundle.putLong("notifyTime", item.notificationTime)
        viewModel.seeData(item)
        replaceMainFragment(NotificationDetailFragment(), true, dataBundle = dataBundle)
    }

    private fun settingRecyclerView() {
        binding.recyclerViewNotification.adapter = adapter
        updateList()
    }

    private fun updateList() {
        viewModel.notificationList.observe(viewLifecycleOwner) {
            adapter.updateList(it.sortedByDescending { it.notificationTime }.toMutableList())
            if(it.isNotEmpty()) {
                binding.scrollViewNotification.isVisible = true
                binding.nofiticationEmpty.root.isVisible = false
                val size = it.filter { it.notificationSee == 0 }
                binding.textViewNotificationSize.text = "미열람 알림 : ${size.size}개"
                // 리사이클러뷰에 스와이프, 드래그 기능 달기
                val swipeHelperCallback =
                    NotificationSwipeCallback(adapter, it.toMutableList(), viewModel, icon = ContextCompat.getDrawable(requireContext(), R.drawable.delete_24px)!!)
                ItemTouchHelper(swipeHelperCallback).attachToRecyclerView(binding.recyclerViewNotification)
            }
            else {
                binding.scrollViewNotification.isVisible = false
                binding.nofiticationEmpty.root.isVisible = true
            }
        }
    }

    /*
    데이터
     */

    // Dialog
    private fun observeProgressDialog() {
        // 화면 입장 시 공지사항 로딩을 위한 Dialog
        val progressBarDialog = CustomDialogProgressbar(requireContext())
        progressBarDialog.show()

        viewModel.isLoadNotificationList.observe(viewLifecycleOwner) {
            if (it) {
                progressBarDialog.dismiss()
            }
        }
    }

    private fun loadData() {
        try {
            if (::bookApplication.isInitialized && bookApplication.loginUserModel != null) {
                viewModel.loadNotificationData(userToken = bookApplication.loginUserModel.userToken)
                observeProgressDialog()
            }
        } catch (e: Exception) {
            val loginDialog = CustomDialog(
                requireContext(),
                onPositiveClick = {
                    removeFragment()
                    replaceMainFragment(LoginFragment(), true)
                },
                contentText = "로그인을 먼저 진행해주세요.",
                icon = R.drawable.error_24px
            )
            loginDialog.showCustomDialog()
        }
    }


}