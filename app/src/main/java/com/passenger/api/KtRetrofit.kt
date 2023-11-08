package updata.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object KtRetrofit {
    private val mOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.MINUTES)
        .writeTimeout(60, TimeUnit.MINUTES)
        .callTimeout(60, TimeUnit.MINUTES)
        .readTimeout(60, TimeUnit.MINUTES)
        .retryOnConnectionFailure(true)
        .followRedirects(false)//?
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
    private var mRetrofit: Retrofit? = null
    private var mRetrofitBuilder = Retrofit.Builder()
//        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
//        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .client(mOkHttpClient)
    fun initConfig(baseUrl: String): KtRetrofit {
        mRetrofit = mRetrofitBuilder.baseUrl(baseUrl).client(mOkHttpClient).build()
        return this
    }

    fun <T> getService(baseClass: Class<T>): T {
        if (mRetrofit == null) {
            throw UninitializedPropertyAccessException("Retrofit null")
        } else {
            return mRetrofit!!.create(baseClass)
        }
    }
}