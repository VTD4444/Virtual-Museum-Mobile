// In file: ui/components/WebViewComponent.kt
package com.example.virtualmuseum.ui.components

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat

@SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
@Composable
fun WebViewComponent(
    modifier: Modifier = Modifier,
    modelPath: String // Nhận đường dẫn model (ví dụ: "/models/t-rex.glb")
) {
    key(modelPath) {
        AndroidView(
            modifier = modifier,
            factory = { context ->

                // 1. Tạo AssetLoader
                val assetLoader = WebViewAssetLoader.Builder()
                    // Ánh xạ tên miền ảo "/assets/" vào thư mục "assets" vật lý
                    .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
                    .setDomain("appassets.androidplatform.net") // Tên miền ảo
                    .setHttpAllowed(false) // Chỉ cho phép https
                    .build()

                // 2. Tạo WebView
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // 3. Sử dụng WebViewClientCompat để chặn request
                    webViewClient = object : WebViewClientCompat() {
                        override fun shouldInterceptRequest(
                            view: WebView,
                            request: WebResourceRequest
                        ): WebResourceResponse? {
                            // Chuyển hướng các request đến tên miền ảo
                            // cho AssetLoader xử lý
                            return assetLoader.shouldInterceptRequest(request.url)
                        }
                    }

                    // --- CÀI ĐẶT WEBVIEW ---
                    settings.javaScriptEnabled = true
                    // Các cài đặt này vẫn cần thiết
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.domStorageEnabled = true
                    setBackgroundColor(Color.TRANSPARENT)

                    // --- LOGIC TẢI MỚI ---
                    // Xử lý đường dẫn (xóa dấu / nếu có)
                    val cleanModelPath = modelPath.removePrefix("/")

                    // 4. Tải file index.html từ TÊN MIỀN ẢO
                    val urlToLoad = Uri.Builder()
                        .scheme("https")
                        .authority("appassets.androidplatform.net")
                        .appendPath("assets")
                        .appendPath("index.html")
                        .appendQueryParameter("model", cleanModelPath) // Truyền tên model qua query
                        .build()

                    loadUrl(urlToLoad.toString())

                    // --- (Giữ nguyên setOnTouchListener) ---
                    setOnTouchListener { _, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                parent.requestDisallowInterceptTouchEvent(true)
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                parent.requestDisallowInterceptTouchEvent(false)
                            }
                        }
                        onTouchEvent(event)
                    }
                }
            }
        )
    }
}