package com.example.data.datasource.remote

import com.example.data.datasource.RecommendDataSource
import com.example.data.model.request.recommend.RequestRecommendKakaoDto
import com.example.data.model.response.BaseResponse
import com.example.data.model.response.recommend.ResponseRecommendAddDto
import com.example.data.model.response.recommend.ResponseRecommendDto
import com.example.data.remote.service.RecommendService
import javax.inject.Inject

class RecommendDataSourceImpl @Inject constructor(
    private val recommendService: RecommendService
) : RecommendDataSource {

    override suspend fun postToGetKakaoListData(
        page: Int,
        request: RequestRecommendKakaoDto
    ): BaseResponse<List<ResponseRecommendDto>> {
        return recommendService.postToGetKakaoList(page, request)
    }

    override suspend fun getSchoolListData(
        page: Int
    ): BaseResponse<List<ResponseRecommendDto>> {
        return recommendService.getSchoolList(page)
    }

    override suspend fun postFriendAdd(
        friendId: Long
    ): ResponseRecommendAddDto {
        return recommendService.postFriendAdd(friendId)
    }

}