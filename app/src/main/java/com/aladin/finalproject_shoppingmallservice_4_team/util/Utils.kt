package com.aladin.finalproject_shoppingmallservice_4_team.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.aladin.finalproject_shoppingmallservice_4_team.R

object Utils {
}

fun Fragment.replaceMainFragment(fragment: Fragment) {
    parentFragmentManager.commit {
        replace(R.id.fragmentContainerView, fragment)
    }
}

fun Fragment.replaceSubFragment(fragment: Fragment) {
    childFragmentManager.commit {
        replace(R.id.fragmentContainerView_main_sub, fragment)
    }
}