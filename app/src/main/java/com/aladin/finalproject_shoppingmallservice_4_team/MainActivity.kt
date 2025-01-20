package com.aladin.finalproject_shoppingmallservice_4_team

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ActivityMainBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.main.MainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.hideSoftInput
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

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
}