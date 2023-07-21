package com.el.yello.presentation.onboarding.fragment.studentid.dialog.studentid

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.el.yello.R
import com.el.yello.databinding.FragmentDialogStudentIdBinding
import com.el.yello.presentation.onboarding.activity.OnBoardingViewModel
import com.example.ui.base.BindingBottomSheetDialog

class StudentidDialogFragment :
    BindingBottomSheetDialog<FragmentDialogStudentIdBinding>(R.layout.fragment_dialog_student_id) {

    private lateinit var idList: List<Int>

    private val viewModel by activityViewModels<OnBoardingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initStudentidAdapter()
    }

    private fun initStudentidAdapter() {
        viewModel.addStudentId()
        idList = viewModel.studentIdResult.value ?: emptyList()
        val adapter = StudentidDialogAdapter(storeStudentId = ::storeStudentId)
        binding.rvStudentid.adapter = adapter
        adapter.submitList(idList)
    }

    fun storeStudentId(studentId: Int) {
        viewModel.setStudentId(studentId)
        dismiss()
    }
    companion object {
        @JvmStatic
        fun newInstance() = StudentidDialogFragment()
    }
}