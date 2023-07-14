package com.yello.presentation.onboarding.fragment.nameid

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.ui.base.BindingFragment
import com.example.ui.view.setOnSingleClickListener
import com.yello.R
import com.yello.databinding.FragmentNameIdBinding
import com.yello.presentation.onboarding.activity.OnBoardingViewModel

class NameIdFragment : BindingFragment<FragmentNameIdBinding>(R.layout.fragment_name_id) {

    private val viewModel by activityViewModels<OnBoardingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        setConfirmBtnClickListener()
    }

    private fun setConfirmBtnClickListener() {
        binding.btnNameidNext.setOnSingleClickListener {
            viewModel.navigateToNextPage()
        }
    }
}

