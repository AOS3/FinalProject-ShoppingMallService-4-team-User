package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookDetailBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BookDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBookDetailBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookDetailBottomSheetBinding.inflate(inflater, container, false)

        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("test", arguments?.getString("bookIsbn")!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance(isbn: String, price: Int): BookDetailBottomSheetFragment {
            val bundle = Bundle().apply {
                putString("bookIsbn", isbn)
                putInt("bookPrice", price)
            }
            return BookDetailBottomSheetFragment().apply {
                arguments = bundle
            }
        }
    }
}

