package com.aladin.finalproject_shoppingmallservice_4_team.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.MainActivity
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSettingBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.changePw.ChangePwFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.guide.GuideFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notice.NoticeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment


class SettingFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentSettingBinding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSettingBinding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        mainActivity = activity as MainActivity
        // 버튼 클릭 메서드
        onClickSettingView()
        // 툴바 메서드
        settingToolbar()
        return fragmentSettingBinding.root
    }

    private fun settingToolbar() {
        fragmentSettingBinding.apply {
            materialToolbarSetting.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }

    private fun onClickSettingView() {
        fragmentSettingBinding.apply {
            // 공지사항
            viewSettingNotice.setOnClickListener {
                replaceMainFragment(NoticeFragment(), true)
            }
            // 이용 약관
            viewSettingGuide.setOnClickListener {
                replaceMainFragment(GuideFragment(), true)
            }
            // 비밀번호 변경
            viewSettingChangePw.setOnClickListener {
                replaceMainFragment(ChangePwFragment(), true)
            }
            // 카메라 권한 설정
            viewSettingCameraPermission.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", mainActivity.packageName, null)
                }
                startActivity(intent)
            }
            // 알림 권한 설정
            viewSettingNotificationPermission.setOnClickListener {
                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, mainActivity.packageName)
                    }
                } else {
                    // Oreo 이전 버전에서는 앱 설정 페이지로 이동
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", mainActivity.packageName, null)
                    }
                }
                startActivity(intent)
            }
        }
    }
}