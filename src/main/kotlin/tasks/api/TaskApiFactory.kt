package tasks.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TaskApiFactory {

    fun create(baseUrl: String): TaskApi =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskApi::class.java)
}
