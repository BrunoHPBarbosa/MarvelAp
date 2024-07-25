package com.example.marvelapi.di

import android.content.Context
import androidx.room.Room
import com.example.marvelapi.data.local.MarvelDataBase
import com.example.marvelapi.data.remote.ServiceApi
import com.example.marvelapi.util.Constants
import com.example.marvelapi.util.Constants.BASE_URL
import com.example.marvelapi.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Singleton
    @Provides
    fun providMarvelDataBase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        MarvelDataBase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideMarvelDao(dataBase: MarvelDataBase) = dataBase.marvelDao()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient().newBuilder()
            .addInterceptor { chain ->
                val currentTimeStamp = System.currentTimeMillis()
                val newUrl = chain.request().url
                    .newBuilder()
                    .addQueryParameter(Constants.TS,currentTimeStamp.toString())
                    .addQueryParameter(Constants.APIKEY,Constants.PUBLIC_KEY)
                    .addQueryParameter(Constants.HASH,
                        provideToMd5Hash(
                            currentTimeStamp.toString() + Constants.PRIVATE_KEY + Constants.PUBLIC_KEY)
                    )
                    .build()

                val newRequest = chain.request()
                    .newBuilder()
                    .url(newUrl)
                    .build()
                    chain.proceed(newRequest)
            }
            .addInterceptor(logging)
            .connectTimeout(100,TimeUnit.SECONDS)
            .readTimeout(100,TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun provideServiceApi(retrofit: Retrofit): ServiceApi {
        return retrofit.create(ServiceApi::class.java)
    }

    @Singleton
    @Provides
    fun provideToMd5Hash(encrypted: String):String{
        var pass = encrypted
        var encryptedString:String? = null
        val md5:MessageDigest
        try{
            md5 = MessageDigest.getInstance("MD5")
            md5.update(pass.toByteArray(),0,pass.length)
            pass =BigInteger(1,md5.digest()).toString(16)
            while(pass.length <32){
            pass = "0$pass"
            }
            encryptedString = pass

    }catch (e1: NoSuchAlgorithmException){
        e1.printStackTrace()

        }
        Timber.d("hash -> $encryptedString")
        return encryptedString ?: ""
    }
}