package com.example.virtualmuseum.domain.repository

import com.example.virtualmuseum.data.remote.dto.*
import com.example.virtualmuseum.utils.Resource
import kotlinx.coroutines.flow.Flow
import com.example.virtualmuseum.data.remote.dto.DeleteCommentRequest

/**
 * Interface định nghĩa các phương thức để lấy dữ liệu cho ứng dụng.
 */
interface MuseumRepository {

    fun login(request: LoginRequest): Flow<Resource<LoginResponseData>>
    fun register(request: RegisterRequest): Flow<Resource<UserDto>>
    fun searchFossils(request: SearchRequest): Flow<Resource<SearchResponse>>
    fun getFossilDetail(fossilId: String, language: String): Flow<Resource<FossilDetailData>>
    fun getComments(fossilId: String, language: String): Flow<Resource<List<CommentDto>>>
    fun createComment(request: CreateCommentRequest): Flow<Resource<CommentDto>>
    fun addFavorite(request: FavoriteRequest): Flow<Resource<Unit>>
    fun removeFavorite(request: FavoriteRequest): Flow<Resource<Unit>>
    fun deleteComment(request: DeleteCommentRequest): Flow<Resource<Unit>>
    fun addOrUpdateReaction(request: AddReactionRequest): Flow<Resource<ReactionDto>>
    fun deleteReaction(request: DeleteReactionRequest): Flow<Resource<Unit>>
}