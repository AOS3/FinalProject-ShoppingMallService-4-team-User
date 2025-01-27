package com.aladin.finalproject_shoppingmallservice_4_team.ui.orderinquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentOrderInquiryDetailBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.ask.AskFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.toCommaString
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class OrderInquiryDetailFragment : Fragment() {

    private var _binding: FragmentOrderInquiryDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderInquiryDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarButton()
        askButton()
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    버튼
     */

    private fun askButton() {
        binding.buttonOrderInquiryAsk.setOnClickListener {
            replaceMainFragment(AskFragment(), true)
        }
    }

    /*
    툴바
     */

    private fun toolbarButton() {
        toolbarBackButton()
    }

    private fun toolbarBackButton() = binding.materialToolbarOrderInquiryDetail.setNavigationOnClickListener { removeFragment() }

    /*
    데이터
     */

    private fun loadData() {
        binding.apply {
            textFieldOrderInquiryDetailName.editText!!.setText(arguments?.getString("userName")!!)
            textFieldOrderInquiryDetailPhoneNumber.editText!!.setText(arguments?.getString("userPhone")!!)
            val userAddress = arguments?.getString("userAddress")!!.split("/")
            textFieldOrderInquiryDetailPostCode.editText!!.setText(userAddress[0])
            textFieldOrderInquiryDetailRoadNameAddress.editText!!.setText(userAddress[1])
            textFieldOrderInquiryDetailDetailAddress.editText!!.setText(userAddress[2])
            textViewOrderInquiryDetailTitle.text = arguments?.getString("bookName")!!
            textViewOrderInquiryDetailWriter.text = arguments?.getString("bookWriter")!!
            textViewOrderInquiryDetailQuality.text = when(arguments?.getInt("bookQuality")!!) {
                0 -> "품질 : 상"
                1 -> "품질 : 중"
                else -> "품질 : 하"
            }
            textViewOrderInquiryDetailTotalSize.text = "총 수량 ${arguments?.getInt("bookTotal")!!}개"
            textViewOrderInquiryDetailTotalPrice.text = "총 구매 가격 : ${arguments?.getInt("bookPrice")!!.toCommaString()}원"
            textViewOrderInquiryDetailOrderNumber.text = "주문 번호 : ${arguments?.getString("bookNumber")!!}"
            textViewOrderInquiryDetailOrderDelivery.text = when(arguments?.getInt("bookDelivery")!!) {
                0 -> "배송전"
                1 -> "배송중"
                else -> "배송 완료"
            }
            textViewOrderInquiryDetailOrderTime.text = showDateData(arguments?.getLong("buyTime")!!)
        }
    }

    // 날짜 보여주는 메서드
    private fun showDateData(timeData: Long): String {
        val dataFormat1 = SimpleDateFormat("yyyy-MM-dd. HH:mm:ss")
        val date = dataFormat1.format(timeData)
        return date
    }
}