package com.ts.tawkexam.di.network

import android.content.Context
import com.google.gson.Gson
import com.ts.tawkexam.utils.BASE_URL
import com.ts.tawkexam.utils.GITHUB_CLIENT_ID
import com.ts.tawkexam.utils.GITHUB_CLIENT_SECRET
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@Module
class NetworkModule {

    @Provides
    fun providesRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(rxJava2CallAdapterFactory)
            .client(okHttpClient)
            .build()
    }


    @Provides
    fun providesOkHttpClient( interceptor: Interceptor, httpLoggingInterceptor: HttpLoggingInterceptor, cache: Cache, dispatcher: Dispatcher): OkHttpClient {
        val client = OkHttpClient.Builder()
            .cache(null)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor)
            .dispatcher(dispatcher)
        return client.build()
    }

    @Provides
//    @AppControllerScope
    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun getInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
                .header("Authorization", Credentials.basic(GITHUB_CLIENT_ID, GITHUB_CLIENT_SECRET))
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    fun getDispatcher(): Dispatcher {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1
        dispatcher.maxRequestsPerHost = 1
        return dispatcher;
    }


    @Provides
    fun providesOkhttpCache(context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    @Provides
    fun providesGson(): Gson {
        return Gson()
    }

    @Provides
    fun providesGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun providesRxJavaCallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }
}

