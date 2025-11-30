package us.pinette.openfamilymap.android.di

import android.content.SharedPreferences
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor(
    private val prefs: SharedPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val baseUrl = prefs.getString(API_BASE_URL_PREF, "https://example.com")!!
            .ifEmpty { "https://example.com" }
            .toHttpUrlOrNull()!!

        val newUrl = chain.request().url.newBuilder()
            .scheme(baseUrl.scheme)
            .host(baseUrl.host)
            .port(baseUrl.port)
            .build()

        val newRequest = chain.request().newBuilder()
            .url(newUrl)
            .header("Authorization", "Bearer ${prefs.getString("accessToken", "")}")
            .build()

        return chain.proceed(newRequest)
    }

    companion object {
        const val API_BASE_URL_PREF = "api_base_url"
    }
}
