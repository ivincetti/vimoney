package ru.vincetti.modules.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.vincetti.modules.network.models.ConfigFile

interface JsonDownloader {

    @GET("config.json")
    suspend fun loadPreferences(@Query("q") locale: String): ConfigFile
}
