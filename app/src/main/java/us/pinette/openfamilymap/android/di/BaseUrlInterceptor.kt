package us.pinette.openfamilymap.android.di

import android.content.SharedPreferences
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor(
    private val prefs: SharedPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val baseUrl = prefs.getString("base_url", "https://example.com")!!

        val newUrl = chain.request().url.newBuilder()
            .scheme(baseUrl.toHttpUrlOrNull()!!.scheme)
            .host(baseUrl.toHttpUrlOrNull()!!.host)
            .port(baseUrl.toHttpUrlOrNull()!!.port)
            .build()

        val newRequest = chain.request().newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
