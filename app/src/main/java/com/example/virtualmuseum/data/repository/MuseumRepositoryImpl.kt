package com.example.virtualmuseum.data.repository

import com.example.virtualmuseum.data.remote.MuseumApiService
import com.example.virtualmuseum.data.remote.dto.*
import com.example.virtualmuseum.domain.repository.MuseumRepository
import com.example.virtualmuseum.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class MuseumRepositoryImpl (
    private val apiService: MuseumApiService
) : MuseumRepository {

    override fun login(request: LoginRequest): Flow<Resource<LoginResponseData>> = flow {
        // 1. Phát ra trạng thái Loading
        emit(Resource.Loading())

        try {
            // 2. Gọi API
            val response = apiService.login(request)
            if (response.success && response.data != null) {
                // 3a. Nếu thành công, phát ra trạng thái Success cùng với dữ liệu
                emit(Resource.Success(response.data))
            } else {
                // 3b. Nếu API trả về lỗi (success = false), phát ra trạng thái Error
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            // 4a. Bắt lỗi HTTP (ví dụ: 404 Not Found, 500 Server Error)
            emit(Resource.Error(e.localizedMessage ?: "Lỗi HTTP không xác định"))
        } catch (e: IOException) {
            // 4b. Bắt lỗi kết nối mạng (ví dụ: không có internet)
            emit(Resource.Error("Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng."))
        }
    }

    override fun register(request: RegisterRequest): Flow<Resource<UserDto>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.register(request)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi HTTP"))
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng"))
        }
    }

    override fun searchFossils(request: SearchRequest): Flow<Resource<SearchResponse>> = flow {
        emit(Resource.Loading())
        try {
            // API này trả về SearchResponse trực tiếp, không phải ApiResponse
            val response = apiService.searchFossils(request)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi HTTP không xác định"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ."))
        }
    }

    override fun getFossilDetail(fossilId: String, language: String): Flow<Resource<FossilDetailData>> = flow {
        emit(Resource.Loading())
        try {
            // PASS language to the apiService call
            val response = apiService.getFossilDetail(fossilId = fossilId, language = language)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Fossil not found")) // More specific error?
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi HTTP"))
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng"))
        }
    }

    override fun getComments(fossilId: String, language: String): Flow<Resource<List<CommentDto>>> = flow {
        emit(Resource.Loading())
        try {
            // No change needed inside, apiService.getComments now expects String
            val response = apiService.getComments(fossilId = fossilId, language = language)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Could not load comments")) // Improved error message
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi HTTP khi tải bình luận"))
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng khi tải bình luận"))
        }
    }

    override fun createComment(request: CreateCommentRequest): Flow<Resource<CommentDto>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.createComment(request)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi HTTP"))
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng"))
        }
    }

    override fun deleteComment(request: DeleteCommentRequest): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.deleteComment(request)
            if (response.success) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi HTTP"))
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng"))
        }
    }

    override fun addFavorite(request: FavoriteRequest): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.addFavorite(request)
            if (response.success) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi HTTP"))
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng"))
        }
    }

    override fun removeFavorite(request: FavoriteRequest): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.removeFavorite(request)
            if (response.success) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi HTTP"))
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng"))
        }
    }

    override fun addOrUpdateReaction(request: AddReactionRequest): Flow<Resource<ReactionDto>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.addOrUpdateReaction(request)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi HTTP"))
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng"))
        }
    }

    override fun deleteReaction(request: DeleteReactionRequest): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.deleteReaction(request)
            if (response.success) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi HTTP"))
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng"))
        }
    }

    override fun changePassword(request: ChangePasswordRequest): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.changePassword(request)
            if (response.success) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) { /* ... catch lỗi ... */ }
    }

    override fun getFavorites(language: String): Flow<Resource<List<FavoriteFossilDto>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getFavorites(language = language)
            if (response.success) {
                emit(Resource.Success(response.data ?: emptyList()))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) { /* ... */ }
    }

    override fun getCommentHistory(): Flow<Resource<List<CommentHistoryDto>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getCommentHistory()
            if (response.success) {
                emit(Resource.Success(response.data ?: emptyList()))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) { /* ... */ }
    }

    override fun getNews(language: String): Flow<Resource<List<NewsDto>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getNews(language)
            if ((response.success || response.message == "success") && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.localizedMessage}"))
        }
    }
}