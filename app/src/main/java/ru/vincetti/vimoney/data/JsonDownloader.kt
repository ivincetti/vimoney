package ru.vincetti.vimoney.data

import retrofit2.http.GET
import retrofit2.http.Query
import ru.vincetti.vimoney.data.models.json.ConfigFile

interface JsonDownloader {

    @GET("config.json")
    suspend fun loadPreferences(@Query("q") locale: String): ConfigFile
}
