package com.aladin.finalproject_shoppingmallservice_4_team

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ActivityMainBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.main.MainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.hideSoftInput
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    // 뒤로 가기 버튼 처리에 필요한 변수 선언
    private var backPressedTime: Long = 0

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

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let { event ->
            // 포커스가 있는 View가 있다면
            currentFocus?.let { focusedView ->
                val rect = Rect()
                // 포커스가 있는 View의 화면상의 위치를 rect에 저장
                focusedView.getGlobalVisibleRect(rect)

                // 터치 지점이 포커스가 있는 View의 영역 내부가 아니라면
                if (!rect.contains(event.x.toInt(), event.y.toInt())) {
                    // 키보드 내리기
                    val inputManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
                    // 포커스 해제
                    focusedView.clearFocus()
                }
            }
        }
        // 기본 터치 이벤트 처리
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()
        // OnBackPressedDispatcher를 사용하여 뒤로 가기 버튼 처리
        onBackPressedDispatcher.addCallback(this) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                // 스택이 있을 경우, 스택 제거
                supportFragmentManager.popBackStack()
                Log.e("backStack","backStack")
            } else {
                // 스택이 비어 있을 경우
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime <= 3000) {
                    // 3초안에 또 누를시 앱 종료
                    exitProcess(0)
                } else {
                    backPressedTime = currentTime
                    Toast.makeText(this@MainActivity, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}