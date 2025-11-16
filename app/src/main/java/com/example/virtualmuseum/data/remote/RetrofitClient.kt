package com.example.virtualmuseum.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // THAY ĐỔI QUAN TRỌNG: Dùng 10.0.2.2 thay cho localhost
//    private const val BASE_URL = "http://172.11.47.158:5000/"
    private const val BASE_URL = "http://10.0.2.2:5000/"

    // Interceptor để log các request và response ra Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor())
        .build()

    val instance: MuseumApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MuseumApiService::class.java)
    }
}