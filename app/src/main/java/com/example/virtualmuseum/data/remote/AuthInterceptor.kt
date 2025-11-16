package com.example.virtualmuseum.data.remote

import com.example.virtualmuseum.data.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor { // <-- XÓA (tokenManager: TokenManager)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Lấy token trực tiếp từ object TokenManager
        TokenManager.getToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}