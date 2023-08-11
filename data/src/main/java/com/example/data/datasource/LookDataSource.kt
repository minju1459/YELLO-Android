package com.example.data.datasource

import com.example.data.model.response.look.ResponseLookListDto

interface LookDataSource {

    suspend fun getLookListData(
        page: Int
    ): ResponseLookListDto

}