package com.aladin.finalproject_shoppingmallservice_4_team.util

import android.content.Context
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.aladin.finalproject_shoppingmallservice_4_team.R

fun Fragment.replaceMainFragment(
    fragment: Fragment,
    isAddToBackStack: Boolean,
    dataBundle: Bundle? = null,
    backStackTag: String? = null
) {
    // bundle 객체가 null이 아니라면
    dataBundle?.let {
        fragment.arguments = it
    }
    parentFragmentManager.commit {
        replace(R.id.fragmentContainerView, fragment)
        if (isAddToBackStack) {
            addToBackStack(backStackTag)
        }
    }
}

fun Fragment.replaceSubFragment(
    fragment: Fragment,
    isAddToBackStack: Boolean,
    dataBundle: Bundle? = null,
    backStackTag: String? = null
) {
    // bundle 객체가 null이 아니라면
    dataBundle?.let {
        fragment.arguments = it
    }
    parentFragmentManager.commit {
        replace(R.id.fragmentContainerView_main_sub, fragment)
        if (isAddToBackStack) {
            addToBackStack(backStackTag)
        }
    }
}

fun Fragment.clearAllBackStack() {
    parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun Fragment.removeFragment() {
    parentFragmentManager.popBackStack()
}

fun TextView.showSoftInput() {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus() // TextField에 포커스 설정

    // UI 준비 후 키보드 올리기
    this.postDelayed({
        inputManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }, 200) // 안전성을 위해 약간의 지연 추가
}

fun TextView.hideSoftInput() {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    // 키보드 내리기
    inputManager.hideSoftInputFromWindow(this.windowToken, 0)
    this.clearFocus() // 포커스 해제
}