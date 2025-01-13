package com.aladin.finalproject_shoppingmallservice_4_team

import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ActivityMainBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.splash.SplashFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.ShopFragmentName
import com.google.android.material.transition.MaterialSharedAxis
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    // 현재 Fragment와 다음 Fragment를 담을 변수(애니메이션 이동 때문에...)
    private var newFragment: Fragment? = null
    private var oldFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // 드래그로 여는 것을 막음
        activityMainBinding.drawerLayoutMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        setBottomNavigationView()
        replaceFragment(ShopFragmentName.SPLASH_FRAGMENT, false, false, null,)
    }

    // 네비게이션 아이콘에 클릭에 따라 화면이 변하게
    fun setBottomNavigationView() {
        activityMainBinding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(ShopFragmentName.SPLASH_FRAGMENT,false,false,null,false)
                    true
                }
                R.id.nav_barcode -> {
                    replaceFragment(ShopFragmentName.SPLASH_FRAGMENT,false,false,null,false)
                    true
                }
                R.id.nav_info -> {
                    replaceFragment(ShopFragmentName.SPLASH_FRAGMENT,false,false,null)
                    true
                }
                R.id.nav_like_list -> {
                    replaceFragment(ShopFragmentName.SPLASH_FRAGMENT,false,false,null)
                    true
                }
                else -> false
            }
        }
    }

    // 모든 스택 지우기
    fun clearBackStack() {
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    // 프래그먼트를 교체하는 함수
    fun replaceFragment(
        fragmentName: ShopFragmentName,
        isAddToBackStack: Boolean,
        animate: Boolean,
        dataBundle: Bundle?,
        isVisibleBottomNav: Boolean = true
    ) {
        // newFragment가 null이 아니라면 oldFragment 변수에 담아준다.
        if (newFragment != null) {
            oldFragment = newFragment
        }
        // 프래그먼트 객체
        newFragment = when (fragmentName) {
            // 로그인 화면
            ShopFragmentName.SPLASH_FRAGMENT -> SplashFragment()
        }

        // bundle 객체가 null이 아니라면
        if (dataBundle != null) {
            newFragment?.arguments = dataBundle
        }

        if (isVisibleBottomNav) {
            activityMainBinding.bottomNavigationView.visibility = View.VISIBLE
        } else {
            activityMainBinding.bottomNavigationView.visibility = View.GONE
        }

        // 프래그먼트 교체
        supportFragmentManager.commit {
            if (animate) {
                // 만약 이전 프래그먼트가 있다면
                if (oldFragment != null) {
                    oldFragment?.exitTransition =
                        MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                    oldFragment?.reenterTransition =
                        MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
                }

                newFragment?.exitTransition =
                    MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment?.reenterTransition =
                    MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
                newFragment?.enterTransition =
                    MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment?.returnTransition =
                    MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
            }

            replace(R.id.fragmentContainerView, newFragment!!)
            if (isAddToBackStack) {
                addToBackStack(fragmentName.str)
            }
        }
    }

    // 프래그먼트를 BackStack에서 제거하는 메서드
    fun removeFragment(fragmentName: ShopFragmentName) {
        supportFragmentManager.popBackStack(
            fragmentName.str,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    // drawerLayout 열기
    fun openDrawerLayout() {
        activityMainBinding.drawerLayoutMain.openDrawer(GravityCompat.END) // 드로어 열기
    }

    // 키보드 올리는 메서드
    fun showSoftInput(view: View) {
        // 입력을 관리하는 매니저
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // 포커스를 준다.
        view.requestFocus()

        thread {
            SystemClock.sleep(500)
            // 키보드를 올린다.
            inputManager.showSoftInput(view, 0)
        }
    }

    // 키보드를 내리는 메서드
    fun hideSoftInput() {
        // 포커스가 있는 뷰가 있다면
        if (currentFocus != null) {
            // 입력을 관리하는 매니저
            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            // 키보드를 내린다.
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            // 포커스를 해제한다.
            currentFocus?.clearFocus()
        }
    }

    // Activity에서 터치가 발생하면 호출되는 메서드
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 만약 포커스가 주어진 View가 있다면
        if (currentFocus != null) {
            // 현재 포커스가 주어진 View의 화면상의 영역 정보를 가져온다.
            val rect = Rect()
            currentFocus?.getGlobalVisibleRect(rect)
            // 현재 터치 지점이 포커스를 가지고 있는 View의 영역 내부가 아니라면
            if (rect.contains(ev?.x?.toInt()!!, ev?.y?.toInt()!!) == false) {
                // 키보드를 내리고 포커스를 제거한다.
                hideSoftInput()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}