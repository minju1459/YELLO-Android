package com.el.yello.presentation.tutorial

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.el.yello.R
import com.el.yello.databinding.ActivityTutorialABinding
import com.example.ui.base.BindingActivity
import com.example.ui.view.setOnSingleClickListener

class TutorialAActivity : BindingActivity<ActivityTutorialABinding>(R.layout.activity_tutorial_a) {

    private val viewModel by viewModels<TutorialViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root.setOnSingleClickListener {
            viewModel.isCodeTextEmpty = intent.getBooleanExtra("codeTextEmpty", false)
            val intent = Intent(this@TutorialAActivity, TutorialBActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
