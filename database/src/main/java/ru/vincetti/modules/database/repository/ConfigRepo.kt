package ru.vincetti.modules.database.repository

import ru.vincetti.modules.core.models.Config
import ru.vincetti.modules.database.sqlite.ConfigDao
import ru.vincetti.modules.database.sqlite.models.ConfigModel
import javax.inject.Inject

class ConfigRepo @Inject constructor(
    private val configDao: ConfigDao
) {

    companion object {
        private const val CONFIG_KEY_NAME_DATE_EDIT = "date_edit"
    }

    suspend fun loadByKey(): Config? {
        return configDao.loadConfigByKey(CONFIG_KEY_NAME_DATE_EDIT)?.toConfig()
    }

    suspend fun add(timeStamp: String) {
        configDao.insertConfig(
            ConfigModel(
                keyName = CONFIG_KEY_NAME_DATE_EDIT,
                value = timeStamp
            )
        )
    }

    suspend fun update(conf: Config) {
        configDao.updateConfig(ConfigModel.from(conf))
    }
}
