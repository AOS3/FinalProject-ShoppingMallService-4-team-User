package com.aladin.finalproject_shoppingmallservice_4_team.ui.register

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentRegisterStep1Binding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentRegisterStep2Binding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment

class RegisterStep2Fragment : Fragment() {

    lateinit var fragmentRegisterStep2Binding: FragmentRegisterStep2Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentRegisterStep2Binding = FragmentRegisterStep2Binding.inflate(layoutInflater)

        settingRegister2Toolbar()

        registerButtonListener()

        return fragmentRegisterStep2Binding.root
    }

    // 툴바를 설정하는 메서드
    fun settingRegister2Toolbar(){
        fragmentRegisterStep2Binding.apply {
            toolbarRegisterStep2.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarRegisterStep2.setNavigationOnClickListener {
                removeFragment()
            }

        }
    }

    // 다음 단계로 이동하는 메서드
    fun registerButtonListener(){
        fragmentRegisterStep2Binding.apply {
            buttonRegisterStep2Register.setOnClickListener {
                replaceMainFragment(RegisterStep3Fragment(),false)
            }
        }
    }

}