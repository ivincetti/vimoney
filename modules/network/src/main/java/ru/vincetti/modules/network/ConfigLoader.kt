package ru.vincetti.modules.network

import ru.vincetti.modules.network.models.ConfigFile

interface ConfigLoader {

    suspend fun loadPreferences(): ConfigFile
}
