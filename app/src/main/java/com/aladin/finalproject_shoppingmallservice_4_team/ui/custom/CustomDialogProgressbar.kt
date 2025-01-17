package com.aladin.finalproject_shoppingmallservice_4_team.ui.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.DialogProgressbarBinding

class CustomDialogProgressbar(
    context: Context,
) : Dialog(context) {
    private lateinit var binding: DialogProgressbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogProgressbarBinding.inflate(LayoutInflater.from(context))

        initViews()
    }

    private fun initViews() = with(binding) {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
        setCancelable(false)
    }
}