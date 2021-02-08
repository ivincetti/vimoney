package ru.vincetti.modules.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.vincetti.modules.network.models.ConfigFile
import javax.inject.Inject

class ConfigLoaderImpl @Inject constructor() : ConfigLoader {

    companion object {
        private const val URL = "https://vincetti.ru/vimoney/"
        private const val RU_LOCALE = "Ru"
    }

    private val jsonDownloader by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JsonDownloader::class.java)
    }

    override suspend fun loadPreferences(): ConfigFile {
        return jsonDownloader.loadPreferences(RU_LOCALE)
    }
}
