package com.el.yello.presentation.main.recommend.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.el.yello.R
import com.el.yello.databinding.FragmentRecommendItemBottomSheetBinding
import com.el.yello.presentation.main.profile.ProfileViewModel
import com.el.yello.presentation.main.recommend.kakao.RecommendKakaoViewModel
import com.example.ui.base.BindingBottomSheetDialog
import com.example.ui.view.setOnSingleClickListener

class RecommendFriendItemBottomSheet :
    BindingBottomSheetDialog<FragmentRecommendItemBottomSheetBinding>(R.layout.fragment_recommend_item_bottom_sheet) {

    private val viewModel by activityViewModels<RecommendKakaoViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        setItemImage()
        initDeleteBtnListener()
    }

    private fun setItemImage() {
        if (viewModel.clickedUserData.profileImageUrl == ProfileViewModel.BASIC_THUMBNAIL) {
            binding.ivRecommendFriendThumbnail.load(R.drawable.img_yello_basic)
        } else {
            binding.ivRecommendFriendThumbnail.load(viewModel.clickedUserData.profileImageUrl) {
                transformations(CircleCropTransformation())
            }
        }
    }

    private fun initDeleteBtnListener() {
        binding.btnRecommendFriendDelete.setOnSingleClickListener {
            // TODO : 체크 표시 전환 후 dismiss ,
            //  viewModel.deleteFriendDataToServer
            dismiss()
        }
    }
}
